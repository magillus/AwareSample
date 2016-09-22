package com.example.mat.awaresample;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment {

    private PlacesAdapter placesAdapter;

    public PlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        RecyclerView placesList = (RecyclerView) view.findViewById(R.id.places_list);
        placesAdapter = new PlacesAdapter();
        placesList.setAdapter(placesAdapter);
        placesList.setLayoutManager(new LinearLayoutManager(getContext()));

    }





    @Override
    public void onResume() {
        super.onResume();
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setTitle("Places");



















            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 333);
            }

























            Awareness.SnapshotApi
                    .getPlaces(activity.client)
                    .setResultCallback(new ResultCallback<PlacesResult>() {
                @Override
                public void onResult(@NonNull PlacesResult placesResult) {



























                    if (!placesResult.getStatus().isSuccess()) {
                        Toast.makeText(activity, "No places found.", Toast.LENGTH_SHORT).show();;
                        placesAdapter.clear();
                    } else {
                        placesAdapter.setPlaces(placesResult.getPlaceLikelihoods());
                        if (placesResult.getPlaceLikelihoods()!=null) {
                            Toast.makeText(activity,
                                    String.format("%d places found.",
                                            placesResult.getPlaceLikelihoods().size()),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "No places found. -null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }


    private class PlacesAdapter extends RecyclerView.Adapter<PlaceViewHolder> {


        private final List<PlaceLikelihood> placeList = new ArrayList<>();

        public void clear() {
            placeList.clear();
        }

        public void setPlaces(List<PlaceLikelihood> places) {
            placeList.clear();
            if (places != null) {
                placeList.addAll(places);
            }
            notifyDataSetChanged();
        }

        @Override
        public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PlaceViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(PlaceViewHolder holder, int position) {
            holder.bind(placeList.get(position));
        }

        @Override
        public int getItemCount() {
            return placeList.size();
        }
    }

    private class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        public PlaceViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_place_item, parent, false));
            txtName = (TextView) itemView.findViewById(R.id.place_name);
        }

        public void bind(PlaceLikelihood placeLikelihood) {
            txtName.setText(placeLikelihood.getPlace().getName());
        }
    }
}
