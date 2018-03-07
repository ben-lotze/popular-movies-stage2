package com.example.lotze.unclebenspopularmovies.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.lotze.unclebenspopularmovies.BuildConfig;
import com.example.lotze.unclebenspopularmovies.R;
import com.example.lotze.unclebenspopularmovies.data.Language;
import com.example.lotze.unclebenspopularmovies.data.TMDbImageSize;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Benjamin Lotze on 17.02.2018.
 */

public class NetworkUtils {

    private static String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String BASE_URL_PICTURES = "http://image.tmdb.org/t/p/";


    public static boolean apiKeyAvailable() {
        if (TextUtils.isEmpty(API_KEY)) {
            return false;
        }
        return true;
    }



    /**
     * to check if network connection is available
     *
     * @param context
     * @return
     */
    public static boolean internetConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            Log.d("NetworkUtils", "internetConnectionAvailableViaNetworkInfo() -> true");
            return true;
        }
        Log.d("NetworkUtils", "internetConnectionAvailableViaNetworkInfo() -> false");
        return false;
    }




    public static String buildImagePath(@NonNull String imageName, TMDbImageSize imageSize) {
        // fallback to a medium size if no size specified
        if (imageSize==null) {
            imageSize = TMDbImageSize.w342;
        }
        String url = BASE_URL_PICTURES + imageSize + "/" + imageName;
        return url;
    }


    public static URL buildUrlForGenres(Language language) {
        String urlStr = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + API_KEY
                + "&language=" + language.getLanguageCode();
        try {
            URL url = new URL(urlStr);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // in case of errors
        return null;
    }

    public static URL buildUrlForMenuItemSelected(int itemId, int page, Language language) {
        if (itemId == R.id.nav_best_rated_movies) {
            return buildUrlBestRatedMovies(page, language);
        } else if (itemId == R.id.nav_most_popular_movies) {
            return buildUrlMostPopularMovies(page, language);
        }

        // other cases
        return null;
    }

    public static URL buildUrlBestRatedMovies(int page, Language language) {
        String urlStr = "https://api.themoviedb.org/3/movie/top_rated?"
                + "api_key="  + API_KEY
                + "&language=" + language.getLanguageCode()
                + "&page=" + page
                ;
        try {
            URL url = new URL(urlStr);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // in case of error
        return null;
    }

    public static URL buildUrlMostPopularMovies(int page, Language language) {

        String urlStr = "https://api.themoviedb.org/3/movie/popular?"
                + "api_key=" + API_KEY
                + "&language=" + language.getLanguageCode()
                + "&page=" + page
                ;
        try {
            URL url = new URL(urlStr);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // in case of error
        return null;
    }


    public static String downloadJson(URL url) {

        OkHttpClient client = new OkHttpClient();
        try {
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, "{}");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            String responseStr = response.body().string();
//            Log.d("NetworkUtils", "response: " + responseStr);
            return responseStr;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // in case of errors
        return null;
    }



}
