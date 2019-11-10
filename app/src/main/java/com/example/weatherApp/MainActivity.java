package com.example.weatherApp;

import android.Manifest;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherApp.database.WeatherDBReader;
import com.example.weatherApp.database.WeatherDBSource;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

import static com.example.weatherApp.ServiceReadWeatherInfo.ACTION_MYINTENTSERVICE;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CODE = 10;
    //TODO: реализовать список с выбором города


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
    private Switch swGeoCoord;

    public static final String cityKey = "city";
    public static final String temperatureKey = "temperature";
    public static final String windKey = "wind";
    public static final String pressureKey = "pressure";
    public static final String humidityKey = "humidity";
    public static final String saveKey = "preferences";
    public static final String saveCityKey = "savedCity";

    private static double lat;
    private static double lot;

    public static MyBroadcastReceiver myBroadcastReceiver;

    //чтение данных
    private WeatherDBReader weatherDBReader;
    //получение данных
    private WeatherDBSource weatherDBSource;

    private FusedLocationProviderClient fusedLocationClient;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


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

        swGeoCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверим разрешения, и если их нет - запросим у пользователя
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    // запросим координаты
                    requestLocation();
                } else {
                    // разрешений нет, будем запрашивать у пользователя
                    requestLocationPermissions();

                }
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

                    if(swGeoCoord.isChecked()){
                        intentMain2.putExtra("GeoCoord", true);
                        intentMain2.putExtra("lat", lat);
                        intentMain2.putExtra("lot", lot);
                    }

                    intentMain2.putExtra(cityKey, city);
                    intentMain2.putExtra(temperatureKey, temperature);
                    intentMain2.putExtra(windKey, wind);
                    intentMain2.putExtra(pressureKey, pressure);
                    intentMain2.putExtra(humidityKey, humidity);

                    startActivity(intentMain2);

                } else {

                    //АЛЬБОМНАЯ ОРИЕНТАЦИЯ

                    //если включены геокоординаты и подключён интернет
                    if (isOnline(MainActivity.this) && swGeoCoord.isChecked()) {

                        intentService.putExtra("GeoCoord", true);
                        intentService.putExtra("lat", lat);
                        intentService.putExtra("lot", lot);
                        startService(intentService);

                    } else if (isOnline(MainActivity.this) && city.equals("") && !swGeoCoord.isChecked()) {

                        //загрузка сохранённого города
                        city = loadPreferences(sharedPref);

                        textViewCity.setText(city);

                        //если подключены к интернету, то можно запустить сервис
                        startService(intentService.putExtra(cityKey, city));

                    } else if (isOnline(getApplicationContext()) && !swGeoCoord.isChecked()) {

                        textViewCity.setText(city);

                        startService(intentService.putExtra(cityKey, city));

                    } else if(!isOnline(MainActivity.this)){

                        Toast.makeText(MainActivity.this, "have not internet", Toast.LENGTH_LONG).show();

                        //TODO: Получить из базы данных погоду для этого города

                        textViewCity.setText(city);
                    }

                    //Делаем видимым то, что отметели
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

    private void requestLocation() {
        // Если разрешений все-таки нет - то просто выйдем
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location (in some rare situations this can be null)
                        if (location != null) {
                            lat = location.getLatitude();   // Широта
                            lot = location.getLongitude(); // Долгота
                        } else {
                            Toast.makeText(MainActivity.this, "can't define location",
                                    Toast.LENGTH_LONG).show();
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

                if(swGeoCoord.isChecked()){
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

    // Запрос разрешения для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Это результат запроса у пользователя разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Это то самое разрешение, что мы запрашивали ?
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // разрешения даны
                requestLocation();
            }
        }
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
        swGeoCoord = findViewById(R.id.sw_geoCoord);

        textViewCity = findViewById(R.id.tv_city);
        textViewTemperature = findViewById(R.id.tv_temperature);
        textViewWind = findViewById(R.id.tv_wind_speed);
        textViewPressure = findViewById(R.id.tv_pressure);
        textViewHumidity = findViewById(R.id.tv_humidity);

    }
}

