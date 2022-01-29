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

public class PermissionDB extends SQLiteOpenHelper {

    public PermissionDB(Context context){
        super(context, "permission.db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL("CREATE Table PermissionData (" +
                "id INTEGER primary key AUTOINCREMENT, " +
                "host TEXT, notification INTEGER, " +
                "brightness INTEGER, vibrate INTEGER, " +
                "calc INTEGER," +
                "torch INTEGER, uikit INTEGER" +
                ")");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP Table if exists PermissionData");
    }

    public boolean addHostPermission(String host){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put("host",host);
        data.put("notification",0);
        data.put("torch",0);
        data.put("calc",1);
        data.put("vibrate",0);
        data.put("torch",0);
        data.put("uikit",1);

        long result = db.insert("PermissionData", null, data);
        return  (result != -1);
    }

    public boolean updateHostPermission(String host,String type,int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(type,value);
        long result = db.update("PermissionData", data, "host=?",new String[]{host});
        return  (result != -1);
    }

    public  String getHostPermission(String host, String type){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+type+" FROM PermissionData WHERE host = ?", new String[]{host});

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

    public String getHostPermission(String host){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,host,brightness,vibrate,torch,uikit,notification,calc FROM PermissionData WHERE host = ?", new String[]{host});
        int rowCount = cursor.getCount();
        StringBuffer response = new StringBuffer("");
        if(rowCount != 0){
            StringBuffer buffer[] = new StringBuffer[rowCount];
            int i=0;
            for(i=0;i<rowCount;i++){
                buffer[i] = new StringBuffer("");
            }
            i=0;
            while(cursor.moveToNext()){
                buffer[i].append("\"id\":\""+cursor.getString(0) + "\",");
                buffer[i].append("\"host\":\""+cursor.getString(1) + "\",");
                buffer[i].append("\"brightness\":\""+cursor.getString(2) + "\",");
                buffer[i].append("\"vibrate\":\""+cursor.getString(3) + "\",");
                buffer[i].append("\"torch\":\""+cursor.getString(4) + "\",");
                buffer[i].append("\"uikit\":\""+cursor.getString(5) + "\",");
                buffer[i].append("\"notification\":\""+cursor.getString(6) + "\",");
                buffer[i].append("\"calc\":\""+cursor.getString(7) + "\"");
                i++;
            }
            response.append("[");
            for(i=0;i<buffer.length;i++) {
                response.append("{" + buffer[i].toString() + "}");
                if(i != buffer.length-1){
                    response.append(",");
                }
            }
            response.append("]");
        } else {
            response = new StringBuffer("[]");
        }
        return  response.toString();
    }

    public boolean clearHostPermission(String host){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;
        if(host.length() > 0){
            result = db.delete("PermissionData", "host=?", new String[]{host});
        }
        return (result != -1);
    }
}

