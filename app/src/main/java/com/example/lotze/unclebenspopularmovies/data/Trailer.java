package com.example.lotze.unclebenspopularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Benjamin Lotze on 14.04.2018.
 */

public class Trailer implements Parcelable {

    public static final String PARCELABLE_KEY = "trailer-parcel-key";

    // data from Tmdb json
    String id;
    String key;
    String name;
    String site;
    int size;


    // data from youtube json
    private String thumbnailUrl;
    private String authorName;
    private String authorUrl;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public String getAuthorUrl() {
        return authorUrl;
    }
    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }



    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }



    protected Trailer(Parcel in) {
        this.id = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = in.readInt();

        this.thumbnailUrl = in.readString();
        this.authorName = in.readString();
        this.authorUrl = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);

        dest.writeString(thumbnailUrl);
        dest.writeString(authorName);
        dest.writeString(authorUrl);
    }


    @Override
    public String toString() {
        return "Trailer{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", size=" + size +
                ", thumb=" + thumbnailUrl +
                ", authorName=" + authorName +
                '}';
    }
}
