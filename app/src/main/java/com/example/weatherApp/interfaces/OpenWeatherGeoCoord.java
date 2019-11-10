package com.example.weatherApp.interfaces;

import com.example.weatherApp.modelGeoCoord.WeatherRequestGeoCoord;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherGeoCoord {

    @GET("data/2.5/weather")
    Call<WeatherRequestGeoCoord> loadWeather(@Query("lat") double lat,
                                             @Query("lon") double lon,
                                             @Query("appId") String keyApi);
}
