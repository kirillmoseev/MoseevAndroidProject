package com.mirea.kt.ribo.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, "passwords.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE entries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category TEXT, " +
                "service TEXT, " +
                "login TEXT, " +
                "password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS entries");
        onCreate(db);
    }

    public void addEntry(String category, String service, String login, String encryptedPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category", category);
        values.put("service", service);
        values.put("login", login);
        values.put("password", encryptedPassword);
        db.insert("entries", null, values);
    }

    public List<Entry> getEntries(String category) {
        List<Entry> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, service, login, password FROM entries WHERE category = ? ORDER BY service",
                new String[]{category});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String service = cursor.getString(1);
            String login = cursor.getString(2);
            String password = cursor.getString(3);
            list.add(new Entry(id, service, login, password));
        }
        cursor.close();
        return list;
    }

    public void deleteEntry(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("entries", "id = ?", new String[]{String.valueOf(id)});
    }
}
