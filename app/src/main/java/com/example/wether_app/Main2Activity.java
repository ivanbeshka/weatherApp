package com.example.wether_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    private String cityKey = "city";
    private String temperatureKey = "temperature";
    private String windKey = "wind";
    private String pressureKey = "pressure";
    private String humidityKey = "humidity";

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

        Bundle arguments = getIntent().getExtras();

        textViewCity.setText(arguments.get(cityKey).toString());

        if((boolean)arguments.get(temperatureKey)){
            textViewTemperature.setVisibility(View.VISIBLE);
        }

        if((boolean)arguments.get(windKey)){
            textViewWind.setVisibility(View.VISIBLE);
        }

        if((boolean)arguments.get(pressureKey)){
            textViewPressure.setVisibility(View.VISIBLE);
        }

        if((boolean)arguments.get(humidityKey)){
            textViewHumidity.setVisibility(View.VISIBLE);
        }
    }
}
