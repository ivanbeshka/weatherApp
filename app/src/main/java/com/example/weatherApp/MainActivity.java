package com.example.weatherApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.example.weatherApp.model.Main;

import java.util.Calendar;

import static com.example.weatherApp.ServiceReadWeatherInfo.ACTION_MYINTENTSERVICE;

public class MainActivity extends AppCompatActivity {


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
    private Button btnDeleteHomeCity;

    private static SensorManager sensorManager;
    private static Sensor temperatureSensor;
    private static Sensor humiditySensor;

    public static final String cityKey = "city";
    public static final String temperatureKey = "temperature";
    public static final String windKey = "wind";
    public static final String pressureKey = "pressure";
    public static final String humidityKey = "humidity";
    public static final String saveKey = "preferences";
    public static final String saveCityKey = "savedCity";

    private static String temp;

    public static MyBroadcastReceiver MyBroadcastReceiver;

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
        initialize();

        //УСТАНОВКА ОБРАБОТЧИКОВ НАЖАТИЯ НА КНОПКИ
        initButtonsListeners();

        MyBroadcastReceiver = new MyBroadcastReceiver();

        // регистрируем BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(MyBroadcastReceiver, intentFilter);

    }

    private void initDBSource() {
        weatherDBSource = new WeatherDBSource(getApplicationContext());
        weatherDBSource.open();
        weatherDBReader = weatherDBSource.getWeatherDBReader();
    }

    private void dataUpdated() {
        weatherDBReader.refresh();
    }


    @Override
    protected void onPause() {
        super.onPause();

        //ОТКЛЮЧЕНИЕ СЕНСОРОВ
        sensorManager.unregisterListener(listenerTemperature, temperatureSensor);
        sensorManager.unregisterListener(listenerHumidity, humiditySensor);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        //СЕНСОРЫ
//        if (temperatureSensor != null) {
//            sensorManager.registerListener(listenerTemperature, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//        if (humiditySensor != null) {
//            sensorManager.registerListener(listenerHumidity, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
//        }
    }

    private final SensorEventListener listenerTemperature = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            textViewTemperature.setText(R.string.temperature + ": " + event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private final SensorEventListener listenerHumidity = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            textViewHumidity.setText(R.string.humidity + ": " + event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


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

                    startService(intentService.putExtra(cityKey, city));

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
    private void initialize() {
        editTextInputCity = findViewById(R.id.et_input_city);
        checkBoxTemperature = findViewById(R.id.cb_temperature);
        checkBoxWindSpeed = findViewById(R.id.cb_wind_speed);
        checkBoxPressure = findViewById(R.id.cb_pressure);
        checkBoxHumidity = findViewById(R.id.cb_humidity);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        btnSaveHomeCity = findViewById(R.id.btn_save_home_city);
        btnDeleteHomeCity = findViewById(R.id.btn_delete_home_city);

        textViewCity = findViewById(R.id.tv_city);
        textViewTemperature = findViewById(R.id.tv_temperature);
        textViewWind = findViewById(R.id.tv_wind_speed);
        textViewPressure = findViewById(R.id.tv_pressure);
        textViewHumidity = findViewById(R.id.tv_humidity);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                String temp = intent.getStringExtra(temperatureKey);
                String humid = intent.getStringExtra(humidityKey);
                String wind = intent.getStringExtra(windKey);
                String press = intent.getStringExtra(pressureKey);
                String time = Calendar.getInstance().getTime().toString();
//                float windInt = Float.parseFloat(wind);
//                int pressInt = Integer.parseInt(press);
//                int humidInt = Integer.parseInt(humid);

                weatherDBSource.addWeather(String.valueOf(textViewCity.getText()), Float.parseFloat(temp),
                        1, 1, 1, time);
                dataUpdated();

                textViewTemperature.setText(temperatureKey + " " + temp);
                textViewHumidity.setText(humidityKey + " " + humid);
                textViewPressure.setText(pressureKey + " " + press);
                textViewWind.setText(windKey + " " + wind);



            }
        }
    }
}
