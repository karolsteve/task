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

import com.steve.task.condition.TaskCondition;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public class TaskParams implements Serializable {
    private List<TaskCondition> conditions;
    private boolean             isPersistent;
    private int                 retryCount;
    private String              groupId;
    private String              id;
    private boolean             wakeLock;
    private long                wakeLockTimeOut;

    private TaskParams(List<TaskCondition> conditions, boolean isPersistent, int retryCount,
                       String groupId, String id, boolean wakeLock, long wakeLockTimeOut) {
        this.conditions = conditions;
        this.isPersistent = isPersistent;
        this.retryCount = retryCount;
        this.groupId = groupId;
        this.id = id;
        this.wakeLock = wakeLock;
        this.wakeLockTimeOut = wakeLockTimeOut;
    }

    public static Builder builder() {
        return new Builder();
    }

    List<TaskCondition> getConditions() {
        return conditions;
    }

    boolean isPersistent() {
        return isPersistent;
    }

    int getRetryCount() {
        return retryCount;
    }

    String getGroupId() {
        return groupId;
    }

    public String getId() {
        return id;
    }

    boolean isWakeLock() {
        return wakeLock;
    }

    long getWakeLockTimeOut() {
        return wakeLockTimeOut;
    }

    public static class Builder {
        private List<TaskCondition> conditions      = new LinkedList<>();
        private boolean             isPersistent    = false;
        private int                 retryCount      = 50;
        private String              groupId         = null;
        private String              id              = null;
        private boolean             wakeLock        = false;
        private long                wakeLockTimeOut = 0;

        public Builder addCondition(TaskCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder isPersistent() {
            this.isPersistent = true;
            return this;
        }

        public Builder setRetryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder withWakeLock(boolean isWakeLockRequired, long timeOut, TimeUnit timeUnit) {
            this.wakeLock = isWakeLockRequired;
            this.wakeLockTimeOut = timeUnit.toMillis(timeOut);
            return this;
        }

        public Builder withWakeLock(boolean isWakeLockRequired) {
            return withWakeLock(isWakeLockRequired, 0, TimeUnit.SECONDS);
        }

        public TaskParams build() {
            return new TaskParams(conditions, isPersistent, retryCount, groupId, id, wakeLock, wakeLockTimeOut);
        }
    }
}
