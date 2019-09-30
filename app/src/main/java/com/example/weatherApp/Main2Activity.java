package com.example.weatherApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.example.weatherApp.MainActivity.cityKey;
import static com.example.weatherApp.MainActivity.humidityKey;
import static com.example.weatherApp.MainActivity.pressureKey;
import static com.example.weatherApp.MainActivity.saveCityKey;
import static com.example.weatherApp.MainActivity.saveKey;
import static com.example.weatherApp.MainActivity.temperatureKey;
import static com.example.weatherApp.MainActivity.windKey;
import static com.example.weatherApp.ServiceReadWeatherInfo.ACTION_MYINTENTSERVICE;

public class Main2Activity extends AppCompatActivity {

    private TextView textViewCity;
    private TextView textViewTemperature;
    private TextView textViewWind;
    private TextView textViewPressure;
    private TextView textViewHumidity;

    private MyBroadcastReceiver MyBroadcastReceiver;
    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textViewCity = findViewById(R.id.tv_city);
        textViewTemperature = findViewById(R.id.tv_temperature);
        textViewWind = findViewById(R.id.tv_wind_speed);
        textViewPressure = findViewById(R.id.tv_pressure);
        textViewHumidity = findViewById(R.id.tv_humidity);


        intentService = new Intent(this, ServiceReadWeatherInfo.class);

        MyBroadcastReceiver = new MyBroadcastReceiver();

        // регистрируем BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(MyBroadcastReceiver, intentFilter);

        Intent intent = getIntent();

        String prefFileName = saveKey;
        SharedPreferences sharedPref = getSharedPreferences(prefFileName, MODE_PRIVATE);

        String city = intent.getStringExtra(cityKey);

        if (city.equals("")) {

            textViewCity.setText(loadPreferences(sharedPref));
        } else {

            textViewCity.setText(city);
        }

        startService(intentService.putExtra(cityKey, city));

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

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra(temperatureKey);
            String humid = intent.getStringExtra(humidityKey);
            String wind = intent.getStringExtra(windKey);
            String press = intent.getStringExtra(pressureKey);

            textViewTemperature.setText(temperatureKey + " " + temp);
            textViewHumidity.setText(humidityKey + " " + humid);
            textViewPressure.setText(pressureKey + " " + press);
            textViewWind.setText(windKey + " " + wind);

        }
    }

    //ПОЛУЧАЕМ СОХРАНЁННЫЙ ГОРОД
    public String loadPreferences(SharedPreferences sharedPref) {

        return sharedPref.getString(saveCityKey, "null");
    }
}
