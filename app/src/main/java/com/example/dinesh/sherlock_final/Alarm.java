package com.example.dinesh.sherlock_final;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dinesh on 11/8/16.
 */
public class Alarm extends BroadcastReceiver     {
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("swdsw","Alarm");
       // Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show();
        PowerManager.WakeLock wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP , "WakeLock");

        wakeLock.acquire();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wakeLock.release();



        final PendingIntent pIntent = PendingIntent.getBroadcast(context, 0,
                intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarm.cancel(pIntent);

        AlarmManager am=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setExactAndAllowWhileIdle(ALARM_TYPE, System.currentTimeMillis()+30000, pendingIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            am.setExact(ALARM_TYPE,System.currentTimeMillis()+30000, pendingIntent);
        else
            am.set(ALARM_TYPE, System.currentTimeMillis()+30000, pendingIntent);


    }

}

