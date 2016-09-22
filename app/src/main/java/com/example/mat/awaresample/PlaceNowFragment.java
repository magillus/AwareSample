package com.example.mat.awaresample;


import android.Manifest;
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
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.common.api.ResultCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceNowFragment extends Fragment {

    private TextView txtLabel;

    public PlaceNowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_now, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtLabel = (TextView) view.findViewById(R.id.place_now);
    }

    @Override
    public void onResume() {
        super.onResume();

        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setTitle("Location now");


            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 333);
            }


            Awareness.SnapshotApi.getLocation(activity.client).setResultCallback(locationResult -> {


                if (!locationResult.getStatus().isSuccess()) {
                    txtLabel.setText("Place now not found.");
                } else {
                    txtLabel.setText(String.format("Place now: %f, %f -> %s",
                            locationResult.getLocation().getLongitude(),
                            locationResult.getLocation().getLatitude(),
                            ""));
                }
            });
        }
    }
}
