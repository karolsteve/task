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

import android.content.Context;
import android.os.PowerManager;

import com.steve.task.condition.TaskConditionListener;
import com.steve.task.condition.TaskConditionProvider;
import com.steve.task.storage.TaskSerializer;
import com.steve.task.storage.TaskStorage;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public class TaskManager implements TaskConditionListener {

    private final TaskQueue taskQueue     = new TaskQueue();
    private final Executor  eventExecutor = Executors.newSingleThreadExecutor();

    private Context     context;
    private TaskStorage storage;

    private TaskManager(Context context, String name, int consumers, TaskSerializer<Task> serializer,
                        List<TaskConditionProvider> conditionProviders) {

        this.context = context;
        this.storage = new TaskStorage(context, name, serializer);
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskQueue.addAll(storage.getAllTasks());
            }
        });

        if (conditionProviders != null && !conditionProviders.isEmpty()) {
            for (TaskConditionProvider provider : conditionProviders) {
                provider.setListener(this);
            }
        }

        for (int i = 0; i < consumers; i++) {
            new TaskConsumer("Consumer-" + i, taskQueue, storage).start();
        }
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public void stopTaskWithId(final String taskId) {
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (Task task : TaskConsumer.currents) {
                    if (task.getId() != null && task.getId().equals(taskId)) {
                        task.stop();
                        if (task.isPersistent()) {
                            storage.remove(task.getStorageId());
                        }
                    }
                }
                for (Task task : TaskManager.this.taskQueue.taskQueue) {
                    if (task.getId() != null && task.getId().equals(taskId)) {
                        TaskManager.this.taskQueue.taskQueue.remove(task);
                        task.stop();
                        if (task.isPersistent()) {
                            storage.remove(task.getStorageId());
                        }
                    }
                }
            }
        });
    }

    public void addTaskToQueue(final Task task) {
        if (task.needsWakeLock()) {
            task.setWakeLock(acquireWakeLock(context, task.toString(), task.getWakeLockTimeOut()));
        }

        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (task.isPersistent()) {
                        storage.persist(task);
                    }
                    task.setContext(context);
                    task.onTaskAddedToQueue();
                    taskQueue.add(task);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private PowerManager.WakeLock acquireWakeLock(Context context, String tag, long timeOut) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
        if (timeOut == 0) wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        else wakeLock.acquire(timeOut);
        return wakeLock;
    }

    @Override
    public void onConditionChanged() {
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskQueue.onConditionChanged();
            }
        });
    }

    public static class Builder {
        private Context                     context;
        private String                      name;
        private int                         consumers;
        private TaskSerializer<Task>        serializer;
        private List<TaskConditionProvider> conditionProviders;

        public Builder(Context context) {
            this.context = context;
            this.consumers = 3;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setConsumers(int consumers) {
            this.consumers = consumers;
            return this;
        }

        public Builder setSerializer(TaskSerializer<Task> serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder setConditions(TaskConditionProvider... providers) {
            this.conditionProviders = Arrays.asList(providers);
            return this;
        }

        public TaskManager build() {
            if (name == null) name = "Default";
            if (conditionProviders == null) conditionProviders = new LinkedList<>();
            return new TaskManager(context, name, consumers, serializer, conditionProviders);
        }
    }
}
