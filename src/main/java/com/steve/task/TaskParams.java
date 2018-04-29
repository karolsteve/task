package com.steve.task;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.steve.task.condition.TaskCondition;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public class TaskParams implements Serializable {
    private List<TaskCondition> conditions;
    private boolean                 isPersistent;
    private int                     retryCount;
    private String                  groupId;
    private boolean                 wakeLock;
    private long                    wakeLockTimeOut;

    private TaskParams(List<TaskCondition> conditions, boolean isPersistent, int retryCount,
                       String groupId, boolean wakeLock, long wakeLockTimeOut) {
        this.conditions = conditions;
        this.isPersistent = isPersistent;
        this.retryCount = retryCount;
        this.groupId = groupId;
        this.wakeLock = wakeLock;
        this.wakeLockTimeOut = wakeLockTimeOut;
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

    boolean isWakeLock() {
        return wakeLock;
    }

    long getWakeLockTimeOut() {
        return wakeLockTimeOut;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<TaskCondition> conditions      = new LinkedList<>();
        private boolean                 isPersistent    = false;
        private int                     retryCount      = 50;
        private String                  groupId         = null;
        private boolean                 wakeLock        = false;
        private long                    wakeLockTimeOut = 0;

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
            return new TaskParams(conditions, isPersistent, retryCount, groupId, wakeLock, wakeLockTimeOut);
        }
    }
}
