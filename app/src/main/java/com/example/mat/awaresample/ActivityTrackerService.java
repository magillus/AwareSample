package com.example.mat.awaresample;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by mateusz.perlak on 9/19/16.
 */

public class ActivityTrackerService extends IntentService {
    private static final String ACTION_CHECK_ACTIVITY = "ACTION_CHECK_ACTIVITY";
    private static final String ACTION_START_ALARM = "ACTION_START_ALARM";
    private static final String ACTION_STOP_ALARM = "ACTION_STOP_ALARM";

    private static final int ACTION_REQUEST_CODE = 343;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ActivityTrackerService(String name) {
        super(name);
    }

    public static PendingIntent getCheckActivityPendingIntent(Context context) {
        return PendingIntent.getService(context, ACTION_REQUEST_CODE,
                new Intent(ACTION_CHECK_ACTIVITY),
                0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_CHECK_ACTIVITY:
                handleCheckActivity();
                break;
            case ACTION_START_ALARM:
                startAlarmCheck(30000);
                break;
            case ACTION_STOP_ALARM:
                stopAlarmCheck();
                break;
        }
    }

    private void startAlarmCheck(long timeSpan) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis() + timeSpan, timeSpan,
                getCheckActivityPendingIntent(this));
    }

    private void stopAlarmCheck() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(getCheckActivityPendingIntent(this));
    }

    private void handleCheckActivity() {
        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        client.connect();
        Awareness.SnapshotApi.getDetectedActivity(client).setResultCallback(new ResultCallback<DetectedActivityResult>() {
            @Override
            public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                if (!detectedActivityResult.getStatus().isSuccess()) {
                    storeNoActivityDetected();
                } else {
                    storeActivity(detectedActivityResult.
                            getActivityRecognitionResult().
                            getMostProbableActivity());
                }
            }
        });
    }
    private void storeNoActivityDetected() {
        
    }

    private void storeActivity(DetectedActivity activity) {

    }
}
