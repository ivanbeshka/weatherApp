package com.example.weatherApp;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.weatherApp.database.WeatherDBReader;
import com.example.weatherApp.database.WeatherDBSource;

import java.util.Date;

import static com.example.weatherApp.ServiceReadWeatherInfo.ACTION_MYINTENTSERVICE;

public class MainActivity extends AppCompatActivity {
    //TODO реализовать список с выбором города


    private EditText editTextInputCity;
    private CheckBox checkBoxTemperature;
    private CheckBox checkBoxWindSpeed;
    private CheckBox checkBoxPressure;
    private CheckBox checkBoxHumidity;

    private TextView textViewCity;
    private TextView textViewTemperature;
    private TextView textViewWind;
    private TextView textViewPressure;
    private TextView textViewHumidity;

    private FloatingActionButton floatingActionButton;
    private Button btnSaveHomeCity;

    public static final String cityKey = "city";
    public static final String temperatureKey = "temperature";
    public static final String windKey = "wind";
    public static final String pressureKey = "pressure";
    public static final String humidityKey = "humidity";
    public static final String saveKey = "preferences";
    public static final String saveCityKey = "savedCity";

    public static MyBroadcastReceiver myBroadcastReceiver;

    //чтение данных
    private WeatherDBReader weatherDBReader;
    //получение данных
    private WeatherDBSource weatherDBSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDBSource();

        //ИНИЦИАЛИЗАЦИЯ ВЬЮ
        initializeView();

        //УСТАНОВКА ОБРАБОТЧИКОВ НАЖАТИЯ НА КНОПКИ
        initButtonsListeners();

        myBroadcastReceiver = new MyBroadcastReceiver();

        // регистрируем BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }

    private void initDBSource() {
        weatherDBSource = new WeatherDBSource(getApplicationContext());
        weatherDBSource.open();
        weatherDBReader = weatherDBSource.getWeatherDBReader();
    }

    private void dataUpdated() {
        weatherDBReader.refresh();
    }


    //УСТАНОВКА ОБРАБОТЧИКОВ НАЖАТИЯ НА КНОПКИ
    private void initButtonsListeners() {

        final Intent intentMain2 = new Intent(this, Main2Activity.class);
        final Intent intentService = new Intent(this, ServiceReadWeatherInfo.class);

        btnSaveHomeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String prefFileName = saveKey;

                SharedPreferences sharedPref = getSharedPreferences(prefFileName, MODE_PRIVATE);
                savePref(sharedPref);
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String prefFileName = saveKey;
                SharedPreferences sharedPref = getSharedPreferences(prefFileName, MODE_PRIVATE);

                String city = editTextInputCity.getText().toString();
                Boolean temperature = checkBoxTemperature.isChecked();
                Boolean wind = checkBoxWindSpeed.isChecked();
                Boolean pressure = checkBoxPressure.isChecked();
                Boolean humidity = checkBoxHumidity.isChecked();


                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //ПОРТРЕТНАЯ ОРИЕНТАЦИЯ ПЕРЕДАЧА НАСТРОЕК
                    intentMain2.putExtra(cityKey, city);
                    intentMain2.putExtra(temperatureKey, temperature);
                    intentMain2.putExtra(windKey, wind);
                    intentMain2.putExtra(pressureKey, pressure);
                    intentMain2.putExtra(humidityKey, humidity);
                    startActivity(intentMain2);
                } else {
                    //АЛЬБОМНАЯ ОРИЕНТАЦИЯ
                    if (city.equals("")) {

                        textViewCity.setText(loadPreferences(sharedPref));
                    } else {

                        textViewCity.setText(city);
                    }

                    if (isOnline(MainActivity.this)) {
                        startService(intentService.putExtra(cityKey, city));
                    }


                    textViewCity.setVisibility(View.VISIBLE);

                    if (temperature) {
                        textViewTemperature.setVisibility(View.VISIBLE);
                    }

                    if (wind) {
                        textViewWind.setVisibility(View.VISIBLE);
                    }

                    if (pressure) {
                        textViewPressure.setVisibility(View.VISIBLE);
                    }

                    if (humidity) {
                        textViewHumidity.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }


    //ПОЛУЧЕНИЕ ЗНАЧЕНИЙ ОТ СЕРВИСА
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                String temp = intent.getStringExtra(temperatureKey);
                String humid = intent.getStringExtra(humidityKey);
                String wind = intent.getStringExtra(windKey);
                String press = intent.getStringExtra(pressureKey);

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

    //СОХРАНЯЕМ НАСТРОЙКИ(ГОРОД)
    private void savePref(SharedPreferences sharedPref) {

        String savedCity = editTextInputCity.getText().toString();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(saveCityKey, savedCity);
        editor.apply();
    }

    //ПОЛУЧАЕМ СОХРАНЁННЫЙ ГОРОД
    public String loadPreferences(SharedPreferences sharedPref) {

        return sharedPref.getString(saveCityKey, "null");
    }

    //ИНИЦИАЛИЗАЦИЯ ВЬЮ
    private void initializeView() {
        editTextInputCity = findViewById(R.id.et_input_city);
        checkBoxTemperature = findViewById(R.id.cb_temperature);
        checkBoxWindSpeed = findViewById(R.id.cb_wind_speed);
        checkBoxPressure = findViewById(R.id.cb_pressure);
        checkBoxHumidity = findViewById(R.id.cb_humidity);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        btnSaveHomeCity = findViewById(R.id.btn_save_home_city);

        textViewCity = findViewById(R.id.tv_city);
        textViewTemperature = findViewById(R.id.tv_temperature);
        textViewWind = findViewById(R.id.tv_wind_speed);
        textViewPressure = findViewById(R.id.tv_pressure);
        textViewHumidity = findViewById(R.id.tv_humidity);

    }
}

