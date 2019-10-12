package com.example.weatherApp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;

public class WeatherDBSource implements Closeable {
    //  Источник данных, позволяет изменять данные в таблице
// Создает и содержит в себе читатель данных

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private WeatherDBReader weatherDBReader;

    public WeatherDBSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Открывает базу данных
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        // создать читателя и открыть его
        weatherDBReader = new WeatherDBReader(database);
        weatherDBReader.open();
    }

    // Закрыть базу данных
    public void close() {
        weatherDBReader.close();
        dbHelper.close();
    }

    // Добавить новую запись
    public WeatherDBStructure addWeather(String city, float temperature, float wind, int pressure,
                              int humidity, String date, long time) {
        ContentValues values = new ContentValues();


        values.put(DatabaseHelper.COLUMN_CITY, city);
        values.put(DatabaseHelper.COLUMN_TEMPERATURE, temperature);
        values.put(DatabaseHelper.COLUMN_WIND, wind);
        values.put(DatabaseHelper.COLUMN_PRESSURE, pressure);
        values.put(DatabaseHelper.COLUMN_HUMIDITY, humidity);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_TIME, time);


        // Добавление записи
        long insertId = database.insert(DatabaseHelper.TABLE_WEATHER, null,
                values);


        WeatherDBStructure newWeather = new WeatherDBStructure();

        newWeather.setCity(city);
        newWeather.setTemperature(temperature);
        newWeather.setWind(wind);
        newWeather.setPressure(pressure);
        newWeather.setHumidity(humidity);
        newWeather.setTime(time);
        newWeather.setDate(date);
        newWeather.setId(insertId);

        return newWeather;
    }

    // Изменить запись
    public void editWeather(WeatherDBStructure weather, String city, float temperature, float wind, int pressure,
                            int humidity, String date, long time) {
        ContentValues editedWeather = new ContentValues();

        editedWeather.put(DatabaseHelper.COLUMN_ID, weather.getId());
        editedWeather.put(DatabaseHelper.COLUMN_CITY, city);
        editedWeather.put(DatabaseHelper.COLUMN_TEMPERATURE, temperature);
        editedWeather.put(DatabaseHelper.COLUMN_WIND, wind);
        editedWeather.put(DatabaseHelper.COLUMN_PRESSURE, pressure);
        editedWeather.put(DatabaseHelper.COLUMN_HUMIDITY, humidity);
        editedWeather.put(DatabaseHelper.COLUMN_DATE, date);
        editedWeather.put(DatabaseHelper.COLUMN_TIME, time);

        // изменение записи
        database.update(DatabaseHelper.TABLE_WEATHER,
                editedWeather,
                DatabaseHelper.COLUMN_ID + " = " + weather.getId(),
                null);
    }

    // Удалить запись
    public void deleteWather(WeatherDBStructure weather) {
        long id = weather.getId();
        database.delete(DatabaseHelper.TABLE_WEATHER, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    // Очистить таблицу
    public void deleteAll() {
        database.delete(DatabaseHelper.TABLE_WEATHER, null, null);
    }

    // вернуть читателя (он потребуется в других местах)
    public WeatherDBReader getWeatherDBReader() {
        return weatherDBReader;
    }
}

