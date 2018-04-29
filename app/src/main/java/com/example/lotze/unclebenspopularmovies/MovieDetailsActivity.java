package com.example.lotze.unclebenspopularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lotze.unclebenspopularmovies.data.Language;
import com.example.lotze.unclebenspopularmovies.data.MovieReview;
import com.example.lotze.unclebenspopularmovies.data.MovieTMDb;
import com.example.lotze.unclebenspopularmovies.data.TMDbImageSize;
import com.example.lotze.unclebenspopularmovies.dataHandlers.ReviewAdapter;
import com.example.lotze.unclebenspopularmovies.db.FavouriteMovieContract;
import com.example.lotze.unclebenspopularmovies.tools.JsonUtils;
import com.example.lotze.unclebenspopularmovies.tools.NetworkUtils;
import com.example.lotze.unclebenspopularmovies.tools.UrlHelpers;
import com.example.lotze.unclebenspopularmovies.data.Trailer;
import com.example.lotze.unclebenspopularmovies.dataHandlers.TrailerAdapter;
import com.example.lotze.unclebenspopularmovies.tools.Helpers;
import com.example.lotze.unclebenspopularmovies.tools.MovieTools;

import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity
        implements TrailerAdapter.ListItemClickListener {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private static MovieTMDb movie;
    private boolean isFavorite;
    private int favId;

    private ImageView ivBackdrop;
    private ImageView ivPoster;
    private TextView tvTitle;
    private TextView tvTitleOriginal;
    private TextView tvReleaseDate;
    private TextView tvGenres;
    private TextView tvVoteAverage;
    private TextView tvVoteCount;
    private TextView tvDescription;

//    private Button btnFavoriteToggle;
//    private Button btnSearchOn;

    private LinearLayout btnFavoriteToggle;
    private TextView btnFavoriteToggleText;
    private LinearLayout btnSearchOn;
    private TextView btnSearchOnText;

    private RecyclerView rvTrailers;
    private TrailerAdapter trailerAdapter;
    private ProgressBar progressTrailers;
    private TextView tvNoTrailers;

    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private ProgressBar progressReviews;
    private TextView tvNoReviews;

    private static final int LOADER_ID_MOVIE_DETAILS = 0;
    private LoaderManager.LoaderCallbacks<MovieTMDb> loaderListenerMovieDetails;
    private static final int LOADER_ID_FAVORITE_STATUS = 1;
    private LoaderManager.LoaderCallbacks<Boolean> loaderListenerFavoriteStatus;
    private static final int LOADER_ID_TRAILERS = 2;
    private LoaderManager.LoaderCallbacks<List<Trailer>> loaderListenerTrailers;
    private static final int LOADER_ID_REVIEWS = 3;
    private LoaderManager.LoaderCallbacks<List<MovieReview>> loaderListenerReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        movie = intent.getParcelableExtra(MovieTMDb.PARCELABLE_KEY);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        rvTrailers = findViewById(R.id.rv_trailer_list);
        rvTrailers.setVisibility(View.GONE);
        progressTrailers = findViewById(R.id.loading_indicator_trailer);
        progressTrailers.setVisibility(View.VISIBLE);
        tvNoTrailers = findViewById(R.id.tv_no_trailers);
        tvNoTrailers.setVisibility(View.GONE);

        rvReviews = findViewById(R.id.rv_reviews);
        rvReviews.setVisibility(View.GONE);
        progressReviews = findViewById(R.id.loading_indicator_reviews);
        progressReviews.setVisibility(View.VISIBLE);
        tvNoReviews = findViewById(R.id.tv_no_reviews);
        tvNoReviews.setVisibility(View.GONE);

        initializeLoaders();
        getSupportLoaderManager().restartLoader(LOADER_ID_MOVIE_DETAILS, null, loaderListenerMovieDetails);
        getSupportLoaderManager().restartLoader(LOADER_ID_FAVORITE_STATUS, null, loaderListenerFavoriteStatus);

        // TODO: wait until extra data loaded, show loading indicator
//        if (movie != null) {
//            Language language = Helpers.loadLanguagePreference(this);
//            URL urlMovieDetails = UrlHelpers.getUrlMovieDetails(movie.getId(), language);
//            String jsonStr = NetworkUtils.downloadJson(urlMovieDetails);
//            JsonUtils.parseAdditionalMovieInfo(movie, jsonStr);



        if (movie == null) {
            View movieDetailsLayout = findViewById(R.id.detail_activity_movie_contents_layout);
            movieDetailsLayout.setVisibility(View.INVISIBLE);
            TextView tvNoConnection = findViewById(R.id.tv_no_connection);
            tvNoConnection.setVisibility(View.VISIBLE);
        }


        trailerAdapter = new TrailerAdapter(this);
        rvTrailers.setAdapter(trailerAdapter);
//        LinearLayoutManager layoutManagerTrailers = new LinearLayoutManager(this);
        int gridCols = getResources().getInteger(R.integer.rv_column_count);
        GridLayoutManager layoutManagerTrailers = new GridLayoutManager(this, gridCols);
        rvTrailers.setLayoutManager(layoutManagerTrailers);
        getSupportLoaderManager().restartLoader(LOADER_ID_TRAILERS, null, loaderListenerTrailers);

        reviewAdapter = new ReviewAdapter();
        rvReviews.setAdapter(reviewAdapter);
        rvReviews.setHasFixedSize(true);
        LinearLayoutManager layoutManagerReviews = new LinearLayoutManager(this);
        rvReviews.setLayoutManager(layoutManagerReviews);
        getSupportLoaderManager().restartLoader(LOADER_ID_REVIEWS, null, loaderListenerReviews);

    }

    private void populateUi(final MovieTMDb movie) {
        ivBackdrop = findViewById(R.id.iv_movie_details_backdrop);
        String backdropUrl = movie.getBackdropPath(TMDbImageSize.w500);
        Glide.with(this).load(backdropUrl).into(ivBackdrop);

        ivPoster = findViewById(R.id.iv_movie_detail_poster);
        String posterUrl = movie.getPosterPath(TMDbImageSize.w342);
        Glide.with(this).load(posterUrl).into(ivPoster);

        tvTitle = findViewById(R.id.tv_movie_details_title);
        String title = movie.getTitle();
        tvTitle.setText(title);

        // only show original title if it differs
        tvTitleOriginal = findViewById(R.id.tv_movie_details_title_original);
        String titleOriginal = movie.getOriginalTitle();
        if (TextUtils.isEmpty(titleOriginal) || title.toLowerCase().equals(titleOriginal.toLowerCase())) {
            tvTitleOriginal.setVisibility(View.GONE);
        } else {
            String languageOriginal = movie.getOriginalLanguage();
            tvTitleOriginal.setText(getString(R.string.original_title)
                    + " " + titleOriginal
                    + " (" + languageOriginal + ")");
        }


        tvGenres = findViewById(R.id.tv_movie_details_genres);
        List<String> genreNames = movie.getGenreNames();
        Log.d("MovieDetails", "genreIds = " + genreNames);
        if (genreNames != null && genreNames.size() > 0) {
            Iterator<String> it = genreNames.iterator();
            String genres = "";
            while (it.hasNext()) {
                String genreName = it.next();
//                String genreName = MovieTools.getGenre(genreId);
                genres += "" + genreName;
                if (it.hasNext()) {
                    genres += ", ";
                }
            }
            tvGenres.setText(genres);
        } else {
            tvGenres.setVisibility(View.GONE);
            // if no genres found: adjust titles's bottom margin to be equal to top margin
            // (because margin is otherwise set on genre textView which is now gone)
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvTitle.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.topMargin);
        }


        // format release date in current user's local format
        tvReleaseDate = findViewById(R.id.tv_movie_detail_release_date);
        String releaseDateRawStr = movie.getReleaseDate();
        Log.d(TAG, "will no parse release date raw str = " + releaseDateRawStr);
        Date date = Date.valueOf(releaseDateRawStr);
