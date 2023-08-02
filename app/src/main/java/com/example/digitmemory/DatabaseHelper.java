package com.example.digitmemory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_HIGHSCORE = "highscore";
    private static final String COLUMN_LAST_ATTEMPTS = "last_attempts";

    private static final String ALTER_TABLE_ADD_LAST_ATTEMPTS =
            "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_LAST_ATTEMPTS + " TEXT";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USERNAME + " TEXT," +
                    COLUMN_PASSWORD + " TEXT," +
                    COLUMN_HIGHSCORE + " INTEGER," +
                    COLUMN_LAST_ATTEMPTS + " TEXT" + ")";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }


    //wird nur ausgeführt wenn zbsp eine neue Spalte als Datensatz hinzugefügt werden soll
    //zur aktualisierung der db-struktur
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create a new table with the updated structure
        onCreate(db);

        // Perform migration from old version to new version if needed
        if (newVersion > oldVersion) {
            if (oldVersion < 2) {

                db.execSQL(ALTER_TABLE_ADD_LAST_ATTEMPTS);
            }
            // Add more migration code for future versions if needed
        }
    }

    // Add a new user to the database
    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_HIGHSCORE, 0); // Set the initial high score to 0 for new users
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Check if the provided username and password match any user in the database
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Check if a username already exists in the database
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Get the user's high score from the database based on the username
    public int getUserHighScore(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userHighScore = 0;

        if (username != null) {
            Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_HIGHSCORE}, COLUMN_USERNAME + "=?",
                    new String[]{username}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                userHighScore = cursor.getInt(cursor.getColumnIndex(COLUMN_HIGHSCORE));
                cursor.close();
            }
        }

        db.close();
        return userHighScore;
    }

    // Update the user's high score in the database
    public int updateUserHighScore(String username, int newHighScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIGHSCORE, newHighScore);
        String whereClause = COLUMN_USERNAME + "=?";
        String[] whereArgs = {username};
        db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();

        // Return the updated high score
        return newHighScore;
    }

    // Get the last attempts for a user from the database
    public String getLastAttempts(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_LAST_ATTEMPTS};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = null;
        String lastAttempts = null;

        try {
            cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                lastAttempts = cursor.getString(cursor.getColumnIndex(COLUMN_LAST_ATTEMPTS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return lastAttempts;
    }

    // Update the last attempts for a user in the database
    public void updateLastAttempts(String username, String lastAttempts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_ATTEMPTS, lastAttempts);
        String whereClause = COLUMN_USERNAME + "=?";
        String[] whereArgs = {username};
        db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }
}
