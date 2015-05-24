package com.intmainreturn0.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by lz on 15-5-16.
 */
public class SampleBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intentx) {
        if (intentx.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent=new Intent(context, MyIntentService.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
            context.startService(intent);

        }
    }
}