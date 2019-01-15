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
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import com.steve.task.condition.TaskCondition;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public abstract class Task implements Serializable {

    private final TaskParams params;

    private boolean mIsStopped = false;

    private transient long                  storageId;
    private transient int                   runIteration;
    private transient PowerManager.WakeLock wakeLock;

    public Task(TaskParams params) {
        this.params = params;
    }

    public List<TaskCondition> getConditions() {
        return params.getConditions();
    }

    public boolean isPersistent() {
        return params.isPersistent();
    }

    public int getRetryCount() {
        return params.getRetryCount();
    }

    public int getRunIteration() {
        return runIteration;
    }

    public boolean needsWakeLock() {
        return params.isWakeLock();
    }

    public PowerManager.WakeLock getWakeLock() {
        return wakeLock;
    }

    public void setWakeLock(PowerManager.WakeLock wakeLock) {
        this.wakeLock = wakeLock;
    }

    public void setStorageId(long storageId) {
        this.storageId = storageId;
    }

    public long getStorageId() {
        return storageId;
    }

    public String getId(){
        return params.getId();
    }

    public void setContext(Context context) {
        for (TaskCondition condition : getConditions()) {
            if (condition instanceof ContextRequired) {
                ((ContextRequired) condition).setContext(context);
            }
        }
    }

    public abstract void onTaskAddedToQueue();

    public abstract void onTaskExecute() throws Exception;

    public abstract boolean shouldRetry(Exception e);

    public abstract void onTaskCancelled();

    public String getGroupId() {
        return params.getGroupId();
    }

    public boolean isAllConditionsOK() {
        for (TaskCondition condition : getConditions()) {
            if (!condition.isOk()) return false;
        }
        return true;
    }

    public void setRunIteration(int runIteration) {
        this.runIteration = runIteration;
    }

    public long getWakeLockTimeOut(){
        return params.getWakeLockTimeOut();
    }

    void stop() {
        Log.d("Task", "task with id "+getId()+" stopped");
        mIsStopped = true;
        onStop();
    }

    public boolean isStopped() {
        return mIsStopped;
    }

    public void onStop(){ }
}
