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
    private CheckBox checkBoxTemperature;
    private CheckBox checkBoxWindSpeed;
    private CheckBox checkBoxPressure;
    private CheckBox checkBoxHumidity;
    private FloatingActionButton floatingActionButton;

    private TextView textViewCity;
    private TextView textViewTemperature;
    private TextView textViewWind;
    private TextView textViewPressure;
    private TextView textViewHumidity;

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private Sensor humiditySensor;

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
        checkBoxTemperature = findViewById(R.id.checkBox);
        checkBoxWindSpeed = findViewById(R.id.checkBox2);
        checkBoxPressure = findViewById(R.id.checkBox3);
        checkBoxHumidity = findViewById(R.id.checkBox4);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        textViewCity = findViewById(R.id.tv_city);
        textViewTemperature = findViewById(R.id.tv_temperature);
        textViewWind = findViewById(R.id.tv_pressure);
        textViewPressure = findViewById(R.id.tv_pressure);
        textViewHumidity = findViewById(R.id.tv_humidity);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);


        final Intent intent = new Intent(this, Main2Activity.class);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = String.valueOf(editText.getText());
                Boolean temperature = checkBoxTemperature.isChecked();
                Boolean wind = checkBoxWindSpeed.isChecked();
                Boolean pressure = checkBoxPressure.isChecked();
                Boolean humidity = checkBoxHumidity.isChecked();

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    intent.putExtra(cityKey, city);
                    intent.putExtra(temperatureKey, temperature);
                    intent.putExtra(windKey, wind);
                    intent.putExtra(pressureKey, pressure);
                    intent.putExtra(humidityKey, humidity);
                    startActivity(intent);
                } else {
                    textViewCity.setVisibility(View.VISIBLE);
                    textViewCity.setText(city);

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

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerTemperature, temperatureSensor);
        sensorManager.unregisterListener(listenerHumidity, humiditySensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temperatureSensor != null) {
            sensorManager.registerListener(listenerTemperature, temperatureSensor, sensorManager.SENSOR_DELAY_NORMAL);
        }
        if (humiditySensor != null) {
            sensorManager.registerListener(listenerHumidity, humiditySensor, sensorManager.SENSOR_DELAY_NORMAL);
        }
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
}
