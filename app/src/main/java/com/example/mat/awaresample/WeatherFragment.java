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
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.ResultCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {

    TextView weatherText;

    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weatherText = (TextView) view.findViewById(R.id.weather_text);
    }

    @Override
    public void onResume() {
        super.onResume();
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setTitle("Weather");

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 333);
            }

            Awareness.SnapshotApi
                    .getWeather(activity.client)
                    .setResultCallback(weatherResult -> {

                        if (!weatherResult.getStatus().isSuccess()) {
                            weatherText.setText("No weather data.");
                        } else {

                            float cTemp = weatherResult.getWeather().getTemperature(Weather.CELSIUS);
                            float fTemp = weatherResult.getWeather().getTemperature(Weather.FAHRENHEIT);
                            int humidity = weatherResult.getWeather().getHumidity();
                            StringBuilder sb = new StringBuilder("Conditions: ");
                            for (int conditionId : weatherResult.getWeather().getConditions()) {
                                sb.append(getConditionText(conditionId));
                                sb.append("\t");
                            }

                            weatherText.setText(String.format("temp: \n%f C\t\t %f F\n\nHumid: %d\n\n Forecast:%s",
                                    cTemp, fTemp, humidity, sb.toString()
                            ));

                        }
                    });
        }

    }

    private String getConditionText(int conditionId) {
        switch (conditionId) {
            case Weather.CONDITION_CLEAR:
                return "Clear";
            case Weather.CONDITION_CLOUDY:
                return "Cloudy";
            case Weather.CONDITION_FOGGY:
                return "Foggy";
            case Weather.CONDITION_HAZY:
                return "Hazy";
            case Weather.CONDITION_ICY:
                return "Icy";
            case Weather.CONDITION_RAINY:
                return "Rainy";
            case Weather.CONDITION_SNOWY:
                return "Snowy";
            case Weather.CONDITION_WINDY:
                return "Widny";
            default:
            case Weather.CONDITION_UNKNOWN:
                return "N/A";
        }
    }
}
