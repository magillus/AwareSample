package com.example.mat.awaresample;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.FenceState;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IAmStillBroadcastReceiver extends BroadcastReceiver {

    public static final String STILL_FENCE_KEY = "STILL_FENCE_KEY";
    public static final int NOTIFICATION_ID = 342343;

    private SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
    public IAmStillBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState state = FenceState.extract(intent);
        if (state.getFenceKey().equals(STILL_FENCE_KEY)) {
            // get something extra to display
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentText("I am Still received broadcast.")
                    .setSmallIcon(R.drawable.ic_place_black_24dp)
                    .setContentInfo(String.format("Still %s  at  %s",
                            getStateName(state.getCurrentState()),
                            dateFormat.format(new Date(state.getLastFenceUpdateTimeMillis()))))
                    .build();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            manager.notify(NOTIFICATION_ID, notification);
        }
    }


    private String getStateName(int state) {
        switch (state) {
            case FenceState.FALSE:
                return "FALSE";
            case FenceState.TRUE:
                return "TRUE";
            default:
            case FenceState.UNKNOWN:
                return "UNKNOWN";
        }
    }

    public static Intent getIntent() {
        Intent intent = new Intent("com.example.mat.awaresample.STILL_ACTION");
        intent.addCategory("android.intent.category.DEFAULT");
        return intent;
    }
}
