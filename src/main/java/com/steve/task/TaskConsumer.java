/*
 * Copyright 2018 Steve Tchatchouang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
