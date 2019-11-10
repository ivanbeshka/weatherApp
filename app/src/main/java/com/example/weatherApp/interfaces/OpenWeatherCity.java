package com.example.weatherApp.interfaces;

import com.example.weatherApp.model.WeatherRequestCity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherCity {

    @GET("data/2.5/weather")
    Call<WeatherRequestCity> loadWeather(@Query("q") String cityCountry, @Query("appId") String keyApi);
}
