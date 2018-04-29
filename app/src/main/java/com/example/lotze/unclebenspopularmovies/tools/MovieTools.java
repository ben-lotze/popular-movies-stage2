package com.example.lotze.unclebenspopularmovies.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.lotze.unclebenspopularmovies.data.Language;
import com.example.lotze.unclebenspopularmovies.data.Trailer;

import java.net.URL;
import java.util.Map;

/**
 * Created by Benjamin Lotze on 19.02.2018.
 */

public class MovieTools {


    private static Language downloadedGenresLanguage;
    private static Map<Integer, String> genresById;

/*
    public static List<Trailer> loadRelatedMovies(int movieId, Language language) {
        final URL url = UrlHelpers.getUrlRelatedMovies(movieId, language);
        Log.d("MovieTools", "related movies URL = " + url.toString());

        AsyncTask<URL, Void, List<Trailer>> task = new AsyncTask<URL, Void, List<Trailer>>() {
            @Override
            protected List<Trailer> doInBackground(URL... urls) {
                String jsonRelatedMovies = NetworkUtils.downloadJson(url);
                List<Trailer> trailers = JsonUtils.parseRelatedMovieTrailers(jsonRelatedMovies);
                return trailers;
            }
        };

        task.execute(new URL[]{url});

        try {
            return task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static void loadYoutubeInfoIntoTrailer(Trailer trailer) {
        String youtubeId = trailer.getKey();
        URL url = NetworkUtils.buildUrl( UrlHelpers.getYoutubeDetailsJsonUrl(youtubeId) );
        String jsonStr = NetworkUtils.downloadJson(url);
        JsonUtils.parseYoutubeTrailerDetailsIntoTrailer(jsonStr, trailer);
    }


//    public static List<MovieReview> loadMovieReviews(int movieId, Language language) {
//        final URL url = UrlHelpers.getUrlMovieReviews(movieId, language);
//
//        AsyncTask<URL, Void, List<MovieReview>> task = new AsyncTask<URL, Void, List<MovieReview>>() {
//            @Override
//            protected List<MovieReview> doInBackground(URL... urls) {
//                String jsonReviews = NetworkUtils.downloadJson(url);
//                List<MovieReview> reviews = JsonUtils.parseMovieReviews(jsonReviews);
//                return reviews;
//            }
//        };
//
//        task.execute(new URL[]{url});
//
//        try {
//            return task.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }




    // map should be saved
    public static void loadGenres(@NonNull Language language, Context context) {
        // check if genres already available in specified language, otherwise download and parse
        if (genresById != null && language.equals(downloadedGenresLanguage)) {
            return;
        }

        downloadedGenresLanguage = language;  // to check if re-download necessary (after change in settings)
        final URL genreUrl = UrlHelpers.getUrlForGenres(language);

        AsyncTask<URL, Void, Map<Integer, String>> task = new AsyncTask<URL, Void, Map<Integer, String>>() {
            @Override
            protected Map<Integer, String> doInBackground(URL... urls) {

                if (urls == null || urls.length == 0) {
                    return null;
                }

                String jsonGenresStr = NetworkUtils.downloadJson(genreUrl);
                genresById = JsonUtils.parseGenres(jsonGenresStr);
                return genresById;
            }
        }.execute(genreUrl);
    }

    public static String getGenre(int id) {
        if (genresById == null) {
            return "";
        }
        return genresById.get(id);
    }


    // check if is favorite (to show correct icon)
    /*
    public static boolean isFavoriteMovie(final Context context, final int movieId) {

        Log.d("MovieTools", "isFavoriteMovie() called");

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Log.d("MovieTools", "isFavoriteMovie() -> doInBackground() called");
                Cursor cursor = context.getContentResolver().query(FavouriteMovieContract.FavMovieEntry.CONTENT_URI,
                        null,
                        FavouriteMovieContract.FavMovieEntry.COLUMN_MOVIE_ID + "=" + movieId, null,
                        null);
                // TODO: check after having added favs that this is no longer null
                if (cursor==null) {
                    return false;
                }
                int count = cursor.getCount();
                Log.d("MovieTools", "found " + count + " favorites with movie_id=" + movieId);
                return (count > 0 ? true : false);
            }
        };
        task.execute();
        try {
            boolean isFav = task.get();
            return isFav;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
    */

}
