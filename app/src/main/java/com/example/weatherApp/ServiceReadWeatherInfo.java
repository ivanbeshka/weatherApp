package com.example.weatherApp;

import android.app.IntentService;
import android.content.Intent;

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
                            temp = Float.toString((float) (response.body().getMain().getTemp() - 273.15));
                            press = Integer.toString((int) (response.body().getMain().getPressure() * 0.750062));
                            humid = Integer.toString(response.body().getMain().getHumidity());
                            wind = Float.toString(response.body().getWind().getSpeed());

                            Intent responseIntent = new Intent();
                            responseIntent.setAction(ACTION_MYINTENTSERVICE);
                            responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            responseIntent.putExtra(temperatureKey, temp);
                            responseIntent.putExtra(humidityKey, humid);
                            responseIntent.putExtra(pressureKey, press);
                            responseIntent.putExtra(windKey, wind);
                            sendBroadcast(responseIntent);
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
