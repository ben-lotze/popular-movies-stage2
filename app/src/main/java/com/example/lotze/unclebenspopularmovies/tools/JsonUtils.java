package com.example.lotze.unclebenspopularmovies.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.lotze.unclebenspopularmovies.R;
import com.example.lotze.unclebenspopularmovies.data.MovieReview;
import com.example.lotze.unclebenspopularmovies.data.MovieTMDb;
import com.example.lotze.unclebenspopularmovies.data.TMDbContract;
import com.example.lotze.unclebenspopularmovies.data.Trailer;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Lotze on 17.02.2018.
 */

public class JsonUtils {


    private static final String TAG = JsonUtils.class.getSimpleName();
    /**
     * to comply with Udacity rules
     *
     * @param jsonStr string to parse
     * @param context to determine current selection for json parser engine from preferences
     * @return list of movies
     */
    public static List<MovieTMDb> parseJsonMoviesResult(String jsonStr, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonEngineByHandDefault = context.getString(R.string.pref_json_parser_by_hand_key);
        String jsonEngineGson = context.getString(R.string.pref_json_parser_gson_key);
        String jsonEngineSetting = prefs.getString(context.getString(R.string.pref_json_parser_key),
                jsonEngineByHandDefault);

        // optional: parsing with gson
        if (jsonEngineGson.equals(jsonEngineSetting)) {
            return parseMoviesWithGoogleGson(jsonStr);
        }
        // default
        return parseMoviesWithBareHands(jsonStr);
    }


    public static List<MovieTMDb> parseMoviesWithBareHands(String jsonStr) {
        Log.d("JsonUtils", "parsing movies with JSONObjects by hand");
        if (TextUtils.isEmpty(jsonStr)) {
            Log.d("JsonUtils", "json string is empty, returning empty list of movies");
            return new ArrayList<>();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj == null) {
                return new ArrayList<>();
            }

            JSONArray jsonMoviesArray = jsonObj.getJSONArray("results");
            // return empty list if no data available
            if (jsonMoviesArray == null) {
                return new ArrayList<>();
            }

            List<MovieTMDb> movies = new ArrayList<>();

            int numberOfMovies = jsonMoviesArray.length();
            for (int index = 0; index < numberOfMovies; index++) {
                JSONObject jsonCurrentMovie = jsonMoviesArray.getJSONObject(index);


                int id = jsonCurrentMovie.getInt(TMDbContract.FIELD_ID);
                String title = jsonCurrentMovie.getString(TMDbContract.FIELD_TITLE);
                MovieTMDb movie = new MovieTMDb(id, title);

                String releaseDate = jsonCurrentMovie.getString(TMDbContract.FIELD_RELEASE_DATE);
                movie.setReleaseDate(releaseDate);
                String posterPath = jsonCurrentMovie.getString(TMDbContract.FIELD_IMAGE_POSTER_PATH);
                movie.setPosterPath(posterPath);
                String backdropPath = jsonCurrentMovie.getString(TMDbContract.FIELD_IMAGE_BACKDROP_PATH);
                movie.setBackdropPath(backdropPath);
                float voteAvg = jsonCurrentMovie.getInt(TMDbContract.FIELD_VOTE_AVERAGE);
                movie.setVoteAverage(voteAvg);

                movies.add(movie);
            }

            // if no exceptions: return all movies
            return movies;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // in case of error
        return null;
    }


