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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

class TaskQueue {
    private final Set<String> activeGroupIds = new HashSet<>();

    final LinkedList<Task> taskQueue = new LinkedList<>();

    synchronized void onConditionChanged() {
        notifyAll();
    }

    synchronized void add(Task task) {
        taskQueue.add(task);
        notifyAll();
    }

    synchronized void addAll(List<Task> tasks) {
        taskQueue.addAll(tasks);
        notifyAll();
    }

    synchronized void push(Task task) {
        taskQueue.addFirst(task);
    }

    synchronized Task getNextTask() {
        try {
            Task nextTask;
            while ((nextTask = getNextAvailableTask()) == null) {
                wait();
            }
            return nextTask;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    private synchronized Task getNextAvailableTask() {
        if (taskQueue.isEmpty()) return null;
        ListIterator<Task> iterator = taskQueue.listIterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.isAllConditionsOK() && isGroupAvailable(task.getGroupId())) {
                iterator.remove();
                setGroupIdUnavailable(task.getGroupId());
                return task;
            }
        }
        return null;
    }

    private void setGroupIdUnavailable(String groupId) {
        if (groupId != null) activeGroupIds.add(groupId);
    }

    private boolean isGroupAvailable(String groupId) {
        return groupId == null || !activeGroupIds.contains(groupId);
    }

    synchronized void setGroupIdAvailable(String groupId) {
        if (groupId != null) {
            activeGroupIds.remove(groupId);
            notifyAll();
        }
    }
}
