package com.example.weatherApp.database;


// Класс - отражение строк из таблицы
public class WeatherDBStructure {

    private long id;
    private String city;
    private float temperature;
    private float wind;
    private int pressure;
    private int humidity;
    private long time;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getPressure() {
        return pressure;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }

    public float getWind() {
        return wind;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
