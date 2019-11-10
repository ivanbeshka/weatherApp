package com.example.weatherApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.weatherApp.database.WeatherDBReader;
import com.example.weatherApp.database.WeatherDBSource;

import java.util.Date;

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

    private MyBroadcastReceiver myBroadcastReceiver;
    private Intent intentService;

    //чтение данных
    private WeatherDBReader weatherDBReader;
    //получение данных
    private WeatherDBSource weatherDBSource;

    private boolean geoCoords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initializeView();

        initDBSource();

        intentService = new Intent(this, ServiceReadWeatherInfo.class);

        myBroadcastReceiver = new MyBroadcastReceiver();

        // регистрируем BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        Intent intent = getIntent();

        String prefFileName = saveKey;
        SharedPreferences sharedPref = getSharedPreferences(prefFileName, MODE_PRIVATE);

        String city = intent.getStringExtra(cityKey);
        double lat = intent.getDoubleExtra("lat", 0);
        double lot = intent.getDoubleExtra("lot", 0);

        geoCoords = intent.getBooleanExtra("GeoCoord", false);


        //если включены геокоординаты и подключён интернет
        if (isOnline(getApplicationContext()) && geoCoords) {

            intentService.putExtra("GeoCoord", true);
            intentService.putExtra("lat", lat);
            intentService.putExtra("lot", lot);
            startService(intentService);
        }

        if (city.equals("")) {

            textViewCity.setText(loadPreferences(sharedPref));
        } else {

            textViewCity.setText(city);
        }

        if (isOnline(this)) {
            startService(intentService.putExtra(cityKey, city));
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

    @Override
    protected void onDestroy() {

        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }

    private void initializeView() {
        textViewCity = findViewById(R.id.tv_city);
        textViewTemperature = findViewById(R.id.tv_temperature);
        textViewWind = findViewById(R.id.tv_wind_speed);
        textViewPressure = findViewById(R.id.tv_pressure);
        textViewHumidity = findViewById(R.id.tv_humidity);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra(temperatureKey);
            String humid = intent.getStringExtra(humidityKey);
            String wind = intent.getStringExtra(windKey);
            String press = intent.getStringExtra(pressureKey);

            if (geoCoords) {
                String city = intent.getStringExtra(cityKey);
                textViewCity.setText(city);
            }

            String date = String.valueOf(new Date());
            long time = new Date().getTime();

            //добавление погоды в базу данных
            weatherDBSource.addWeather(String.valueOf(textViewCity.getText()), Float.parseFloat(temp),
                    Float.parseFloat(wind), Integer.parseInt(press), Integer.parseInt(humid), date, time);
            dataUpdated();

            textViewTemperature.setText(temperatureKey + " " + temp);
            textViewHumidity.setText(humidityKey + " " + humid);
            textViewPressure.setText(pressureKey + " " + press);
            textViewWind.setText(windKey + " " + wind);

        }
    }

    private void initDBSource() {
        weatherDBSource = new WeatherDBSource(getApplicationContext());
        weatherDBSource.open();
        weatherDBReader = weatherDBSource.getWeatherDBReader();
    }

    private void dataUpdated() {
        weatherDBReader.refresh();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    //ПОЛУЧАЕМ СОХРАНЁННЫЙ ГОРОД
    public String loadPreferences(SharedPreferences sharedPref) {

        return sharedPref.getString(saveCityKey, "null");
    }
}
