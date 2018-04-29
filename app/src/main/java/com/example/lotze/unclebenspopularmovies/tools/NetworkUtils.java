package com.example.lotze.unclebenspopularmovies.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
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


    public static URL buildUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // in case of errors
        return null;
    }
}
