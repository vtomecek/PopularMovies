package com.github.vtomecek.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.vtomecek.popularmovies.data.FavouritesContract.*;

/**
 * Created by vlado on 3/23/17.
 */

public class FavouritesDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "favourites.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold waitlist data
        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + FavouritesEntry.TABLE_NAME + " (" +
                FavouritesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavouritesEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
                FavouritesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavouritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavouritesEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                FavouritesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavouritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavouritesEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavouritesEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                FavouritesEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                FavouritesEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " +
                FavouritesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                FavouritesEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouritesEntry.TABLE_NAME);
        //onCreate(sqLiteDatabase);
    }
}