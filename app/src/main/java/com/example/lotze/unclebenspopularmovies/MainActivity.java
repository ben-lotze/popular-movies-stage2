package com.example.lotze.unclebenspopularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lotze.unclebenspopularmovies.data.MovieListModel;
import com.example.lotze.unclebenspopularmovies.tools.ItemOffsetDecoration;
import com.example.lotze.unclebenspopularmovies.tools.UrlHelpers;
import com.example.lotze.unclebenspopularmovies.dataHandlers.EndlessRecyclerViewScrollListener;
import com.example.lotze.unclebenspopularmovies.data.Language;
import com.example.lotze.unclebenspopularmovies.data.MovieTMDb;
import com.example.lotze.unclebenspopularmovies.dataHandlers.MoviesAdapter;
import com.example.lotze.unclebenspopularmovies.tools.NetworkStateReceiver;
import com.example.lotze.unclebenspopularmovies.tools.Helpers;
import com.example.lotze.unclebenspopularmovies.tools.JsonUtils;
import com.example.lotze.unclebenspopularmovies.tools.MovieTools;
import com.example.lotze.unclebenspopularmovies.tools.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MoviesAdapter.ListItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        NetworkStateReceiver.NetworkStateChangeListener {

    private static final String TAG = "MainActivity";

    private static final int LOADER_ID_MOVIES = 0;


    //    private int page = 0;   // page of current API call
    private int currentMenuItemId = R.id.nav_most_popular_movies;// default to most popular
    private boolean adapterSwapNecessary;


    private TextView tvNoConenction;
    private TextView tvNoApiKey;
    private ProgressBar loadingIndicator;

    private MovieListModel movieListModel;
    private RecyclerView rvMovies;
    private MoviesAdapter moviesAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    /**
     * language for genres and movie descriptions/titles (used in URL)
     */
    private Language language;
    private NetworkStateReceiver networkStateReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawerNavigation = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerNavigation, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerNavigation.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(currentMenuItemId);

        setActionBarTitle(currentMenuItemId);


        // fetch language from settings
        language = Helpers.loadLanguagePreference(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        // start showing only loading indicator
        loadingIndicator = findViewById(R.id.loading_indicator);
        rvMovies = findViewById(R.id.rv_movies_list);
        setUiStateLoading();
        tvNoConenction = findViewById(R.id.tv_no_connection);
        tvNoApiKey = findViewById(R.id.tv_no_api_key);


        // network state
        networkStateReceiver = new NetworkStateReceiver(this, this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);


        moviesAdapter = new MoviesAdapter(this);
        rvMovies.setAdapter(moviesAdapter);

        // read number of columns (portrait/landscape) from respective values-file
        int numberOfColumns = getResources().getInteger(R.integer.rv_column_count);
        GridLayoutManager layoutManager = new GridLayoutManager(this,
                numberOfColumns, GridLayoutManager.VERTICAL, false);
        rvMovies.setLayoutManager(layoutManager);
        // equal margins with item decoration
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        rvMovies.addItemDecoration(itemDecoration);

        // endless scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore() called");
                getSupportLoaderManager().restartLoader(LOADER_ID_MOVIES, null, loaderListenerMovies);
            }
        };
        rvMovies.addOnScrollListener(scrollListener);


        movieListModel = ViewModelProviders.of(this).get(MovieListModel.class);
        Log.d(TAG, "onCreate() -> MovieListModel size=" + movieListModel.getMovies().size());
        List<MovieTMDb> moviesFromModel = movieListModel.getMovies();
        if (moviesFromModel != null && moviesFromModel.size() > 0) {
            moviesAdapter.swapMovies(moviesFromModel);
//            page = movieListModel.getPage();
            Log.d(TAG, "onCreate() -> MovieListModel: swapped movies in adapter, page=" + movieListModel.getPage()
                    + ", size=" + moviesFromModel.size());
            setUiStateShowData();
        }

        // init loader only if there is no loader from previous activity -> no movies in the ViewModel
        // was only if before trying with ViewModel
        else if (NetworkUtils.internetConnectionAvailable(this)
                && UrlHelpers.apiKeyAvailable()) {
            MovieTools.loadGenres(language, this);
            Log.d("MainActivity", "Loader: onCreate() -> no movies in MovieModel -> initLoader()");
            getSupportLoaderManager().initLoader(LOADER_ID_MOVIES, null, loaderListenerMovies);
        }
        // both cases can be true at the same time if there is no internet
        else {
            if (!UrlHelpers.apiKeyAvailable()) {
                tvNoApiKey.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.INVISIBLE);
            }
            // TODO: this is always true ?
            if (!NetworkUtils.internetConnectionAvailable(this)) {
                loadingIndicator.setVisibility(View.INVISIBLE);
                tvNoConenction.setVisibility(View.VISIBLE);
            }
        }


    }

    private void setUiStateLoading() {
        rvMovies.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void setUiStateShowData() {
        tvNoConenction.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
        rvMovies.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_best_rated_movies) {
            checkCategoryChangeAndReload(itemId);
        } else if (itemId == R.id.nav_most_popular_movies) {
            checkCategoryChangeAndReload(itemId);
        } else if (itemId == R.id.nav_favorite_movies) {
            Intent startFavMoviesActivity = new Intent(this, FavoriteMoviesActivity.class);
            startActivity(startFavMoviesActivity);
        } else if (itemId == R.id.nav_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
        } else if (itemId == R.id.nav_about_info) {
            Intent startAboutActivity = new Intent(this, AboutActivity.class);
            startActivity(startAboutActivity);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * checks if category has changed and reloads data only if change occurred
     *
     * @param itemIdSelected of NavigationDrawer item
     */
    public void checkCategoryChangeAndReload(int itemIdSelected) {

        // only reload if endpoint changed, do nothing if user clicks again on same menu point
        if (itemIdSelected != currentMenuItemId) {
            Log.d("MainActivity", "different menu item selected, reloading");
            currentMenuItemId = itemIdSelected;    // to call loader with correct URL

            movieListModel.setPage(1);
            adapterSwapNecessary = true;

            // ui changes
            setActionBarTitle(currentMenuItemId);
            setUiStateLoading();

            if (NetworkUtils.internetConnectionAvailable(this)) {
                getSupportLoaderManager().restartLoader(LOADER_ID_MOVIES, null, loaderListenerMovies);
            } else {
                // will start loading automatically when connection is back through NetworkStateReceiver
                loadingIndicator.setVisibility(View.INVISIBLE);
                tvNoConenction.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onMoviesListItemClick(int clickedItemIndex) {
        Log.d("MainActivity", "movie clicked, starting detail activity");

        // get movie to show
        MovieTMDb movie = moviesAdapter.getMovieAtPosition(clickedItemIndex);

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieTMDb.PARCELABLE_KEY, movie);
        startActivity(intent);
    }


    public void setActionBarTitle(int itemId) {
        if (itemId == R.id.nav_best_rated_movies) {
            getSupportActionBar().setTitle(getString(R.string.action_item_movies_best_rating));
        } else if (itemId == R.id.nav_most_popular_movies) {
            getSupportActionBar().setTitle(getString(R.string.action_item_movies_most_popular));
        } else if (itemId == R.id.nav_favorite_movies) {
            getSupportActionBar().setTitle(getString(R.string.action_item_movies_favorites));
        }
    }


    private LoaderManager.LoaderCallbacks<List<MovieTMDb>> loaderListenerMovies =
            new LoaderManager.LoaderCallbacks<List<MovieTMDb>>() {

                @Override
                public Loader<List<MovieTMDb>> onCreateLoader(int id, Bundle args) {

                    return new AsyncTaskLoader<List<MovieTMDb>>(MainActivity.this) {

                        @Override
                        protected void onStartLoading() {
                            forceLoad();
                        }

                        @Override
                        public List<MovieTMDb> loadInBackground() {
                            movieListModel.incrementPage();
                            URL url = UrlHelpers.getUrlForMenuItemSelected(currentMenuItemId,
                                    movieListModel.getPage(), language);
                            // TODO: test to save current page in ViewModel
                            Log.d("MainActivity", "AsyncTaskLoader: loadInBackground() called: "
                                    + url.toString());
                            String responseStr = NetworkUtils.downloadJson(url);

                            List<MovieTMDb> movies = JsonUtils.parseJsonMoviesResult(responseStr,
                                    MainActivity.this);
                            return movies;
                        }

                        @Override
                        public void deliverResult(List<MovieTMDb> movies) {
                            super.deliverResult(movies);
                        }
                    };

                }

                @Override
                public void onLoadFinished(Loader<List<MovieTMDb>> loader, List<MovieTMDb> movies) {

                    Log.d("MainActivity", "Loader: onLoadFinished() " +
                            "-> received " + movies.size() + " movies" +
                            ", adapter size=" + moviesAdapter.getItemCount() +
                            ", movieModel size=" + movieListModel.getMovieCount());

                    if (adapterSwapNecessary) {
                        Log.d("MainActivity", "Loader: onLoadFinished() -> swapping");
                        movieListModel.setMovies(movies);
                        moviesAdapter.swapMovies(movies);
                        scrollListener.resetState();
                        adapterSwapNecessary = false;
                    } else {
                        Log.d("MainActivity", "Loader: onLoadFinished() -> adding movies");

                        // TODO: find out why this does not work after phone rotation
                        /* After rotation this adds movies two times! Each page of 20 movies then adds
                         * 40 movies to the RecyclerView. Before rotating the phone everything is fine
                         * for any number of loaded pages.
                         */
//                        movieListModel.addMovies(movies);
//                        moviesAdapter.addMovies(movies);

                        // FIXED bug by using swap instead of adding (reason unclear),
                        // log is surprisingly unhelpful
                        List<MovieTMDb> previousMovies = moviesAdapter.getMovies();
                        previousMovies.addAll(movies);
                        movieListModel.setMovies(previousMovies);
                        moviesAdapter.swapMovies(previousMovies);
                    }

                    Log.d(TAG, "onLoadFinished() " +
                            "-> MovieListModel size=" + movieListModel.getMovieCount() +
                            ", adapter size=" + moviesAdapter.getItemCount()
                    );

                    setUiStateShowData();
                }

                @Override
                public void onLoaderReset(Loader<List<MovieTMDb>> loader) {
                    Log.d("MainActivity", "Loader: onLoaderReset() called");
                }
            };


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // when language setting changed reload current endpoint with page 1
        Log.d("MainActivity", "pref key changed: " + key);
        if (key.equals(getString(R.string.pref_languages_key))) {
            language = Helpers.loadLanguagePreference(this);
            Log.d("MainActivity", "changed language to " + language.getLanguageCode());
            // old movies must be removed, new load started
            movieListModel.setPage(1);
            adapterSwapNecessary = true;
            getSupportLoaderManager().restartLoader(LOADER_ID_MOVIES, null, loaderListenerMovies);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() -> MovieListModel size=" + movieListModel.getMovies().size());
        // unregister MainActivity as an OnPreferenceChangedListener to avoid memory leaks
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() -> MovieListModel size=" + movieListModel.getMovies().size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);

        Log.d(TAG, "onResume() -> MovieListModel size=" + movieListModel.getMovies().size());
    }


    @Override
    public void onNetworkStateChanged(boolean networkAvailable) {
        Log.d("MainActivity", "network state changed: available? " + networkAvailable);
        if (networkAvailable) {
            getSupportLoaderManager().restartLoader(LOADER_ID_MOVIES, null, loaderListenerMovies);
        }
    }


}
