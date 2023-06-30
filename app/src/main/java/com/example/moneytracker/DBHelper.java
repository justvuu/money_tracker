package com.example.moneytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String COLUMN_USER_INCOME = "income";

    private static final String COLUMN_USER_EXPENSE = "expense";


    private static final String TABLE_TRANSACTION = "transactions";
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_TRANSACTION_AMOUNT = "amount";
    private static final String COLUMN_TRANSACTION_DATE = "date";
    private static final String COLUMN_TRANSACTION_DESCRIPTION = "description";
    private static final String COLUMN_USER_FK = "user_id_fk";
    private static final String COLUMN_CATEGORY_FK = "category_id_fk";

    private static final String TABLE_CATEGORY = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CATEGORY_TYPE = "type";



    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_USER_INCOME + " REAL,"
            + COLUMN_USER_EXPENSE + " REAL"
            + ")";

    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "("
            + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CATEGORY_NAME + " TEXT,"
            + COLUMN_CATEGORY_TYPE + " INTEGER,"
            + COLUMN_USER_FK + " INTEGER"
            + ")";
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION + "("
            + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TRANSACTION_AMOUNT + " REAL,"
            + COLUMN_CATEGORY_FK + " TEXT,"
            + COLUMN_TRANSACTION_DATE + " TEXT,"
            + COLUMN_TRANSACTION_DESCRIPTION + " TEXT,"
            + COLUMN_USER_FK + " INTEGER,"
            + "FOREIGN KEY (" + COLUMN_USER_FK + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "),"
            + "FOREIGN KEY (" + COLUMN_CATEGORY_FK + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + ")"
            + ")";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION + 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("ALTER TABLE " + TABLE_CATEGORY + " ADD COLUMN " + COLUMN_USER_FK + " INTEGER");
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
        values.put(COLUMN_USER_INCOME, user.getIncome());
        values.put(COLUMN_USER_EXPENSE, user.getExpense());
        long id = db.insert(TABLE_USER, null, values);

        cursor.close();
        db.close();
        return id;
    }

    public int login(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int userId = -1;
        try {
            // Define the columns to retrieve
            String[] projection = { COLUMN_USER_ID, COLUMN_USERNAME };

            // Define the selection criteria
            String selection = "username = ? AND password = ?";

            // Define the selection arguments
            String[] selectionArgs = { username, password };

            // Query the User table
            cursor = db.query(TABLE_USER, projection, selection, selectionArgs, null, null, null);

            // Check if the cursor has any rows
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return userId;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        User user = null;

        try {
            // Define the columns to retrieve
            String[] projection = {COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_USER_INCOME, COLUMN_USER_EXPENSE};

            // Define the selection criteria
            String selection = COLUMN_USER_ID + " = ?";

            // Define the selection argument
            String[] selectionArgs = {String.valueOf(userId)};

            // Query the User table
            cursor = db.query(TABLE_USER, projection, selection, selectionArgs, null, null, null);

            // Check if the cursor has any rows
            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve the user attributes from the cursor
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                double income = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_INCOME));
                double expense = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_EXPENSE));

                // Create a new User object with the retrieved attributes
                user = new User(id, username, password, income, expense);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return user;
    }

    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = null;
        boolean isOldPasswordCorrect = false;

        try {
            db = this.getWritableDatabase();

            // Check if the old password is correct
            isOldPasswordCorrect = checkOldPassword(db, userId, oldPassword);
            if (!isOldPasswordCorrect) {
                return false; // Old password is incorrect, return false
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_PASSWORD, newPassword);

            String whereClause = COLUMN_USER_ID + " = ?";
            String[] whereArgs = { String.valueOf(userId) };

            int rowsAffected = db.update(TABLE_USER, values, whereClause, whereArgs);

            return rowsAffected > 0;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private boolean checkOldPassword(SQLiteDatabase db, int userId, String oldPassword) {
        String selection = COLUMN_USER_ID + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { String.valueOf(userId), oldPassword };

        Cursor cursor = db.query(TABLE_USER, null, selection, selectionArgs, null, null, null);

        boolean isOldPasswordCorrect = cursor.getCount() > 0;

        cursor.close();

        return isOldPasswordCorrect;
    }

    public void updateUserIncome(int userId, double newIncome) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_INCOME, newIncome);

        String whereClause = COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        db.update(TABLE_USER, values, whereClause, whereArgs);

        db.close();
    }

    public void updateUserExpense(int userId, double newExpense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EXPENSE, newExpense);

        String whereClause = COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        db.update(TABLE_USER, values, whereClause, whereArgs);

        db.close();
    }
    public void deleteUserByUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
    }

    public long AddCategory(int userIdFk, String name, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);
        values.put(COLUMN_CATEGORY_TYPE, type);
        values.put(COLUMN_USER_FK, userIdFk);
        long id = db.insert(TABLE_CATEGORY, null, values);
        db.close();
        return id;
    }

    public List<Category> getAllCategories(int userIdFk) {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_CATEGORY_ID,
                COLUMN_CATEGORY_NAME,
                COLUMN_CATEGORY_TYPE,
                COLUMN_USER_FK
        };

        String selection = COLUMN_USER_FK + " = ?";
        String[] selectionArgs = { String.valueOf(userIdFk) };

        Cursor cursor = db.query(
                TABLE_CATEGORY,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        try {


            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME);
                int typeIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_TYPE);
                int userIdIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_FK);
                do {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    int type = cursor.getInt(typeIndex);
                    int userId = cursor.getInt(userIdIndex);

                    Category category = new Category(id, userId, name, type);
                    categories.add(category);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return categories;
        }
        catch (Exception ex){
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return categories;
        }
    }
    public Category getCategoryById(int categoryId, int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Category category = null;

        String[] projection = { COLUMN_CATEGORY_ID,  COLUMN_CATEGORY_NAME, COLUMN_CATEGORY_TYPE, COLUMN_USER_FK };
        String selection = COLUMN_CATEGORY_ID + " = ? AND " + COLUMN_USER_FK + " = ?";
        String[] selectionArgs = { String.valueOf(categoryId), String.valueOf(userId) };

        Cursor cursor = db.query(TABLE_CATEGORY, projection, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_TYPE));
            int userIdVal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_FK));

            category = new Category(id, userIdVal, name, type);
        }

        if (cursor != null) {
            cursor.close();
        }

        return category;
    }

    public boolean updateCategory(int categoryId, int type, String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_TYPE, type);
        values.put(COLUMN_CATEGORY_NAME, categoryName);

        String whereClause = COLUMN_CATEGORY_ID + " = ?";
        String[] whereArgs = {String.valueOf(categoryId)};

        int rowsAffected = db.update(TABLE_CATEGORY, values, whereClause, whereArgs);

        db.close();

        return rowsAffected > 0;
    }


    public long AddTransaction(double amount, int categoryId, String transDate, int userId, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANSACTION_AMOUNT, amount);
        values.put(COLUMN_CATEGORY_FK, categoryId);
        values.put(COLUMN_TRANSACTION_DATE, transDate);
        values.put(COLUMN_USER_FK, userId);
        values.put(COLUMN_TRANSACTION_DESCRIPTION, description);
        long id = db.insert(TABLE_TRANSACTION, null, values);
        db.close();
        return id;
    }

    public boolean updateTransaction(int transactionId, double amount, int categoryId, String transDate, int userId, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANSACTION_AMOUNT, amount);
        values.put(COLUMN_CATEGORY_FK, categoryId);
        values.put(COLUMN_TRANSACTION_DATE, transDate);
        values.put(COLUMN_USER_FK, userId);
        values.put(COLUMN_TRANSACTION_DESCRIPTION, description);
        int rowsAffected = db.update(TABLE_TRANSACTION, values, COLUMN_TRANSACTION_ID + "=?", new String[]{String.valueOf(transactionId)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean deleteTransaction(int transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_TRANSACTION, COLUMN_TRANSACTION_ID + "=?", new String[]{String.valueOf(transactionId)});
        db.close();
        return rowsAffected > 0;
    }
    public List<Transaction> getTransactions() {
        List<Transaction> transactionList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] projection = { COLUMN_TRANSACTION_ID, COLUMN_TRANSACTION_AMOUNT, COLUMN_CATEGORY_FK, COLUMN_TRANSACTION_DATE, COLUMN_TRANSACTION_DESCRIPTION, COLUMN_USER_FK };

            // Define the selection criteria
            String selection = "user_id_fk = ?";

            // Define the selection argument
            String[] selectionArgs = { String.valueOf(1) };

            // Query the Transaction table
            cursor = db.query(TABLE_TRANSACTION, projection, selection, selectionArgs, null, null, null);

            // Iterate through the cursor and create Transaction objects
            if (cursor != null && cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID);
                int amountIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_AMOUNT);
                int categoryIdIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_FK);
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE);
                int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DESCRIPTION);
                int userIdIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_FK);
                do {
                    int id = cursor.getInt(idIndex);
                    double amount = cursor.getDouble(amountIndex);
                    int categoryId = cursor.getInt(categoryIdIndex);
                    String date = cursor.getString(dateIndex);
                    int userId = cursor.getInt(userIdIndex);
                    String description = cursor.getString(descriptionIndex);

                    Transaction transaction = new Transaction(id, amount, categoryId, date, description, userId);
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return transactionList;
    }

    public Transaction getTransactionById(int transactionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Transaction transaction = null;

        String[] projection = {
                COLUMN_TRANSACTION_ID,
                COLUMN_TRANSACTION_AMOUNT,
                COLUMN_CATEGORY_FK,
                COLUMN_TRANSACTION_DATE,
                COLUMN_TRANSACTION_DESCRIPTION,
                COLUMN_USER_FK
        };

        String selection = COLUMN_TRANSACTION_ID + " = ?";
        String[] selectionArgs = { String.valueOf(transactionId) };

        Cursor cursor = db.query(
                TABLE_TRANSACTION,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID);
            int amountIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_AMOUNT);
            int categoryIdIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_FK);
            int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE);
            int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DESCRIPTION);
            int userIdIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_FK);

            int id = cursor.getInt(idIndex);
            double amount = cursor.getDouble(amountIndex);
            int categoryId = cursor.getInt(categoryIdIndex);
            String date = cursor.getString(dateIndex);
            String description = cursor.getString(descriptionIndex);
            int userId = cursor.getInt(userIdIndex);

            transaction = new Transaction(id, amount, categoryId, date, description, userId);

            cursor.close();
        }

        db.close();

        return transaction;
    }



    public List<Transaction> getRecentTransactions(int userIdFk) {
        List<Transaction> transactionList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] projection = { COLUMN_TRANSACTION_ID, COLUMN_TRANSACTION_AMOUNT, COLUMN_CATEGORY_FK, COLUMN_TRANSACTION_DATE, COLUMN_TRANSACTION_DESCRIPTION, COLUMN_USER_FK };

            // Define the selection criteria
            String selection = "user_id_fk = ?";

            // Define the selection argument
            String[] selectionArgs = { String.valueOf(userIdFk) };

            String sortOrder = "date DESC LIMIT 6";
            // Query the Transaction table
            cursor = db.query(TABLE_TRANSACTION, projection, selection, selectionArgs, null, null, sortOrder);

            // Iterate through the cursor and create Transaction objects
            if (cursor != null && cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID);
                int amountIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_AMOUNT);
                int categoryIdIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_FK);
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE);
                int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DESCRIPTION);
                int userIdIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_FK);
                do {
                    int id = cursor.getInt(idIndex);
                    double amount = cursor.getDouble(amountIndex);
                    int categoryId = cursor.getInt(categoryIdIndex);
                    String date = cursor.getString(dateIndex);
                    int userId = cursor.getInt(userIdIndex);
                    String description = cursor.getString(descriptionIndex);

                    Transaction transaction = new Transaction(id, amount, categoryId, date, description, userId);
                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return transactionList;
    }

    public List<Transaction> getTransactionsByMonthAndUserId(String month, int userId) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] projection = {COLUMN_TRANSACTION_ID, COLUMN_TRANSACTION_AMOUNT, COLUMN_CATEGORY_FK, COLUMN_TRANSACTION_DATE, COLUMN_TRANSACTION_DESCRIPTION, COLUMN_USER_FK};

            // Define the selection criteria
