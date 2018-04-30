package com.example.lotze.unclebenspopularmovies.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Benjamin Lotze on 14.04.2018.
 */

public class FavouriteMovieContract {

    // to know which ContentProvider to access
    public static final String AUTHORITY = "com.example.lotze.unclebenspopularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_FAVORITE_MOVIES = "favorites";


    /* FavMovieEntry is an inner class that defines the contents of the task table */
    public static final class FavMovieEntry implements BaseColumns {

        // FavMovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();


        public static final String TABLE_NAME = "favorites";

        // plus "_ID" column from interface BaseColumns
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_TIMESTAMP_SAVED = "timestamp";
        public static final String COLUMN_DATE_RELEASED = "date_released";


    }

}
