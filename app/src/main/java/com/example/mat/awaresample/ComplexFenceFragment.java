package com.example.mat.awaresample;


import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComplexFenceFragment extends Fragment {

    TextView fenceSetupText;
    TextView fenceActivityText;

    public ComplexFenceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complex_fence, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fenceSetupText = (TextView) view.findViewById(R.id.fence_setup_text);
        fenceActivityText = (TextView) view.findViewById(R.id.fence_activity_text);
    }

    private void addActivityText(String text) {
        fenceActivityText.setText(fenceActivityText.getText() + "\n" + text);
    }

    private void addSetupText(String text) {
        fenceSetupText.setText(fenceSetupText.getText() + "\n" + text);
    }


    private PendingIntent fencePendingIntent;
    private FenceBroadcastReceiver broadcastReceiver;

    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        MainActivity activity = (MainActivity) getActivity();
        activity.setTitle("Complex Fences");
        Intent intent = new Intent("FENCE_RECEIVER_ACTION");
        fencePendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0);
        broadcastReceiver = new FenceBroadcastReceiver();
        activity.registerReceiver(broadcastReceiver, new IntentFilter("FENCE_RECEIVER_ACTION"));


        AwarenessFence atWorkFence = LocationFence.in(41.9328746, -87.709752, 100, 30 * 1000);
        AwarenessFence stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);

        AwarenessFence stillAtWork = AwarenessFence.and(atWorkFence, stillFence);


        Awareness.FenceApi.updateFences(activity.client,
                new FenceUpdateRequest.Builder()
                        .addFence("stillAtWorkKey", stillAtWork, fencePendingIntent)
                        .build())
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        addSetupText("Fence for activity is setup");
                    } else {
                        addSetupText("Fence for activity is NOT setup");
                    }
                });


        Awareness.FenceApi.queryFences(activity.client,
                FenceQueryRequest.forFences("stillAtWorkKey"))
                .setResultCallback(fenceQueryResult -> {
                    if (!fenceQueryResult.getStatus().isSuccess()) {
                        addSetupText("Could not query fences: ");
                        return;
                    }
                    FenceStateMap map = fenceQueryResult.getFenceStateMap();
                    for (String fenceKey : map.getFenceKeys()) {
                        FenceState fenceState = map.getFenceState(fenceKey);
                        addActivityText("Fence " + fenceKey + ": "
                                + fenceState.getCurrentState()
                                + ", was="
                                + fenceState.getPreviousState()
                                + ", lastUpdateTime="
                                + new java.text.SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(
                                new Date(fenceState.getLastFenceUpdateTimeMillis())));
                    }
                });


    }


    private class FenceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);
            switch (fenceState.getFenceKey()) {
                case "stillAtWorkKey":
                    addActivityText(String.format(" STILL at work: %s", getStateName(fenceState.getCurrentState())));
                    break;
            }
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
}
