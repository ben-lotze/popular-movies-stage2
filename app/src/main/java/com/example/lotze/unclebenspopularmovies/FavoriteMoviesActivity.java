package com.example.lotze.unclebenspopularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lotze.unclebenspopularmovies.data.MovieTMDb;
import com.example.lotze.unclebenspopularmovies.dataHandlers.FavouriteMoviesAdapter;
import com.example.lotze.unclebenspopularmovies.db.FavouriteMovieContract;

public class FavouriteMoviesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        FavouriteMoviesAdapter.ListItemClickListener {

    private static final String TAG = "FavouriteMoviesActivity";
    private static final int FAV_MOVIES_LOADER_ID = 0;
    private FavouriteMoviesAdapter adapter;

    private String ORDER_BY_COLUMN = FavouriteMovieContract.FavMovieEntry.COLUMN_TITLE;
    private String ORDER_ASC_DESC = "ASC";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.action_item_movies_favorites);
        }

        RecyclerView rvFavoriteMovies = findViewById(R.id.rv_favorite_movies);
        adapter = new FavouriteMoviesAdapter(this);
        rvFavoriteMovies.setAdapter(adapter);
        int numberOfColumns = getResources().getInteger(R.integer.rv_column_count);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        rvFavoriteMovies.setLayoutManager(layoutManager);

        getSupportLoaderManager().initLoader(FAV_MOVIES_LOADER_ID, null, this);


        // swiping to remove favorites
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = (int) viewHolder.itemView.getTag(R.id.id_movie_fav_id);
                Log.d(TAG, "starting to delete favorite movie with fav id=" + id);

                Uri uri = FavouriteMovieContract.FavMovieEntry.CONTENT_URI.buildUpon().appendPath("" + id).build();
                int rowsDeleted = getContentResolver().delete(uri, null, null);
                if (rowsDeleted > 0) {
                    // restart loader to re-query
                    getSupportLoaderManager().restartLoader(FAV_MOVIES_LOADER_ID, null,
                            FavouriteMoviesActivity.this);
                }

            }
        }).attachToRecyclerView(rvFavoriteMovies);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle loaderArgs) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Cursor cache
//            Cursor mFavMovieData = null;

            @Override
            protected void onStartLoading() {
//                if (mFavMovieData != null) {
//                    // deliver previously loaded data if available
//                    deliverResult(mFavMovieData);
//                } else {
//                    // force new load if no data cached
//                    forceLoad();
//                }
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                Log.e(TAG, "loadInBackground() called, ORDER BY " + FavouriteMoviesActivity.this.ORDER_BY_COLUMN + " " + ORDER_ASC_DESC);
                try {
                    Uri uri = FavouriteMovieContract.FavMovieEntry.CONTENT_URI;
                    return getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            ORDER_BY_COLUMN + " " + ORDER_ASC_DESC);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
//                mFavMovieData = data;
                Log.e(TAG, "deliverResult() called");
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(TAG, "onLoadFinished() called -> swapping cursor");
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // re-queries all favorites
        getSupportLoaderManager().restartLoader(FAV_MOVIES_LOADER_ID, null, this);
    }


    @Override
    public void onMoviesListItemClick(int clickedItemIndex) {
        MovieTMDb movie = adapter.getMovieAtPosition(clickedItemIndex);
        Log.d(TAG, "clicked on fav movie at position " + clickedItemIndex + ", movieId=" + movie.getId());

        Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
        intent.putExtra(MovieTMDb.PARCELABLE_KEY, movie);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorites_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        // title defaults to ASCENDING sorting
        // date/timestamp sorting defaults to newest first
        if (itemId == R.id.action_sort_favs_title) {
            if (ORDER_BY_COLUMN.equals(FavouriteMovieContract.FavMovieEntry.COLUMN_TITLE)) {
                ORDER_ASC_DESC = "DESC";
            } else {
                ORDER_ASC_DESC = "ASC";
            }
            this.ORDER_BY_COLUMN = FavouriteMovieContract.FavMovieEntry.COLUMN_TITLE;
        } else if (itemId == R.id.action_sort_favs_release_date) {
            if (ORDER_BY_COLUMN.equals(FavouriteMovieContract.FavMovieEntry.COLUMN_DATE_RELEASED)) {
                ORDER_ASC_DESC = "ASC";
            } else {
                ORDER_ASC_DESC = "DESC";
            }
            this.ORDER_BY_COLUMN = FavouriteMovieContract.FavMovieEntry.COLUMN_DATE_RELEASED;
        } else if (itemId == R.id.action_sort_favs_timestamp_added) {
            if (ORDER_BY_COLUMN.equals(FavouriteMovieContract.FavMovieEntry.COLUMN_TIMESTAMP_SAVED)) {
                ORDER_ASC_DESC = "ASC";
            } else {
                ORDER_ASC_DESC = "DESC";
            }
            this.ORDER_BY_COLUMN = FavouriteMovieContract.FavMovieEntry.COLUMN_TIMESTAMP_SAVED;
        }

        // restart loader
        Log.d(TAG, "onOptionsItemSelected() restarting loader to query new sort order by " + ORDER_BY_COLUMN
                + " " + ORDER_ASC_DESC);
        getSupportLoaderManager().restartLoader(FAV_MOVIES_LOADER_ID, null, this);

        return super.onOptionsItemSelected(item);
    }
}

