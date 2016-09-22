package com.example.mat.awaresample;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


/**
 * A simple {@link Fragment} subclass.
 */
public class HeadphoneFragment extends Fragment {


    private static final String TAG = HeadphoneFragment.class.getSimpleName();

    public HeadphoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_headphone, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        final MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {
            activity.setTitle("Headphones");

            Awareness.SnapshotApi.getHeadphoneState(activity.client).setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                @Override
                public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {

                    ImageView statusImageView = (ImageView) getView().findViewById(R.id.img_headphone_state);























                    if (!headphoneStateResult.getStatus().isSuccess()) {
                        statusImageView.setImageDrawable(AppCompatResources.getDrawable(activity,
                                R.drawable.cross));
                    } else {
                        switch (headphoneStateResult.getHeadphoneState().getState()) {
                            case HeadphoneState.PLUGGED_IN:
                                statusImageView.setImageDrawable(AppCompatResources.getDrawable(activity,
                                        R.drawable.checkmark));
                                break;
                            case HeadphoneState.UNPLUGGED:
                                statusImageView.setImageDrawable(AppCompatResources.getDrawable(activity,
                                        R.drawable.minus));
                                break;
                        }
                    }
                }
            });

            /// LOOK DOWN!!



























            // register headphone fence
            fenceIntent = PendingIntent.getBroadcast(activity, 0, new Intent("FENCE_RECEIVER_ACTION"), 0);
            receiver = new HeadphoneFenceReceiver();

            activity.registerReceiver(receiver, new IntentFilter("FENCE_RECEIVER_ACTION"));

            AwarenessFence fence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
























            Awareness.FenceApi.updateFences(activity.client, new FenceUpdateRequest.Builder()
                    .addFence("headphoneFenceKey", fence, fenceIntent)
                    .build()).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.i(TAG, "Fence for headphones in registered");
                    } else {
                        Log.i(TAG, "Fence for headphones in NOT registered");
                    }
                }
            });
        }
    }

    PendingIntent fenceIntent;
    HeadphoneFenceReceiver receiver;
























    private class HeadphoneFenceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);
            if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFenceKey")) {
                ImageView statusImageView = (ImageView) getView().findViewById(R.id.img_headphone_state);
                if (statusImageView != null) {
                    switch (fenceState.getCurrentState()) {
                        case FenceState.TRUE:
                            statusImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(),
                                    R.drawable.checkmark));
                            break;
                        case FenceState.FALSE:
                            statusImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(),
                                    R.drawable.minus));
                            break;
                        default:
                        case FenceState.UNKNOWN:
                            statusImageView.setImageDrawable(AppCompatResources.getDrawable(getContext(),
                                    R.drawable.cross));
                            break;
                    }
                }
            }
        }
    }
}
