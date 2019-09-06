package com.example.wether_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private CheckBox checkBox_temperature;
    private CheckBox checkBox_wind_speed;
    private CheckBox checkBox_pressure;
    private CheckBox checkBox_humidity;
    private FloatingActionButton floatingActionButton;

    private TextView textView_city;
    private TextView textView_temperature;
    private TextView textView_wind;
    private TextView textView_pressure;
    private TextView textView_humidity;

    private SensorManager sensorManager;
    private Sensor temperature_sensor;
    private Sensor humidity_sensor;

    private String cityKey = "city";
    private String temperatureKey = "temperature";
    private String windKey = "wind";
    private String pressureKey = "pressure";
    private String humidityKey = "humidity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        checkBox_temperature = findViewById(R.id.checkBox);
        checkBox_wind_speed = findViewById(R.id.checkBox2);
        checkBox_pressure = findViewById(R.id.checkBox3);
        checkBox_humidity = findViewById(R.id.checkBox4);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        textView_city = findViewById(R.id.tv_city);
        textView_temperature = findViewById(R.id.tv_temperature);
        textView_wind = findViewById(R.id.tv_pressure);
        textView_pressure = findViewById(R.id.tv_pressure);
        textView_humidity = findViewById(R.id.tv_humidity);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        temperature_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humidity_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);


        final Intent intent = new Intent(this, Main2Activity.class);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = String.valueOf(editText.getText());
                Boolean temperature = checkBox_temperature.isChecked();
                Boolean wind = checkBox_wind_speed.isChecked();
                Boolean pressure = checkBox_pressure.isChecked();
                Boolean humidity = checkBox_humidity.isChecked();

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    intent.putExtra(cityKey, city);
                    intent.putExtra(temperatureKey, temperature);
                    intent.putExtra(windKey, wind);
                    intent.putExtra(pressureKey, pressure);
                    intent.putExtra(humidityKey, humidity);
                    startActivity(intent);
                } else {
                    textView_city.setVisibility(View.VISIBLE);
                    textView_city.setText(city);

                    if (temperature) {
                        textView_temperature.setVisibility(View.VISIBLE);
                    }

                    if (wind) {
                        textView_wind.setVisibility(View.VISIBLE);
                    }

                    if (pressure) {
                        textView_pressure.setVisibility(View.VISIBLE);
                    }

                    if (humidity) {
                        textView_humidity.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerTemperature, temperature_sensor);
        sensorManager.unregisterListener(listenerHumidity, humidity_sensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temperature_sensor == null) {
            sensorManager.registerListener(listenerTemperature, temperature_sensor, sensorManager.SENSOR_DELAY_NORMAL);
        }
        if (humidity_sensor == null) {
            sensorManager.registerListener(listenerHumidity, humidity_sensor, sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private final SensorEventListener listenerTemperature = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            textView_temperature.setText(R.string.temperature + ": " + event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private final SensorEventListener listenerHumidity = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            textView_humidity.setText(R.string.humidity + ": " + event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
