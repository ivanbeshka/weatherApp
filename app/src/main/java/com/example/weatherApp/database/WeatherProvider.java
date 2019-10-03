package com.example.weatherApp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class WeatherProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.provider.SQLiteExample"; // URI authority
    private static final String WEATHER_PATH = "weather";   // URI path

    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + WEATHER_PATH);

    // Типы URI для определения запроса
    private static final int URI_ALL = 1;   // URI для всех записей
    private static final int URI_ID = 2;    // URI для конкретрной записи

    // Типы данных:
    // набор строк
    private static final String CONTACT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + WEATHER_PATH;
    // одна строка
    private static final String CONTACT_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + WEATHER_PATH;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, WEATHER_PATH, URI_ALL);
        uriMatcher.addURI(AUTHORITY, WEATHER_PATH + "/#", URI_ID);
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        String filter = extractFilterFromUri(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_WEATHER, projection, filter,
                selectionArgs, null, null, sortOrder);

        // Установим оповещение при изменении данных в CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALL:
                return CONTACT_CONTENT_TYPE;
            case URI_ID:
                return CONTACT_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) != URI_ALL)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID = db.insert(DatabaseHelper.TABLE_WEATHER, null, values);

        Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        String filter = extractFilterFromUri(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = db.delete(DatabaseHelper.TABLE_WEATHER, filter, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        String filter = extractFilterFromUri(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = db.update(DatabaseHelper.TABLE_WEATHER, values, filter, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private String extractFilterFromUri(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALL: return "";
            case URI_ID:
                String id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                return DatabaseHelper.COLUMN_ID + " = " + id;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }
}
