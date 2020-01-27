package com.example.postcreator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.postcreator.model.PostModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DataBaseHelper";

    private static final String DB_NAME = "poster";
    private static final int DB_VERSION = 1;

    private static final String TBL_POST = "tbl_post";
    private static final String _ID = "_id";
    private static final String POST_TEXT = "title";
    private static final String POST_TEXT_SIZE = "title_size";
    private static final String POST_TEXT_COLOR = "title_color";
    private static final String POST_BG = "bg_image";
    private static final String POST_IMG = "post_image";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlPost = "CREATE TABLE " + TBL_POST + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + POST_TEXT + " VARCHAR,"
                + POST_TEXT_SIZE + " VARCHAR,"
                + POST_TEXT_COLOR + " VARCHAR,"
                + POST_BG + " BLOG,"
                + POST_IMG + " BLOG"
                + ");";
        db.execSQL(sqlPost);
    }


    public boolean addPoster(String title, String size, String color, byte[] bgImage, byte[] image) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POST_TEXT, title);
        contentValues.put(POST_BG, bgImage);
        contentValues.put(POST_TEXT_SIZE, size);
        contentValues.put(POST_TEXT_COLOR, color);
        contentValues.put(POST_IMG, image);
        db.insert(TBL_POST, null, contentValues);
        db.close();
        return true;
    }

    public Cursor getId(String bookTitle) {
        SQLiteDatabase db = getWritableDatabase();
        //Cursor res = db.rawQuery("SELECT "+_ID+" FROM "+TBL_POST+" WHERE " +POST_TEXT+" = "+bookTitle,null);
        Cursor res = db.rawQuery("select * from " + TBL_POST + " WHERE " + POST_TEXT + " = '" + bookTitle + "'", null);
        return res;
    }


    public List<PostModel> getBookData() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TBL_POST, null);
        List<PostModel> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(_ID));
                String title = cursor.getString(cursor.getColumnIndex(POST_TEXT));
                String color = cursor.getString(cursor.getColumnIndex(POST_TEXT_COLOR));
                String size = cursor.getString(cursor.getColumnIndex(POST_TEXT_SIZE));
                byte[] bgImage = cursor.getBlob(cursor.getColumnIndex(POST_BG));
                byte[] postImage = cursor.getBlob(cursor.getColumnIndex(POST_IMG));

                list.add(new PostModel(id, title, size, color, bgImage, postImage));
                cursor.moveToNext();
            }
        }
        return list;
    }

    public void deleteBook() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TBL_POST);
        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlBOOK = "DROP TABLE IF EXISTS " + TBL_POST;
        db.execSQL(sqlBOOK);
        onCreate(db);
    }
}