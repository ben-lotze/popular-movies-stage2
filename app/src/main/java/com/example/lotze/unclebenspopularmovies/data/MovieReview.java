package com.example.lotze.unclebenspopularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Benjamin Lotze on 14.04.2018.
 */

public class MovieReview implements Parcelable {

    public static final String PARCELABLE_KEY = "movie-review-parcel-key";

    private String id;
    private String author;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    private String content;

    protected MovieReview(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}
