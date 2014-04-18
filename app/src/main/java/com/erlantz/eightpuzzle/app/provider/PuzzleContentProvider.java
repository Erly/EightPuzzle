package com.erlantz.eightpuzzle.app.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by erlantz on 4/04/14.
 */
public class PuzzleContentProvider extends ContentProvider {

    private SQLiteDatabase database;
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Puzzles";

    private static final String CREATE_TABLE = "CREATE TABLE " +
            PuzzleContract.PUZZLES_TABLE_NAME + " (" +
            PuzzleContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PuzzleContract.PUZZLE_NAME + " TEXT NOT NULL, " +
            PuzzleContract.PUZZLE_BITMAP_PATH + " TEXT NOT NULL, " +
            PuzzleContract.PUZZLE_THUMB_PATH + " TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PuzzleContract.PUZZLES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return (null != database);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PuzzleContract.PUZZLES_TABLE_NAME);
        Cursor c = qb.query(database, null, null, null, null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        // Unimplemented
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = database.insert(PuzzleContract.PUZZLES_TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(PuzzleContract.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = database.delete(PuzzleContract.PUZZLES_TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(PuzzleContract.CONTENT_URI, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = database.update(PuzzleContract.PUZZLES_TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(PuzzleContract.CONTENT_URI, null);
        return rowsUpdated;
    }
}
