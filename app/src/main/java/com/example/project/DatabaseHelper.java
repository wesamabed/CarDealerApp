package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD_HASH = "password_hash";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_CITY = "city";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FIRST_NAME + " TEXT, " +
                    COLUMN_LAST_NAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD_HASH + " TEXT, " +
                    COLUMN_PHONE_NUMBER + " TEXT, " +
                    COLUMN_GENDER + " TEXT, " +
                    COLUMN_COUNTRY + " TEXT, " +
                    COLUMN_CITY + " TEXT" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
    public void resetUsersTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD_HASH, user.getPasswordHash());
        values.put(COLUMN_PHONE_NUMBER, user.getPhone_number());
        values.put(COLUMN_GENDER, user.getGender());
        values.put(COLUMN_COUNTRY, user.getCountry());
        values.put(COLUMN_CITY, user.getCity());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // returns true if insertion is successful
    }

    public void printAllUsersToLogcat() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_FIRST_NAME,
                COLUMN_LAST_NAME,
                COLUMN_EMAIL,
                COLUMN_PASSWORD_HASH,
                COLUMN_PHONE_NUMBER,
                COLUMN_GENDER,
                COLUMN_COUNTRY,
                COLUMN_CITY
        };

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String userData = "User: " +
                    "First Name: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)) + ", " +
                    "Last Name: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)) + ", " +
                    "Email: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)) + ", " +
                    "PhoneNumber: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)) + ", " +
                    "Gender: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)) + ", " +
                    "Country: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY)) + ", " +
                    "City: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY)) + ", " +
                    "Password Hash: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH));
            Log.d("DatabaseHelper", userData);
        }
        cursor.close();
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
