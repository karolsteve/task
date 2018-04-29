package com.steve.task.condition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public class TaskNetworkConditionProvider implements TaskConditionProvider {
    private TaskConditionListener listener;
    private TaskNetworkCondition networkCondition;

    public TaskNetworkConditionProvider(Context context) {
        this.networkCondition = new TaskNetworkCondition(context);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(listener == null){
                    return;
                }
                if(networkCondition.isOk()){
                    listener.onConditionChanged();
                }
            }
        }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void setListener(TaskConditionListener listener) {
        this.listener = listener;
    }
}