//        Language language = Helpers.loadLanguagePreference(this);
//        // fallback in case more languages get added to enum
//        Locale locale = new Locale(language.getLanguageCode());
//        Log.d("MovieDetails", "locale: " + locale.getCountry() + ", " + locale.getLanguage());
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String dateStr = dateFormat.format(date);
        tvReleaseDate.setText(dateStr);


        int runtime = movie.getRuntime();
        TextView tvRuntime = findViewById(R.id.tv_movie_runtime);
        tvRuntime.setText(runtime + " Minutes");
        tvRuntime.setVisibility(View.VISIBLE);

        int budget = movie.getBudget();
        if (budget > 0) {
            // else: stays invisible
            TextView tvBudget = findViewById(R.id.tv_movie_budget);
            if (budget >= 1_000_000_000) {
                float billions = budget / 1_000_000_000;
                tvBudget.setText(String.valueOf(billions) + "  Billions");
            }
            else if (budget >= 1_000_000) {
                float millions = budget / 1_000_000;
                tvBudget.setText(String.valueOf(millions) + "  Millions");
            }
            else {
                tvBudget.setText(String.valueOf(budget));
            }
            tvBudget.setVisibility(View.VISIBLE);
        }


        tvVoteAverage = findViewById(R.id.tv_movie_detail_rating);
        float voteAvg = movie.getVoteAverage();
        tvVoteAverage.setText(String.format("%.1f", voteAvg));

        tvVoteCount = findViewById(R.id.tv_movie_detail_vote_count);
        int voteCount = movie.getVoteCount();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        tvVoteCount.setText(numberFormat.format(voteCount) + " votes");

        tvDescription = findViewById(R.id.tv_plot_overview);
        String overview = movie.getOverview();
        Log.d("MovieDetails", "overview: " + overview);
        if (TextUtils.isEmpty(overview)) {
            overview = getString(R.string.description_not_available);
            if (voteAvg >= 8) {
                overview += getString(R.string.funny_rating_awesome);
            } else if (voteAvg <= 5) {
                overview += getString(R.string.funny_rating_not_good);
            }
        }
        tvDescription.setText(overview);

        Log.d("MovieDetails", "starting check for favorite");
