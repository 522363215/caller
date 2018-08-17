package com.example.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessagePictureDBProcess {

    private Context mContext;
    private SQLiteDatabase database;

    public MessagePictureDBProcess(Context context) {
        mContext = context;
        database = new MessagePictureDB(context).getWritableDatabase();
    }


    public void close(){
        database.close();
    }

    public void deletePicture(String url) {
        database.delete("picture", "drawable=?", new String[]{url});
    }

    public void addPicture(Picture place) {
        //db.execSQL("create table picture(name text,drawable text,path text)");
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", place.getId());
        contentValues.put("name", place.getName());
        contentValues.put("drawable", place.getDrawableRes());
        contentValues.put("path", place.getPath());
        contentValues.put("thumbnail", place.getThumbnail());
        database.insert("picture", null, contentValues);
    }

    public List<Picture> findLikePicture(String url) {
        List<Picture> list = new ArrayList();
        Cursor cursor = database.rawQuery("select * from picture where drawable like?", new String[]{"%" + url + "%"});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Picture(cursor.getString(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("drawable"))
                        ,cursor.getString(cursor.getColumnIndex("path")),cursor.getString(cursor.getColumnIndex("thumbnail"))
                        ));
            }
        }
        return list;
    }

    public Picture findPicture(String url) {
        Cursor cursor = database.rawQuery("select * from picture where drawable =?", new String[]{url});
        if (cursor == null || !cursor.moveToNext()) {
            return null;
        }
        return new Picture(cursor.getString(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("drawable"))
                ,cursor.getString(cursor.getColumnIndex("path")),cursor.getString(cursor.getColumnIndex("thumbnail"))
                );
    }

    public boolean haveThing(){
        Picture place = findPicture("");
        return place==null;
    }

}