//            String selection = "strftime('%m', date(" + COLUMN_TRANSACTION_DATE + ")) = ? AND " + COLUMN_USER_FK + " = ?";
            String selection = "SUBSTR(" + COLUMN_TRANSACTION_DATE + ", 1, INSTR(" + COLUMN_TRANSACTION_DATE + ", '/') - 1) = ? AND " + COLUMN_USER_FK + " = ?";

            // Define the selection arguments
            String[] selectionArgs = {month, String.valueOf(userId)};
            String sortOrder = "date DESC";

            // Query the transaction table
            cursor = db.query(TABLE_TRANSACTION, projection, selection, selectionArgs, null, null, sortOrder);

            // Loop through the cursor and create Transaction objects
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_AMOUNT));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_FK));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DATE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_DESCRIPTION));

                    // Create a Transaction object
                    Transaction transaction = new Transaction(id, amount, categoryId, date, description, userId);
                    transactions.add(transaction);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception ex){
            System.out.println(ex);
        }

        finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return transactions;
    }

    public List<CategoryTotal> getIncomeCategoryTotals(String month, int userId) {
        List<CategoryTotal> categoryTotals = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] projection = {
                    COLUMN_CATEGORY_FK,
                    "SUM(amount) AS total_amount"
            };
            String selection = "SUBSTR(" + COLUMN_TRANSACTION_DATE + ", 1, INSTR(" + COLUMN_TRANSACTION_DATE + ", '/') - 1) = ? AND " + COLUMN_USER_FK + " = ?";

            // Define the selection arguments
            String[] selectionArgs = {month, String.valueOf(userId)};
            String groupBy = COLUMN_CATEGORY_FK;
            String sortOrder = "total_amount DESC";

            // Query the Transactions table
            cursor = db.query(
                    TABLE_TRANSACTION,
                    projection,
                    selection,
                    selectionArgs,
                    groupBy,
                    null,
                    sortOrder,
                    null
            );

            // Iterate over the cursor and create CategoryTotal objects
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_FK));
                    double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));

                    // Get the category name based on the category ID
                    Category category = getCategoryById(categoryId, userId);
                    if(category.getType() == 0) continue;
                    String categoryName = category.getName();

                    CategoryTotal categoryTotal = new CategoryTotal(categoryName, totalAmount);
                    categoryTotals.add(categoryTotal);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return categoryTotals;

    }

    public List<CategoryTotal> getExpenseCategoryTotals(String month, int userId) {
        List<CategoryTotal> categoryTotals = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] projection = {
                    COLUMN_CATEGORY_FK,
                    "SUM(amount) AS total_amount"
            };
            String selection = "SUBSTR(" + COLUMN_TRANSACTION_DATE + ", 1, INSTR(" + COLUMN_TRANSACTION_DATE + ", '/') - 1) = ? AND " + COLUMN_USER_FK + " = ?";

            // Define the selection arguments
            String[] selectionArgs = {month, String.valueOf(userId)};
            String groupBy = COLUMN_CATEGORY_FK;
            String sortOrder = "total_amount DESC";

            // Query the Transactions table
            cursor = db.query(
                    TABLE_TRANSACTION,
                    projection,
                    selection,
                    selectionArgs,
                    groupBy,
                    null,
                    sortOrder,
                    null
            );

            // Iterate over the cursor and create CategoryTotal objects
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_FK));
                    double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));

                    // Get the category name based on the category ID
                    Category category = getCategoryById(categoryId, userId);
                    if(category.getType() == 1) continue;
                    String categoryName = category.getName();

                    CategoryTotal categoryTotal = new CategoryTotal(categoryName, totalAmount);
                    categoryTotals.add(categoryTotal);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return categoryTotals;

    }





}
