package com.example.weatherApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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

    private String cityKey = "city";
    private String temperatureKey = "temperature";
    private String windKey = "wind";
    private String pressureKey = "pressure";
    private String humidityKey = "humidity";
    private String saveKey = "preferences";
    private String saveCityKey = "savedCity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ИНИЦИАЛИЗАЦИЯ ВЬЮ
        initialize();

        //УСТАНОВКА ОБРАБОТЧИКОВ НАЖАТИЯ НА КНОПКИ
        initButtonsListeners();

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

        final Intent intent = new Intent(this, Main2Activity.class);


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
                    intent.putExtra(cityKey, city);
                    intent.putExtra(temperatureKey, temperature);
                    intent.putExtra(windKey, wind);
                    intent.putExtra(pressureKey, pressure);
                    intent.putExtra(humidityKey, humidity);
                    startActivity(intent);
                } else {
                    //АЛЬБОМНАЯ ОРИЕНТАЦИЯ


                    if (city.equals("")) {

                        textViewCity.setText(loadPreferences(sharedPref));
                    } else {

                        textViewCity.setText(city);
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
}
