package com.example.screenstop;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

// BootReceiver.java
public class BootReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
            float percentage = settings.getFloat(MainActivity.SCREEN_PERCENTAGE_KEY, 20.0f);

            Intent serviceIntent = new Intent(context, ScreenStopService.class);
            serviceIntent.putExtra("percentage", percentage);
            context.startService(serviceIntent);
        }
    }
}
