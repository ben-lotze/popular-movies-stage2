package com.example.lotze.unclebenspopularmovies.tools;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.lotze.unclebenspopularmovies.BuildConfig;
import com.example.lotze.unclebenspopularmovies.R;
import com.example.lotze.unclebenspopularmovies.data.Language;
import com.example.lotze.unclebenspopularmovies.data.TMDbImageSize;
import com.example.lotze.unclebenspopularmovies.tools.NetworkUtils;

import java.net.URL;

/**
 * Created by Benjamin Lotze on 13.04.2018.
 */

public class UrlHelpers {

    // TODO use strings.xml or final strings for url keys like api key, page, ...
    // TODO: since data in selected language is not always available, offer spinner for language
    // (selecting spinner reloads specified data)


    private static final String API_KEY = BuildConfig.TMDB_API_KEY;


    private static final String BASE_URL_PICTURES = "https://image.tmdb.org/t/p/";

    private static final String URL_BASE = "https://api.tmdb.org";
    // fixed urls

    private static final String BASE_URL_GENRES = "/3/genre/movie/list?";
    // urls for additional movie data
    private static final String BASE_URL_MOVIE_DETAILS = "/3/movie/{id}?";
    private static final String BASE_URL_RELATED_VIDEOS = "/3/movie/{id}/videos?"; // language works, but not always available
    private static final String BASE_URL_RATINGS = "/3/movie/{id}/reviews?"; // language works
    // paginated urls
    private static final String BASE_URL_MOVIES_BEST_RATED = "/3/movie/top_rated?";
    private static final String BASE_URL_MOVIES_MOST_POPULAR = "/3/movie/popular?";




    public static boolean apiKeyAvailable() {
        if (TextUtils.isEmpty(API_KEY)) {
            return false;
        }
        return true;
    }


    public static String getYoutubeTrailerUrlStr(String youtubeId) {
        return "https://www.youtube.com/watch?v=" + youtubeId;
    }
    public static String getYoutubeDetailsJsonUrl(String youtubeId) {
        return "https://www.youtube.com/oembed?url=" + getYoutubeTrailerUrlStr(youtubeId) + "&format=json";
    }


    public static URL getUrlRelatedMovies(int movieId, Language language) {
        String urlStr = URL_BASE + BASE_URL_RELATED_VIDEOS;
        urlStr = addApiKeyToUrl(urlStr);
        if (language != null) {
            urlStr = addLanguageToUrl(urlStr, language);
        }
//        urlStr = String.format(urlStr, movieId);
        Log.d("UrlHelpers", "related movies URL = " + urlStr);
        urlStr = urlStr.replace("{id}", String.valueOf(movieId));
        return NetworkUtils.buildUrl(urlStr);
    }

    public static URL getUrlMovieDetails(int movieId, Language language) {
        String urlStr = URL_BASE + BASE_URL_MOVIE_DETAILS;
        urlStr = addApiKeyToUrl(urlStr);
        if (language != null) {
            urlStr = addLanguageToUrl(urlStr, language);
        }
        urlStr = urlStr.replace("{id}", String.valueOf(movieId));
        return NetworkUtils.buildUrl(urlStr);
    }

    public static URL getUrlMovieReviews(int movieId, Language language) {
        String urlStr = URL_BASE + BASE_URL_RATINGS;
        urlStr = addApiKeyToUrl(urlStr);
        if (language != null) {
            urlStr = addLanguageToUrl(urlStr, language);
        }
        Log.d("UrlHelpers", "reviews URL = " + urlStr);
        urlStr = urlStr.replace("{id}", String.valueOf(movieId));
        return NetworkUtils.buildUrl(urlStr);
    }

    public static String buildImagePath(@NonNull String imageName, TMDbImageSize imageSize) {
        if (imageName.startsWith("http")) {
            return imageName;
        }
        // fallback to a medium size if no size specified
        if (imageSize==null) {
            imageSize = TMDbImageSize.w342;
        }
        String url = BASE_URL_PICTURES + imageSize + "/" + imageName;
        return url;
    }

    public static URL getUrlForGenres(Language language) {
        String urlStr = URL_BASE + "/3/genre/movie/list?"
                + "api_key=" + API_KEY
                + "&language=" + language.getLanguageCode();

//        String urlStr = UrlHelpers.Builder()
//                .setUrl(BASE_URL_GENRES)
//                .setLanguage(language)
//                .buildUrlString()
//                ;

       return NetworkUtils.buildUrl(urlStr);
    }

    public static URL getUrlForMenuItemSelected(int itemId, int page, Language language) {
        if (itemId == R.id.nav_best_rated_movies) {
            return getUrlBestRatedMovies(page, language);
        } else if (itemId == R.id.nav_most_popular_movies) {
            return getUrlMostPopularMovies(page, language);
        }
        // undefined cases
        return null;
    }

    public static URL getUrlBestRatedMovies(int page, Language language) {
        String urlStr = URL_BASE + "/3/movie/top_rated?"
                + "api_key="  + API_KEY
                + "&language=" + language.getLanguageCode()
                + "&page=" + page
                ;

//        String urlStr = UrlHelpers.Builder()
//                .setUrl(BASE_URL_MOVIES_BEST_RATED)
//                .setPage(page)
//                .setLanguage(language)
//                .buildUrlString()
//                ;

        return NetworkUtils.buildUrl(urlStr);
    }

    public static URL getUrlMostPopularMovies(int page, Language language) {

        String urlStr = URL_BASE + "/3/movie/popular?"
                + "api_key=" + API_KEY
                + "&language=" + language.getLanguageCode()
                + "&page=" + page
                ;

//        String urlStr = UrlHelpers.Builder()
//                .setUrl(BASE_URL_MOVIES_MOST_POPULAR)
//                .setPage(page)
//                .setLanguage(language)
//                .buildUrlString()
//                ;

        return NetworkUtils.buildUrl(urlStr);
    }

    private static String addApiKeyToUrl(String urlStr) {
        return urlStr += "api_key=" + API_KEY;
    }
    private static String addLanguageToUrl(String urlStr, Language language) {
        return urlStr += "&language=" + language.getLanguageCode();
    }
    private static String addPageToUrl(String urlStr, int page) {
        return urlStr += "&page=" + page;
    }


//    private static String urlBuilderTest() {
//
//        Builder().setUrl(BASE_URL_MOVIES_MOST_POPULAR)
//                .setLanguage(Language.de_DE)
//                .setPage(2)
//
//    }



    static class Builder {

        private static final String URL_BASE = "http://image.tmdb.org";
        private static final String API_KEY2 = BuildConfig.TMDB_API_KEY;

        private String url;
        private int page;
        private Language language;


        // TODO: throw Exceptions when using incompatible operations with wrong base urls

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setPage(int page) {
            this.page = page;
            return this;
        }

        public Builder setLanguage(Language language) {
            this.language = language;
            return this;
        }

        public String buildUrlString() {
            String urlStr = URL_BASE + url
                    + "api_key=" + API_KEY2
                    + (page != 0 ? "&page=" + String.valueOf(page) : "")
                    + (language != null ? "&language=" + language.getLanguageCode() : "")
                    ;
            Log.d("UrlHelpers", "Builder.buildUrlString() -> " + urlStr);
            return urlStr;
        }
        public URL buildUrl() {
            return NetworkUtils.buildUrl(buildUrlString());
        }


        public static boolean apiKeyAvailable() {
            if (TextUtils.isEmpty(API_KEY2)) {
                return false;
            }
            return true;
        }

    }

    public static Builder Builder() {
        return new Builder();
    }



}
