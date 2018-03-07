package com.example.lotze.unclebenspopularmovies.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.lotze.unclebenspopularmovies.R;
import com.example.lotze.unclebenspopularmovies.data.MovieTMDb;
import com.example.lotze.unclebenspopularmovies.data.TMDbContract;
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
            if (jsonObj==null) {
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
                String originalTitle = jsonCurrentMovie.getString(TMDbContract.FIELD_ORIGINAL_TITLE);
                String overview = jsonCurrentMovie.getString(TMDbContract.FIELD_OVERVIEW);
                String originalLanguage = jsonCurrentMovie.getString(TMDbContract.FIELD_ORIGINAL_LANGUAGE);
                String releaseDate = jsonCurrentMovie.getString(TMDbContract.FIELD_RELEASE_DATE);
                String posterPath = jsonCurrentMovie.getString(TMDbContract.FIELD_IMAGE_POSTER_PATH);
                String backdropPath = jsonCurrentMovie.getString(TMDbContract.FIELD_IMAGE_BACKDROP_PATH);

                // genre ids, default to empty list
                List<Integer> genreIds = new ArrayList<>();
                JSONArray jsonGenreIdsArray = jsonCurrentMovie.getJSONArray("genre_ids");
                if (jsonGenreIdsArray != null) {
                    int numberOfGenres = jsonGenreIdsArray.length();
                    for (int i = 0; i < numberOfGenres; i++) {
                        int genreId = (int) jsonGenreIdsArray.get(i);
                        genreIds.add(genreId);
                    }
                }

                int voteCount = jsonCurrentMovie.getInt(TMDbContract.FIELD_VOTE_COUNT);
                float voteAvg = jsonCurrentMovie.getInt(TMDbContract.FIELD_VOTE_AVERAGE);
                float popularity = jsonCurrentMovie.getInt(TMDbContract.FIELD_POPULARITY);

                boolean isAdult = jsonCurrentMovie.getBoolean(TMDbContract.FIELD_ADULT);
                boolean hasVideo = jsonCurrentMovie.getBoolean(TMDbContract.FIELD_ADULT);

                MovieTMDb movie = new MovieTMDb(id, title, originalTitle,
                        voteCount, voteAvg, popularity,
                        overview, genreIds, originalLanguage, releaseDate, isAdult, hasVideo,
                        posterPath, backdropPath
                );
                movies.add(movie);
            }

            // no errors: return all movies
            return movies;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // in case of error
        return null;
    }

    public static List<MovieTMDb> parseMoviesWithGoogleGson(String jsonStr) {
        Log.d("JsonUtils", "parsing movies with Gson");
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
            List<MovieTMDb> movies = gson.fromJson(jsonMoviesStr, new TypeToken<List<MovieTMDb>>() {
            }.getType());
            Log.d("JsonUtils", "parsed movies:");
            for (MovieTMDb movie : movies) {
                Log.d("JsonUtils", "---> movie:" + movie.toString());
            }
            return movies;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // in case of error
        return null;
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
