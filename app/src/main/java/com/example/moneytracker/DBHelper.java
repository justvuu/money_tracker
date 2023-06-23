package com.example.moneytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_TRANSACTION = "transactions";
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_USER_FK = "user_id_fk";
    private static final String COLUMN_CATEGORY_FK = "category_id_fk";

    private static final String TABLE_CATEGORY = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";

    private static final String COLUMN_CATEGORY_TYPE = "type";


    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT"
            + ")";

    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "("
            + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CATEGORY_NAME + " TEXT,"
            + COLUMN_CATEGORY_TYPE + "INTEGER"
            + ")";
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION + "("
            + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_AMOUNT + " REAL,"
            + COLUMN_CATEGORY_FK + " TEXT,"
            + COLUMN_DATE + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_USER_FK + " INTEGER,"
            + "FOREIGN KEY (" + COLUMN_USER_FK + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "),"
            + "FOREIGN KEY (" + COLUMN_CATEGORY_FK + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + ")"
            + ")";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION + 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String alterTableQuery = "ALTER TABLE " + TABLE_CATEGORY + " ADD COLUMN " + COLUMN_CATEGORY_TYPE + " INTEGER";
        db.execSQL(alterTableQuery);
    }

    public long register(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the username already exists
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {user.getUsername()};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.getCount() > 0) {
            // Username already exists, return -1 to indicate failure
            cursor.close();
            db.close();
            return -1;
        }

        // Username doesn't exist, insert the new user
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        long id = db.insert(TABLE_USER, null, values);

        cursor.close();
        db.close();
        return id;
    }

    public boolean login(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] projection = { "username" };

            // Define the selection criteria
            String selection = "username = ? AND password = ?";

            // Define the selection arguments
            String[] selectionArgs = { username, password };

            // Query the User table
            cursor = db.query("Users", projection, selection, selectionArgs, null, null, null);

            // Check if the cursor has any rows
            if (cursor != null && cursor.moveToFirst()) {
                // Login successful, the user exists in the database
                return true;
            }

            // Login failed, the user does not exist or the credentials are incorrect
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }
    public void deleteUserByUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
    }

    public long AddCategory(String name, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);
        values.put(COLUMN_CATEGORY_TYPE, type);
        long id = db.insert(TABLE_CATEGORY, null, values);
        db.close();
        return id;
    }


}
