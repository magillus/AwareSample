package com.example.mat.awaresample


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.snapshot.WeatherResult
import com.google.android.gms.awareness.state.Weather
import com.google.android.gms.common.api.ResultCallback


/**
 * A simple [Fragment] subclass.
 */
class WeatherFragment : Fragment() {

    internal lateinit var weatherText: TextView


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherText = view!!.findViewById<View>(R.id.weather_text) as TextView
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as MainActivity
        if (activity != null) {
            activity.title = "Weather"

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 333)
            }

            Awareness.SnapshotApi
                    .getWeather(activity.client)
                    .setResultCallback { weatherResult ->

                        if (!weatherResult.status.isSuccess) {
                            weatherText.text = "No weather data."
                        } else {

                            val cTemp = weatherResult.weather.getTemperature(Weather.CELSIUS)
                            val fTemp = weatherResult.weather.getTemperature(Weather.FAHRENHEIT)
                            val humidity = weatherResult.weather.humidity
                            val sb = StringBuilder("Conditions: ")
                            for (conditionId in weatherResult.weather.conditions) {
                                sb.append(getConditionText(conditionId))
                                sb.append("\t")
                            }

                            weatherText.setText(String.format("temp: \n%f C\t\t %f F\n\nHumid: %d\n\n Forecast:%s",
                                    cTemp, fTemp, humidity, sb.toString()
                            ))

                        }
                    }
        }

    }

    private fun getConditionText(conditionId: Int): String {
        when (conditionId) {
            Weather.CONDITION_CLEAR -> return "Clear"
            Weather.CONDITION_CLOUDY -> return "Cloudy"
            Weather.CONDITION_FOGGY -> return "Foggy"
            Weather.CONDITION_HAZY -> return "Hazy"
            Weather.CONDITION_ICY -> return "Icy"
            Weather.CONDITION_RAINY -> return "Rainy"
            Weather.CONDITION_SNOWY -> return "Snowy"
            Weather.CONDITION_WINDY -> return "Widny"
            Weather.CONDITION_UNKNOWN -> return "N/A"
            else -> return "N/A"
        }
    }
}// Required empty public constructor
