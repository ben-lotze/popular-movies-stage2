package com.example.lotze.unclebenspopularmovies.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.lotze.unclebenspopularmovies.data.Language;

import java.net.URL;
import java.util.Map;

/**
 * Created by Benjamin Lotze on 19.02.2018.
 */

public class MovieTools {



    private static Language downloadedGenresLanguage;
    private static Map<Integer, String> genresById;

    // map should be saved
    public static void loadGenres(@NonNull Language language, Context context) {
        // check if genres already available in specified language, otherwise download and parse
        if (genresById!=null && language.equals(downloadedGenresLanguage)) {
            return;
        }

        downloadedGenresLanguage = language;  // to check if re-download necessary (after change in settings)
        final URL genreUrl = NetworkUtils.buildUrlForGenres(language);

        AsyncTask<URL, Void, Map<Integer, String>> task = new AsyncTask<URL, Void, Map<Integer, String>>() {
            @Override
            protected Map<Integer, String> doInBackground(URL... urls) {

                if (urls==null || urls.length==0) {
                    return null;
                }

                String jsonGenresStr = NetworkUtils.downloadJson(genreUrl);
                genresById = JsonUtils.parseGenres(jsonGenresStr);
                return genresById;
            }
        }.execute(genreUrl);
    }

    public static String getGenre(int id) {
        if (genresById==null) {
            return "";
        }
        return genresById.get(id);
    }

}
