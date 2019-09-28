package com.example.weatherApp;

import android.app.IntentService;
import android.content.Intent;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.weatherApp.interfaces.OpenWeather;
import com.example.weatherApp.model.WeatherRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.weatherApp.MainActivity.cityKey;
import static com.example.weatherApp.MainActivity.humidityKey;
import static com.example.weatherApp.MainActivity.pressureKey;
import static com.example.weatherApp.MainActivity.temperatureKey;
import static com.example.weatherApp.MainActivity.windKey;

public class ServiceReadWeatherInfo extends IntentService {

    private String temp;
    private String press;
    private String humid;
    private String wind;

    public static final String ACTION_MYINTENTSERVICE = "intentservice.RESPONSE";

    private static final String API_KEY = "d42593a0cb673517005fafe0dde834ff";

    private OpenWeather openWeather;

    public ServiceReadWeatherInfo() {
        super("ServiceReadWeatherInfo");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initRetrofit();
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String city = intent.getStringExtra(cityKey);
        requestRetrofit(city, API_KEY);

        // возвращаем результат
            Intent responseIntent1 = new Intent();
            responseIntent1.setAction(ACTION_MYINTENTSERVICE);
            responseIntent1.addCategory(Intent.CATEGORY_DEFAULT);
            responseIntent1.putExtra(temperatureKey, temp);
            responseIntent1.putExtra(humidityKey, humid);
            responseIntent1.putExtra(pressureKey, press);
            responseIntent1.putExtra(windKey, wind);
            sendBroadcast(responseIntent1);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/") // Базовая часть адреса
                // Конвертер, необходимый для преобразования JSON'а в объекты
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Создаем объект, при помощи которого будем выполнять запросы
        openWeather = retrofit.create(OpenWeather.class);
    }

    private void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequest> call,
                                           @NonNull Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            temp = Float.toString(response.body().getMain().getTemp());
                            press = Integer.toString(response.body().getMain().getPressure());
                            humid = Integer.toString(response.body().getMain().getHumidity());
                            wind = Float.toString(response.body().getWind().getSpeed());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherRequest> call,
                                          @NonNull Throwable throwable) {
                        Log.e("Retrofit", "request failed", throwable);
                    }
                });
    }


}
