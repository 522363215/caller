package com.example.message;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessagePictureDB extends SQLiteOpenHelper {


    public MessagePictureDB(Context context) {
        super(context, "messagepicture.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table picture(id text,name text,drawable text,path text,thumbnail text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
