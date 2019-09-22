package com.example.weatherApp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.weatherApp.interfaces.OpenWeather;
import com.example.weatherApp.model.WeatherRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceReadWeatherInfo extends IntentService {


    private String temp;
    private String press;
    private String humid;
    private String wind;

    public static final String ACTION_MYINTENTSERVICE = "intentservice.RESPONSE";
    private final String temperatureKey = "temperature";
    private final String windKey = "wind";
    private final String pressureKey = "pressure";
    private final String humidityKey = "humidity";

    private static final String KEY = "apiKey";
    private static final String API_KEY = "d42593a0cb673517005fafe0dde834ff";

    private String cityKey = "city";

    private OpenWeather openWeather;

    private SharedPreferences sharedPref;

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
        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_MYINTENTSERVICE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(temperatureKey, temp);
        responseIntent.putExtra(humidityKey, humid);
        responseIntent.putExtra(pressureKey, press);
        responseIntent.putExtra(windKey, wind);
        sendBroadcast(responseIntent);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/") // Базовая часть адреса
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
