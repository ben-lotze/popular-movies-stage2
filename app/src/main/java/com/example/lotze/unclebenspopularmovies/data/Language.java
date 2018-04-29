package com.example.lotze.unclebenspopularmovies.data;

/**
 * Created by Benjamin Lotze on 19.02.2018.
 */

public enum Language {
    de_DE ("de-DE", "Deutsch"),
    en_US ("en-US", "English");

    private String languageCode;
    private String languageUiName;

    Language(String languageCode, String languageUiName) {
        this.languageCode = languageCode;
        this.languageUiName = languageUiName;
    }

    public String getLanguageCode() {
        return languageCode;
    }
    public String getLanguageUiName() {
        return languageUiName;
    }


}
