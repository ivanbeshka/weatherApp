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

    private TextView textView_city;
    private TextView textView_temperature;
    private TextView textView_wind;
    private TextView textView_pressure;
    private TextView textView_humidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView_city = findViewById(R.id.tv_city);
        textView_temperature = findViewById(R.id.tv_temperature);
        textView_wind = findViewById(R.id.tv_wind_speed);
        textView_pressure = findViewById(R.id.tv_pressure);
        textView_humidity = findViewById(R.id.tv_humidity);

        Bundle arguments = getIntent().getExtras();

        textView_city.setText(arguments.get(cityKey).toString());

        if((boolean)arguments.get(temperatureKey)){
            textView_temperature.setVisibility(View.VISIBLE);
        }

        if((boolean)arguments.get(windKey)){
            textView_wind.setVisibility(View.VISIBLE);
        }

        if((boolean)arguments.get(pressureKey)){
            textView_pressure.setVisibility(View.VISIBLE);
        }

        if((boolean)arguments.get(humidityKey)){
            textView_humidity.setVisibility(View.VISIBLE);
        }
    }
}
