package com.example.weatherApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

public class Main2Activity extends AppCompatActivity {

    private String cityKey = "city";
    private String temperatureKey = "temperature";
    private String windKey = "wind";
    private String pressureKey = "pressure";
    private String humidityKey = "humidity";
    private String saveKey = "preferences";
    private String saveCityKey = "savedCity";

    private TextView textViewCity;
    private TextView textViewTemperature;
    private TextView textViewWind;
    private TextView textViewPressure;
    private TextView textViewHumidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textViewCity = findViewById(R.id.tv_city);
        textViewTemperature = findViewById(R.id.tv_temperature);
        textViewWind = findViewById(R.id.tv_wind_speed);
        textViewPressure = findViewById(R.id.tv_pressure);
        textViewHumidity = findViewById(R.id.tv_humidity);

        Intent intent = getIntent();

        String prefFileName = saveKey;
        SharedPreferences sharedPref = getSharedPreferences(prefFileName, MODE_PRIVATE);

        String city = intent.getStringExtra(cityKey);

        if (city.equals("")) {

            textViewCity.setText(loadPreferences(sharedPref));
        } else {

            textViewCity.setText(city);
        }
        textViewCity.setVisibility(View.VISIBLE);


        if (intent.getBooleanExtra(temperatureKey, false)) {
            textViewTemperature.setVisibility(View.VISIBLE);
        }

        if (intent.getBooleanExtra(windKey, false)) {
            textViewWind.setVisibility(View.VISIBLE);
        }

        if (intent.getBooleanExtra(pressureKey, false)) {
            textViewPressure.setVisibility(View.VISIBLE);
        }

        if (intent.getBooleanExtra(humidityKey, false)) {
            textViewHumidity.setVisibility(View.VISIBLE);
        }
    }

    //ПОЛУЧАЕМ СОХРАНЁННЫЙ ГОРОД
    public String loadPreferences(SharedPreferences sharedPref) {

        return sharedPref.getString(saveCityKey, "null");
    }
}
