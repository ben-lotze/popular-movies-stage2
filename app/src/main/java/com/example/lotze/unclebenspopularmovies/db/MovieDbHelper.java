package com.example.lotze.unclebenspopularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Benjamin Lotze on 14.04.2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "favouritesDb.db";

    // increment after each change of the database schema
    private static final int VERSION = 5;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + FavoriteMovieContract.FavMovieEntry.TABLE_NAME + " ("
                + FavoriteMovieContract.FavMovieEntry._ID + " INTEGER PRIMARY KEY, "
                + FavoriteMovieContract.FavMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + FavoriteMovieContract.FavMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + FavoriteMovieContract.FavMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + FavoriteMovieContract.FavMovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "
                + FavoriteMovieContract.FavMovieEntry.COLUMN_TIMESTAMP_SAVED + " TIMESTAMP NOT NULL, "
//                + FavoriteMovieContract.FavMovieEntry.COLUMN_TIMESTAMP_SAVED + " DATE DEFAULT (datetime('now','localtime')), "
                + FavoriteMovieContract.FavMovieEntry.COLUMN_DATE_RELEASED + " TEXT NOT NULL"    // as parsed from TMDb
                + ");";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.FavMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
