package com.gg.moviesmanager;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Service triggered by an Alarm.
 * It maintains the information of the database up-to-date.
 */
public class AutoUpdater extends IntentService {
    public AutoUpdater() {
        super("AutoUpdater");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //If there is internet connection, the update can proceed.
        if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
            updateList(0); // Latest
            updateList(1); // Upcoming
            updateList(2); // Popular
            SharedPreferences sharedPrefs = getSharedPreferences("main.prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putLong("last_update", new Date().getTime());
            editor.apply();
        } else { // If there is no internet connection, a secondary alarm is set (to be triggered after 15 minutes).
            Intent alarmIntent = new Intent(this, AutoUpdater.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pendingIntent);
        }
    }

    public void updateList(int id) {
        ArrayList<Movie> results;

        String jsonString = "";
        // Download of the new content.
        if (id == 0) {
            jsonString = DataDownloader.getLatest();
        } else if (id == 1) {
            jsonString = DataDownloader.getUpcoming();
        } else if (id == 2) {
            jsonString = DataDownloader.getPopulars();
        }
        if (jsonString.equals("")) {
            Log.e("ListDownload", "Could not download list!");
        } else {
            // Parse the JSON information downloaded.
            results = JSONParser.parseList(jsonString);
            if (results == null) {
                Log.e("ListDownload", "Could not parse list!");
            } else {
                // Updates the database with the new content.
                DbCrud.getInstance(this).insertMovies(results);
                for (Movie m : results) {
                    if (!m.getPoster().equals("")) {
                        DataDownloader.downloadImage(this, m.getPoster(), DataDownloader.TypeImage.POSTER);
                    }
                }
                Log.i("AutoUpdater", "Updated list!");
            }
        }
    }
}
