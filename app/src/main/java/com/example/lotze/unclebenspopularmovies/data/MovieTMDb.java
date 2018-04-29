package com.example.lotze.unclebenspopularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.lotze.unclebenspopularmovies.tools.UrlHelpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<String> genreNames; // genre_ids: [28,18,53]



    private String originalLanguage; // original_language
    private String releaseDate; // release_date: "2015-05-27"
    private boolean adult;
    private boolean video;

    private String posterPath;  // poster_path
    private String backdropPath;    // backdrop_path



    // --- additional information: loaded on demand when creating MovieDetailsActivity ---
    private String imdbId;
    private int runtime;
    private int budget;



    private long timestampAddedAsFav;




    //    public MovieTMDb(int id, String title, String originalTitle, int voteCount, float voteAverage,
//                     float popularity, String overview, List<String> genreNames, String originalLanguage,
//                     String releaseDate, boolean adult, boolean video, String posterPath, String backdropPath) {
//        this.id = id;
//        this.title = title;
//        this.originalTitle = originalTitle;
//        this.voteCount = voteCount;
//        this.voteAverage = voteAverage;
//        this.popularity = popularity;
//        this.overview = overview;
//        this.genreNames = genreNames;
//        this.originalLanguage = originalLanguage;
//        this.releaseDate = releaseDate;
//        this.adult = adult;
//        this.video = video;
//        this.posterPath = posterPath;
//        this.backdropPath = backdropPath;
//    }

    public MovieTMDb(int id, String title) {
        this.id = id;
        this.title = title;
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

        imdbId = in.readString();
        runtime = in.readInt();

        genreNames = new ArrayList<>();
        in.readList(genreNames, null);

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

        dest.writeString(imdbId);
        dest.writeInt(runtime);

        dest.writeList(genreNames);
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

    public List<String> getGenreNames() {
        return genreNames;
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
        return UrlHelpers.buildImagePath(posterPath, size);
    }

    public String getBackdropPath(TMDbImageSize size) {
        return UrlHelpers.buildImagePath(backdropPath, size);
    }

    @Override
    public String toString() {
        return "MovieTMDb{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", voteAverage=" + voteAverage +
                ", genreNames=" + genreNames +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }



//    public void addGenre(int genreId, String genreName) {
//        if (genresById == null) {
//            genresById = new HashMap<>();
//        }
//        genresById.put(genreId, genreName);
//    }
//    public Collection<String> getGenreNames() {
//        return genresById.values();
//    }


    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setGenreNames(List<String> genreNames) {
        this.genreNames = genreNames;
    }


    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

//    public String getPosterPath() {
//        return posterPath;
//    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

//    public String getBackdropPath() {
//        return backdropPath;
//    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }


    public int getBudget() {
        return budget;
    }
    public void setBudget(int budget) {
        this.budget = budget;
    }
    public long getTimestampAddedAsFav() {
        return timestampAddedAsFav;
    }
    public void setTimestampAddedAsFav(long timestampAddedAsFav) {
        this.timestampAddedAsFav = timestampAddedAsFav;
    }
}
