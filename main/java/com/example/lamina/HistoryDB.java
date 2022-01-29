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

public class HistoryDB extends SQLiteOpenHelper {

    public HistoryDB(Context context){
        super(context, "history.db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL("CREATE Table HistoryData (id INTEGER primary key AUTOINCREMENT, url TEXT, title TEXT, date_time TEXT, time__stamp INTEGER)");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP Table if exists HistoryData");
    }

    public boolean addToHistory(String url,String title){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        Date d = Calendar.getInstance().getTime();
        data.put("time__stamp",(int)d.getTime());
        data.put("url",url);
        data.put("title",title);
        data.put("date_time",new SimpleDateFormat("dd/MM/yyyy HH:MM").format(d).toString());
        long result = db.insert("HistoryData", null, data);
        return  (result != -1);
    }

    public String getHistory(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,url,title,date_time,time__stamp FROM HistoryData ORDER BY id DESC", null);
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
                buffer[i].append("\"url\":\""+cursor.getString(1) + "\",");
                buffer[i].append("\"title\":\""+cursor.getString(2) + "\",");
                buffer[i].append("\"date_time\":\""+cursor.getString(3) + "\",");
                buffer[i].append("\"time__stamp\":\""+cursor.getString(4) + "\"");
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

    public boolean clearHistory(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;
        if(id.length() > 0){
            result = db.delete("HistoryData", "id=?", new String[]{id});
        } else {
            result = db.delete("HistoryData", null, null);
        }
        return (result != -1);
    }
}

