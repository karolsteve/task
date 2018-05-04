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

package com.steve.task.condition;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.steve.task.ContextRequired;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public class TaskNetworkCondition implements TaskCondition, ContextRequired {

    private transient Context context;

    TaskNetworkCondition(Context context) {
        this.context = context;
    }

    public TaskNetworkCondition() {
    }

    @Override
    public boolean isOk() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}
