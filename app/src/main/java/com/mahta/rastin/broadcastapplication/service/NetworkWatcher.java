package com.mahta.rastin.broadcastapplication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.mahta.rastin.broadcastapplication.global.G;

public class NetworkWatcher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //here, check that the network connection is available. If yes, start your service. If not, stop your service.
        if (G.isNetworkAvailable(context)) {
            //start service
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Intent intent1 = new Intent(context, NotificationService.class);
                context.startService(intent1);
            }
        }
        else {
            //stop service
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Intent intent2 = new Intent(context, NotificationService.class);
                context.stopService(intent2);
            }
        }
    }
}