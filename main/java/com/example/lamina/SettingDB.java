package com.example.lamina;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingDB extends SQLiteOpenHelper {

    public SettingDB(Context context){
        super(context, "setting.db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL("CREATE Table Setting_Data (default_search_engine TEXT, last_visited_url TEXT, dark_theme INTEGER)");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP Table if exists Setting_Data");
    }

    public boolean setLastVisitedUrl(String url){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_visited_url FROM Setting_Data", null);
        int rowCount = cursor.getCount();
        int result = 0;
        if(rowCount == 0){
            ContentValues data = new ContentValues();
            data.put("last_visited_url",url);
            data.put("default_search_engine","");
            data.put("dark_theme",0);
            db.insert("Setting_Data",null,data);
        } else {
            ContentValues data = new ContentValues();
            data.put("last_visited_url",url);
            result = db.update("Setting_Data",data, null, null);
        }
        return  (result != -1);
    }

    public String getLastVisitedUrl(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_visited_url FROM Setting_Data", null);
        int rowCount = cursor.getCount();
        StringBuffer response;
        if(rowCount != 0){
            cursor.moveToFirst();
            response = new StringBuffer(cursor.getString(0));
        } else {
            response = new StringBuffer("");
        }
        return  response.toString();
    }

    public boolean setDefaultSearchEngine(String default_search_engine){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;
        Cursor cursor = db.rawQuery("SELECT default_search_engine FROM Setting_Data", null);
        int rowCount = cursor.getCount();
        if(rowCount == 0){
            ContentValues data = new ContentValues();
            data.put("default_search_engine","google");
            data.put("last_visited_url","");
            data.put("dark_theme",0);
            db.insert("Setting_Data",null,data);
        } else {
            ContentValues data = new ContentValues();
            data.put("default_search_engine",default_search_engine);
            db.update("Setting_Data",data,null,null);
        }
        return (result!=-1);
    }

    public String getDefaultSearchEngine(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT default_search_engine FROM Setting_Data", null);
        int rowCount = cursor.getCount();
        StringBuffer response;
        if(rowCount != 0){
            cursor.moveToFirst();
            response = new StringBuffer(cursor.getString(0));
        } else {
            response = new StringBuffer("");
        }
        return  response.toString();
    }

    public boolean setDarkThemeSetting(int value){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;
        Cursor cursor = db.rawQuery("SELECT dark_theme FROM Setting_Data", null);
        int rowCount = cursor.getCount();
        if(rowCount == 0){
            ContentValues data = new ContentValues();
            data.put("default_search_engine","google");
            data.put("last_visited_url","");
            data.put("dark_theme",value);
            db.insert("Setting_Data",null,data);
        } else {
            ContentValues data = new ContentValues();
            data.put("dark_theme",value);
            db.update("Setting_Data",data,null,null);
        }
        return (result!=-1);
    }

    public String isDarkTheme(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT dark_theme FROM Setting_Data", null);
        int rowCount = cursor.getCount();
        StringBuffer response;
        if(rowCount != 0){
            cursor.moveToFirst();
            response = new StringBuffer(cursor.getString(0));
        } else {
            response = new StringBuffer("");
        }
        return  response.toString();
    }
}

