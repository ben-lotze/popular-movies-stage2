package com.example.lotze.unclebenspopularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.lotze.unclebenspopularmovies.tools.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Lotze on 17.02.2018.
 */

public class MovieTMDb implements Parcelable {

    public static final String PARCELABLE_KEY = "movie-tmdb-parcel-key";

    private int id;
    private String title;
    private String originalTitle;   // original_title

    private int voteCount;  // vote_count
    private float voteAverage;  // vote_average
    private float popularity;

    private String overview;
    private List<Integer> genreIds; // genre_ids: [28,18,53]

    private String originalLanguage; // original_language
    private String releaseDate; // release_date: "2015-05-27"
    private boolean adult;
    private boolean video;

    private String posterPath;  // poster_path
    private String backdropPath;    // backdrop_path


    public MovieTMDb(int id, String title, String originalTitle, int voteCount, float voteAverage,
                     float popularity, String overview, List<Integer> genreIds, String originalLanguage,
                     String releaseDate, boolean adult, boolean video, String posterPath, String backdropPath) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
        this.overview = overview;
        this.genreIds = genreIds;
        this.originalLanguage = originalLanguage;
        this.releaseDate = releaseDate;
        this.adult = adult;
        this.video = video;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
    }

    /**
     * constructor for interface Parcelable
     * @param in
     */
    protected MovieTMDb(Parcel in) {
        id = in.readInt();
        title = in.readString();
        originalTitle = in.readString();
        voteCount = in.readInt();
        voteAverage = in.readFloat();
        popularity = in.readFloat();
        overview = in.readString();
        originalLanguage = in.readString();
        releaseDate = in.readString();
        adult = in.readByte() != 0;
        video = in.readByte() != 0;
        posterPath = in.readString();
        backdropPath = in.readString();

        genreIds = new ArrayList<>();
        in.readList(genreIds, null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeInt(voteCount);
        dest.writeFloat(voteAverage);
        dest.writeFloat(popularity);
        dest.writeString(overview);
        dest.writeString(originalLanguage);
        dest.writeString(releaseDate);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeString(posterPath);
        dest.writeString(backdropPath);

        dest.writeList(genreIds);
    }

    public static final Creator<MovieTMDb> CREATOR = new Creator<MovieTMDb>() {
        @Override
        public MovieTMDb createFromParcel(Parcel in) {
            return new MovieTMDb(in);
        }

        @Override
        public MovieTMDb[] newArray(int size) {
            return new MovieTMDb[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getOverview() {
        return overview;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public boolean isAdult() {
        return adult;
    }

    public boolean hasVideo() {
        return video;
    }

    public String getPosterPath(TMDbImageSize size) {
        return NetworkUtils.buildImagePath(posterPath, size);
    }

    public String getBackdropPath(TMDbImageSize size) {
        return NetworkUtils.buildImagePath(backdropPath, size);
    }

    @Override
    public String toString() {
        return "MovieTMDb{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", voteAverage=" + voteAverage +
                ", genreIds=" + genreIds +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }




}
