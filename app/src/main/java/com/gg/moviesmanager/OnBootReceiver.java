package com.gg.moviesmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("main.prefs", Context.MODE_PRIVATE);
        long lastUpdate = sharedPrefs.getLong("last_update", 0);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") &&
                lastUpdate > 0) {
            Intent alarmIntent = new Intent(context, AutoUpdater.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC,
                    lastUpdate + AlarmManager.INTERVAL_DAY * 3,
                    AlarmManager.INTERVAL_DAY * 3, pendingIntent);
        }
    }
}
