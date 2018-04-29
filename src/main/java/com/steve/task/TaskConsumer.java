package com.steve.task;

import android.util.Log;

import com.steve.task.storage.TaskStorage;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

class TaskConsumer extends Thread {


    private static final String TAG = TaskConsumer.class.getSimpleName();

    private final TaskQueue taskQueue;
    private final TaskStorage storage;

    TaskConsumer(String name, TaskQueue taskQueue, TaskStorage storage) {
        super(name);
        this.taskQueue = taskQueue;
        this.storage = storage;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            Task task = taskQueue.getNextTask();
            AdwaTaskResult result = runTask(task);
            if (result == AdwaTaskResult.DEFERRED) {
                taskQueue.push(task);
            } else {
                if (result == AdwaTaskResult.FAILURE) {
                    task.onTaskCancelled();
                }
                if (task.isPersistent()) {
                    storage.remove(task.getStorageId());
                }

                if(task.getWakeLock() != null && task.getWakeLockTimeOut() == 0){
                    task.getWakeLock().release();
                }
            }
            if(task.getGroupId() != null){
                taskQueue.setGroupIdAvailable(task.getGroupId());
            }
        }
    }

    private AdwaTaskResult runTask(Task task) {
        int retryCount = task.getRetryCount();
        int runIteration = task.getRunIteration();
        for (; runIteration < retryCount; runIteration++) {
            try {
                task.onTaskExecute();
                return AdwaTaskResult.SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "runTask: ", e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (!task.shouldRetry(e)) {
                    return AdwaTaskResult.FAILURE;
                } else if (!task.isAllConditionsOK()) {
                    task.setRunIteration(runIteration + 1);
                    return AdwaTaskResult.DEFERRED;
                }
            }
        }
        return AdwaTaskResult.FAILURE;
    }

    enum AdwaTaskResult {
        /**
         * En cas de succès
         */
        SUCCESS,
        /**
         * En cas d'échec
         */
        FAILURE,
        /**
         * tache Renvoyé à un autre temps
         */
        DEFERRED
    }
}
