package com.example.lotze.unclebenspopularmovies.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Benjamin Lotze on 14.04.2018.
 */

public class FavouriteMovieContentProvider extends ContentProvider {

    public static final int FAV_MOVIES = 100;
    public static final int FAV_MOVIE_WITH_ID = 101;

    private MovieDbHelper mTaskDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(FavouriteMovieContract.AUTHORITY, FavouriteMovieContract.PATH_FAVORITE_MOVIES, FAV_MOVIES);
        uriMatcher.addURI(FavouriteMovieContract.AUTHORITY,
                FavouriteMovieContract.PATH_FAVORITE_MOVIES + "/#", FAV_MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbHelper = new MovieDbHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();
        Log.e("ContentProvider", "sort order = " + sortOrder);

        int matchId = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (matchId) {
            case FAV_MOVIES:
                retCursor = db.query(FavouriteMovieContract.FavMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // default: throw exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // set notification URI on cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int matchId = sUriMatcher.match(uri);
        switch (matchId) {
            case FAV_MOVIES:
                long id = db.insert(FavouriteMovieContract.FavMovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavouriteMovieContract.FavMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri on insert: " + uri);
        }
        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted = 0; // starts as 0


        switch (match) {
            case FAV_MOVIE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                if (id != null) {
                    tasksDeleted = db.delete(FavouriteMovieContract.FavMovieEntry.TABLE_NAME,
                            FavouriteMovieContract.FavMovieEntry._ID + "=?", new String[]{id});
                }
                break;
            default:
                Log.e("ContentProvider", "delete failed");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            Log.e("ContentProvider", "delete successful");
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.e("ContentProvider", "delete failed");
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("update fav movie not yet implemented");
//        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