    public static void parseAdditionalMovieInfo(MovieTMDb movie, String jsonStr) {

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            JSONArray jsonGenreArray = jsonObj.getJSONArray("genres");
            if (jsonGenreArray != null) {
                List<String> genres = new ArrayList<>();
                int numberOfGenres = jsonGenreArray.length();
                for (int i = 0; i < numberOfGenres; i++) {
                    JSONObject jsonCurrentGenre = jsonGenreArray.getJSONObject(i);
                    String genreName = jsonCurrentGenre.getString("name");
                    genres.add(genreName);
                }
                movie.setGenreNames(genres);
                Log.d(TAG,"parsed genre names: " + genres);
            }


            String originalTitle = jsonObj.getString(TMDbContract.FIELD_ORIGINAL_TITLE);
            movie.setOriginalTitle(originalTitle);
            String overview = jsonObj.getString(TMDbContract.FIELD_OVERVIEW);
            movie.setOverview(overview);
            String originalLanguage = jsonObj.getString(TMDbContract.FIELD_ORIGINAL_LANGUAGE);
            movie.setOriginalLanguage(originalLanguage);

            boolean isAdult = jsonObj.getBoolean(TMDbContract.FIELD_ADULT);
            movie.setAdult(isAdult);
            boolean hasVideo = jsonObj.getBoolean(TMDbContract.FIELD_VIDEO);
            movie.setVideo(hasVideo);

            int voteCount = jsonObj.getInt(TMDbContract.FIELD_VOTE_COUNT);
            movie.setVoteCount(voteCount);
            float popularity = jsonObj.getInt(TMDbContract.FIELD_POPULARITY);
            movie.setPopularity(popularity);

            String imdbId = jsonObj.getString("imdb_id");
            movie.setImdbId(imdbId);
            int runtime = jsonObj.getInt("runtime");
            movie.setRuntime(runtime);
            int budget= jsonObj.getInt("budget");
            movie.setBudget(budget);

            // again to be sure
            float voteAvg = jsonObj.getInt(TMDbContract.FIELD_VOTE_AVERAGE);
            movie.setVoteAverage(voteAvg);
            String releaseDate = jsonObj.getString(TMDbContract.FIELD_RELEASE_DATE);
            Log.d(TAG, "json release date = " + releaseDate);
            movie.setReleaseDate(releaseDate);

            // TODO: why needs this to be loaded again? should come from DB when in favorites
            // idea: db saves complete path?
//            String posterPath = jsonObj.getString(TMDbContract.FIELD_IMAGE_POSTER_PATH);
//            movie.setPosterPath(posterPath);
//            String backdropPath = jsonObj.getString(TMDbContract.FIELD_IMAGE_BACKDROP_PATH);
//            movie.setBackdropPath(backdropPath);

//            Log.d(TAG, "added information: imdbId="+imdbId + ", runtime="+runtime
//                + ", genres: "+movie.getGenreNames());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static List<MovieTMDb> parseMoviesWithGoogleGson(String jsonStr) {
//        Log.d("JsonUtils", "parsing movies with Gson");
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            // objects are in Json-array (json stuff before array not interesting)
            JSONArray jsonMoviesArray = jsonObj.getJSONArray("results");
            // Gson can not parse the array directly -> make string from array
            String jsonMoviesStr = jsonMoviesArray.toString();

            Gson gson = new GsonBuilder()
                    // parses underscore names into camelCase Java variables
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            List<MovieTMDb> movies = gson.fromJson(jsonMoviesStr,
                    new TypeToken<List<MovieTMDb>>() {
                    }.getType());
            return movies;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // in case of error
        return null;
    }


    public static List<MovieReview> parseMovieReviews(String jsonStr) {

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray jsonTrailerArray = jsonObj.getJSONArray("results");
            if (jsonTrailerArray == null) {
                return new ArrayList<>();
            }

            // Gson can not parse the array directly -> make string from array
            String jsonReviewsStr = jsonTrailerArray.toString();

            Gson gson = new GsonBuilder()
                    // parses underscore names into camelCase Java variables
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            List<MovieReview> reviews = gson.fromJson(jsonReviewsStr,
                    new TypeToken<List<MovieReview>>() {
                    }.getType());
            return reviews;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // in case of errors
        return null;
    }

    public static List<Trailer> parseRelatedMovieTrailers(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray jsonTrailerArray = jsonObj.getJSONArray("results");
            if (jsonTrailerArray == null) {
                return new ArrayList<>();
            }
            // Gson can not parse the array directly -> make string from array
            String jsonMoviesStr = jsonTrailerArray.toString();

            Gson gson = new GsonBuilder()
                    // parses underscore names into camelCase Java variables
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            List<Trailer> trailers = gson.fromJson(jsonMoviesStr,
                    new TypeToken<List<Trailer>>() {
                    }.getType());
//            for (Trailer trailer : trailers) {
//                Log.d("JsonUtils", "---> trailer:" + trailer.toString());
//            }
            return trailers;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        // in case of errors
        return null;
    }


    public static void parseYoutubeTrailerDetailsIntoTrailer(String jsonStr, Trailer trailer) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String authorName = jsonObj.getString("author_name");
            trailer.setAuthorName(authorName);
            String authorUrl = jsonObj.getString("author_url");
            trailer.setAuthorUrl(authorUrl);
            String thumbnailUrl = jsonObj.getString("thumbnail_url");
            trailer.setThumbnailUrl(thumbnailUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static Map<Integer, String> parseGenres(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray jsonGenresArray = jsonObj.getJSONArray("genres");

            // return empty map if no data available
            if (jsonGenresArray == null) {
                return new HashMap<>();
            }

            Map<Integer, String> genresById = new HashMap<>();
            int numberOfGenres = jsonGenresArray.length();
            for (int i = 0; i < numberOfGenres; i++) {
                JSONObject genreObj = jsonGenresArray.getJSONObject(i);
                int id = genreObj.getInt("id");
                String name = genreObj.getString("name");
                genresById.put(id, name);
            }

            return genresById;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // in cas eof errors
        return null;
    }


}
