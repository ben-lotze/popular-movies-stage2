package com.example.lotze.unclebenspopularmovies;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lotze.unclebenspopularmovies.data.Language;
import com.example.lotze.unclebenspopularmovies.data.MovieTMDb;
import com.example.lotze.unclebenspopularmovies.data.TMDbImageSize;
import com.example.lotze.unclebenspopularmovies.tools.Helpers;
import com.example.lotze.unclebenspopularmovies.tools.MovieTools;

import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView ivBackdrop;
    private ImageView ivPoster;
    private TextView tvTitle;
    private TextView tvTitleOriginal;
    private TextView tvReleaseDate;
    private TextView tvGenres;
    private TextView tvVoteAverage;
    private TextView tvVoteCount;

    private TextView tvDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        MovieTMDb movie = intent.getParcelableExtra(MovieTMDb.PARCELABLE_KEY);

        if (movie!=null) {
            populateUi(movie);
        }
        else {
            View movieDtailsLayout = findViewById(R.id.detail_activity_movie_contents_layout);
            movieDtailsLayout.setVisibility(View.INVISIBLE);
            TextView tvNoConnection = findViewById(R.id.tv_no_connection);
            tvNoConnection.setVisibility(View.VISIBLE);
        }
    }

    private void populateUi(MovieTMDb movie) {
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
        List<Integer> genreIds = movie.getGenreIds();
        Log.d("MovieDetails", "genreIds = " + genreIds);
        if (genreIds != null && genreIds.size() > 0) {
            Iterator<Integer> it = genreIds.iterator();
            String genres = "";
            while (it.hasNext()) {
                int genreId = it.next();
                String genreName = MovieTools.getGenre(genreId);
                genres += "" + genreName;
                if (it.hasNext()) {
                    genres += ", ";
                }
            }
            tvGenres.setText(genres);
        } else {
            tvGenres.setVisibility(View.GONE);
        }


        // format release date in current user's local format
        tvReleaseDate = findViewById(R.id.tv_movie_detail_release_date);
        String releaseDateRawStr = movie.getReleaseDate();
        Date date = Date.valueOf(releaseDateRawStr);
//        Language language = Helpers.loadLanguagePreference(this);
//        // fallback in case more languages get added to enum
//        Locale locale = new Locale(language.getLanguageCode());
//        Log.d("MovieDetails", "locale: " + locale.getCountry() + ", " + locale.getLanguage());
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String dateStr = dateFormat.format(date);
        tvReleaseDate.setText(dateStr);

        tvVoteAverage = findViewById(R.id.tv_movie_detail_rating);
        float voteAvg = movie.getVoteAverage();
        tvVoteAverage.setText(String.format("%.1f", voteAvg));

        tvVoteCount = findViewById(R.id.tv_movie_detail_vote_count);
        int voteCount = movie.getVoteCount();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        tvVoteCount.setText(numberFormat.format(voteCount) + " votes");

        tvDescription = findViewById(R.id.tv_overview);
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

}
