package com.example.lotze.unclebenspopularmovies.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.lotze.unclebenspopularmovies.R;
import com.example.lotze.unclebenspopularmovies.data.Language;

/**
 * Created by Benjamin Lotze on 24.02.2018.
 */

public class Helpers {

    public static Language loadLanguagePreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultLanguageStr = prefs.getString(context.getString(R.string.pref_languages_key),
                context.getString(R.string.pref_language_default));
        return Language.valueOf(defaultLanguageStr);
    }
}