//        btnFavoriteToggle = findViewById(R.id.btn_favorite_toggle);
        btnFavoriteToggle = findViewById(R.id.btn_container_add_to_favs);
        btnFavoriteToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteStatus();
            }
        });


//        btnSearchOn = findViewById(R.id.btn_search_movie_title_on);
        btnSearchOn = findViewById(R.id.btn_container_search_on);
        btnSearchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MovieDetailsActivity.this, v);
                // to implement on click event on items of menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Uri uri = null;

                        int itemId = item.getItemId();
                        String urlStr = null;
                        switch (itemId) {
                            case R.id.action_search_imdb:
                                urlStr = "https://www.imdb.com/title/" + movie.getImdbId();
                                uri = Uri.parse(urlStr);
                                break;
                            case R.id.action_search_youtube:
                                urlStr = "https://www.youtube.com/results?search_query=" + movie.getTitle();
                                uri = Uri.parse(urlStr);
                                break;
                            case R.id.action_search_rotton_tomatoes:
                                urlStr = "https://www.rottentomatoes.com/search/?search=" + movie.getTitle();
                                uri = Uri.parse(urlStr);
                                break;
                            case R.id.action_search_amazon:
                                urlStr = "https://www.amazon.de/s/field-keywords=" + movie.getTitle();
                                uri = Uri.parse(urlStr);
                                break;
                            case R.id.action_search_google:
                                urlStr = "https://www.google.de/search?num=20&q=" + movie.getTitle();
                                uri = Uri.parse(urlStr);
                                break;

                            default:
                                Log.d(TAG, "popup menu listener: no valid itemId found");
                        }

                        if (uri != null) {
                            Intent searchIntent = new Intent(Intent.ACTION_VIEW, uri);
                            Log.d(TAG, "starting search: " + uri.toString());
                            startActivity(searchIntent);
                            return true;
                        }
                        Log.d(TAG, "no search, invalid uri (null)");
                        return false;
                    }
                });

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.search_movie_on, popup.getMenu());
                popup.show();
            }
        });
    }

    public void refreshFavoriteButton() {
        ImageView ivHeart = findViewById(R.id.btn_add_to_favs_img);
        if (btnFavoriteToggle==null) {
            btnFavoriteToggleText = findViewById(R.id.btn_add_to_favs_text);
        }

        if (isFavorite) {
            btnFavoriteToggleText.setText(R.string.action_remove_favourite);
            ivHeart.setImageResource(R.drawable.ic_favorite_accent_color_24dp);
            Log.d("MovieDetails", "changed text to 'Remove...'");
        } else {
            btnFavoriteToggleText.setText(getString(R.string.action_add_favorite));
            ivHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            Log.d("MovieDetails", "changed text to 'Add...'");
        }
    }

    private void toggleFavoriteStatus() {

        int movieId = movie.getId();

        if (isFavorite) {
            Uri uri = FavouriteMovieContract.FavMovieEntry.CONTENT_URI
                    .buildUpon().appendPath(String.valueOf(favId)).build();
            Log.d("MovieDetails", "btnFavoriteToggle clicked -> delete from favs");
            int rowsDeleted = getContentResolver().delete(uri, null, null);
            isFavorite = false;
            refreshFavoriteButton();
        } else {
            Log.d("MovieDetails", "btnFavoriteToggle clicked -> inserting into DB");
            ContentValues values = new ContentValues();
            values.put(FavouriteMovieContract.FavMovieEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(FavouriteMovieContract.FavMovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(FavouriteMovieContract.FavMovieEntry.COLUMN_POSTER_PATH,
                    movie.getPosterPath(null));
            values.put(FavouriteMovieContract.FavMovieEntry.COLUMN_BACKDROP_PATH,
                    movie.getBackdropPath(null));

            // only release date needs to be stored manually, timestamp (when favorite was added) is automatic
            values.put(FavouriteMovieContract.FavMovieEntry.COLUMN_DATE_RELEASED,
                    movie.getReleaseDate());

            // TODO: TIMESTAMP DEFAULT CURRENT_TIMESTAMP only stores '2018' in database, find out why
            // manual value necessary
            values.put(FavouriteMovieContract.FavMovieEntry.COLUMN_TIMESTAMP_SAVED,
                    System.currentTimeMillis());

            Uri uri = FavouriteMovieContract.FavMovieEntry.CONTENT_URI;
            Uri uriNewFavEntry = getContentResolver().insert(uri, values);
            if (uriNewFavEntry != null) {
                Toast.makeText(getBaseContext(),
                        "inserted: " + uriNewFavEntry.toString(), Toast.LENGTH_LONG).show();
                isFavorite = true;
                refreshFavoriteButton();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // back button returns to main activity, as defined in manifest
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTrailerListItemClick(int clickedItemIndex) {

        Trailer trailer = trailerAdapter.getTrailer(clickedItemIndex);
        String key = trailer.getKey();
        String youtubeUrlStr = UrlHelpers.getYoutubeTrailerUrlStr(key);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrlStr));
        startActivity(intent);
    }


    // ---------- async loading of: favorite status + trailers + trailer details + reviews ----------

    public void initializeLoaders() {

        // ---------------- movie details ----------------
        loaderListenerMovieDetails = new LoaderManager.LoaderCallbacks<MovieTMDb>() {

            @Override
            public Loader<MovieTMDb> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<MovieTMDb>(MovieDetailsActivity.this) {

                    @Override
                    protected void onStartLoading() {
                        Log.d(TAG, "AsyncTaskLoader movie details: onStartLoading()");
                        forceLoad();
                    }

                    @Nullable
                    @Override
                    public MovieTMDb loadInBackground() {
                        Language language = Helpers.loadLanguagePreference(getApplicationContext());
                        URL urlMovieDetails = UrlHelpers.getUrlMovieDetails(movie.getId(), language);
                        String jsonStr = NetworkUtils.downloadJson(urlMovieDetails);
                        JsonUtils.parseAdditionalMovieInfo(movie, jsonStr);
                        return movie;
                    }

                    @Override
                    public void deliverResult(@Nullable MovieTMDb data) {
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<MovieTMDb> loader, MovieTMDb movie) {
                Log.d(TAG, "movie details: onLoadFinished(): imdbId="+movie.getImdbId());

                populateUi(movie);




            }

            @Override
            public void onLoaderReset(Loader<MovieTMDb> loader) {
                Log.d(TAG, "movie details: onLoaderReset()");
            }
        };

        // -------- FAV STATUS --------
        loaderListenerFavoriteStatus = new LoaderManager.LoaderCallbacks<Boolean>() {
            @Override
            public Loader<Boolean> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Boolean>(MovieDetailsActivity.this) {
                    @Override
                    protected void onStartLoading() {
                        Log.d(TAG, "AsyncTaskLoader fav status: onStartLoading()");
                        forceLoad();
                    }

                    @Nullable
                    @Override
                    public Boolean loadInBackground() {
                        Cursor cursor = MovieDetailsActivity.this.getContentResolver()
                                .query(FavouriteMovieContract.FavMovieEntry.CONTENT_URI,
                                        null,
                                        FavouriteMovieContract.FavMovieEntry.COLUMN_MOVIE_ID + "=" + movie.getId(),
                                        null,
                                        null);
                        // TODO: check after having added favs that this is no longer null
                        if (cursor == null) {
                            return false;
                        }
                        int count = cursor.getCount();
                        if (count<=0) {
                            return false;
                        }

                        // only work with cursor if != null and size>0
//                        int colCount = cursor.getColumnCount();
//                        for (int currentColIndex=0; currentColIndex<colCount; currentColIndex++) {
//                            Log.d(TAG, "cursor: " + cursor.getColumnName(currentColIndex));
//                        }

                        Log.d(TAG, "AsyncTaskLoader fav status: loadInBackground(): found " + count + " favorites with movie_id=" + movie.getId());
                        cursor.moveToPosition(0);
                        MovieDetailsActivity.this.favId = cursor.getInt(
                                cursor.getColumnIndex(FavouriteMovieContract.FavMovieEntry._ID));
                        cursor.close();
                        return (count > 0 ? true : false);
                    }

                    @Override
                    public void deliverResult(Boolean isFavorite) {
                        Log.d(TAG, "AsyncTaskLoader fav status: deliverResult() called: isFavorite? " + isFavorite);
                        super.deliverResult(isFavorite);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Boolean> loader, Boolean isFavorite) {
                Log.d(TAG, "LoaderManager fav status: onLoadFinished() called, refreshing button text");
                MovieDetailsActivity.this.isFavorite = isFavorite;
                refreshFavoriteButton();
            }

            @Override
            public void onLoaderReset(Loader<Boolean> loader) {
                Log.d(TAG, "LoaderManager fav status: onLoaderReset() called");
            }
        };


        // -------- TRAILERS --------
        loaderListenerTrailers =
                new LoaderManager.LoaderCallbacks<List<Trailer>>() {

                    @Override
                    public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {

                        return new AsyncTaskLoader<List<Trailer>>(MovieDetailsActivity.this) {

                            @Override
                            protected void onStartLoading() {
                                Log.d(TAG, "AsyncTaskLoader trailers: onStartLoading()");
                                forceLoad();
                            }

                            @Nullable
                            @Override
                            public List<Trailer> loadInBackground() {
                                Language language = Helpers.loadLanguagePreference(MovieDetailsActivity.this);
                                final URL url = UrlHelpers.getUrlRelatedMovies(movie.getId(), language);
                                Log.d("MovieTools", "related movies URL = " + url.toString());
                                String jsonRelatedMovies = NetworkUtils.downloadJson(url);
                                List<Trailer> trailers = JsonUtils.parseRelatedMovieTrailers(jsonRelatedMovies);
                                // download trailer info from youtube for each trailer
                                for (Trailer trailer : trailers) {
                                    MovieTools.loadYoutubeInfoIntoTrailer(trailer);
//                                    Log.d("MovieTools", "added YouTube info into trailer: " + trailer);
                                }
                                return trailers;
                            }

                            @Override
                            public void deliverResult(List<Trailer> trailers) {
                                Log.d("MainActivity", "AsyncTaskLoader trailers: deliverResult() called");
                                super.deliverResult(trailers);
                            }

                        };
                    }

                    @Override
                    public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailers) {
                        Log.d(TAG, trailers.size() + " trailers loaded");
                        trailerAdapter.swapTrailers(trailers);
                        // trailer loading: loading indicator
                        if (trailers.size() > 0) {
                            progressTrailers.setVisibility(View.GONE);
                            tvNoTrailers.setVisibility(View.GONE);
                            rvTrailers.setVisibility(View.VISIBLE);
                            Log.d(TAG, "trailer recycler visible");
                        }
                        else {
                            progressTrailers.setVisibility(View.GONE);
                            rvTrailers.setVisibility(View.GONE);
                            tvNoTrailers.setVisibility(View.VISIBLE);
                            Log.d(TAG, "trailer placeholder textview visible");
                        }

                    }

                    @Override
                    public void onLoaderReset(Loader<List<Trailer>> loader) {
                        Log.d(TAG, "trailer loader: onLoaderReset() called");
                    }
                };


        // -------- REVIEWS --------
        loaderListenerReviews = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {
            @Override
            public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<List<MovieReview>>(MovieDetailsActivity.this) {

                    @Override
                    protected void onStartLoading() {
                        Log.d(TAG, "AsyncTaskLoader reviews: onStartLoading()");
                        forceLoad();
                    }

                    @Nullable
                    @Override
                    public List<MovieReview> loadInBackground() {
                        Language language = Helpers.loadLanguagePreference(MovieDetailsActivity.this);
                        final URL url = UrlHelpers.getUrlMovieReviews(movie.getId(), language);
                        String jsonReviews = NetworkUtils.downloadJson(url);
                        List<MovieReview> reviews = JsonUtils.parseMovieReviews(jsonReviews);
                        return reviews;
                    }

                    @Override
                    public void deliverResult(List<MovieReview> reviews) {
                        Log.d("MainActivity", "AsyncTaskLoader reviews: deliverResult() called");
                        super.deliverResult(reviews);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> reviews) {
                Log.d(TAG, reviews.size() + " reviews loaded");
                reviewAdapter.swapReviews(reviews);
                if (reviews.size() > 0) {
                    progressReviews.setVisibility(View.GONE);
                    tvNoReviews.setVisibility(View.GONE);
                    rvReviews.setVisibility(View.VISIBLE);
                    Log.d(TAG, "review recycler visible");
                }
                else {
                    progressReviews.setVisibility(View.GONE);
                    rvReviews.setVisibility(View.GONE);
                    tvNoReviews.setVisibility(View.VISIBLE);
                    Log.d(TAG, "review placeholder textview visible");
                }

            }

            @Override
            public void onLoaderReset(Loader<List<MovieReview>> loader) {
                Log.d(TAG, "review loader: onLoaderReset() called");
            }
        };


    }


}
