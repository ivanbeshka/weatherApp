package com.example.weatherApp.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;


public class WeatherDBReader implements Closeable {

    private Cursor cursor;              // Курсор: фактически это подготовенный запрос,
    // но сами данные считываются только по необходимости
    private final SQLiteDatabase database;

    private final String[] weatherAllColumn = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CITY,
            DatabaseHelper.COLUMN_TEMPERATURE,
            DatabaseHelper.COLUMN_WIND,
            DatabaseHelper.COLUMN_PRESSURE,
            DatabaseHelper.COLUMN_HUMIDITY,
            DatabaseHelper.COLUMN_DATE,
            DatabaseHelper.COLUMN_TIME
    };

    public WeatherDBReader(SQLiteDatabase database) {
        this.database = database;
    }

    // Подготовить к чтению таблицу
    public void open() {
        query();
        cursor.moveToFirst();
    }

    public void close() {
        cursor.close();
    }

    // Перечитать таблицу
    public void refresh() {
        int position = cursor.getPosition();
        query();
        cursor.moveToPosition(position);
    }

    // создание запроса
    private void query() {
        cursor = database.query(DatabaseHelper.TABLE_WEATHER,
                weatherAllColumn, null, null,
                null, null, null);
    }

    // прочитать данные по определенному ID
    public WeatherDBStructure getPosition(int position) {
        cursor.moveToPosition(position);
        return cursorToWeather();
    }

    // получить количество строк в таблице
    public int getCount() {
        return cursor.getCount();
    }

    // преобразователь курсора в объект
    private WeatherDBStructure cursorToWeather() {

        WeatherDBStructure weatherDBStructure = new WeatherDBStructure();
        weatherDBStructure.setId(cursor.getLong(0));
        weatherDBStructure.setCity(cursor.getString(1));
        weatherDBStructure.setTemperature(cursor.getFloat(2));
        weatherDBStructure.setWind(cursor.getFloat(3));
        weatherDBStructure.setPressure(cursor.getInt(4));
        weatherDBStructure.setHumidity(cursor.getInt(5));
        weatherDBStructure.setDate(cursor.getString(6));
        weatherDBStructure.setTime(cursor.getLong(7));

        return weatherDBStructure;
    }
}
