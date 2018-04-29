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
