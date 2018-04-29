package com.example.lotze.unclebenspopularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.lotze.unclebenspopularmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Lotze on 19.04.2018.
 */

public class MovieListModel extends ViewModel {

    private int page;
    private List<MovieTMDb> movies;

    // saved in ViewModel to restore current selection highlighting in NavigationDrawer
    // after returning from Favorites



    public MovieListModel() {
        movies = new ArrayList<>();
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void incrementPage( ){
        page++;
    }

    public List<MovieTMDb> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieTMDb> movies) {
        this.movies = movies;
    }

    public void addMovies(List<MovieTMDb> moviesToBeAdded) {
        Log.d("shit", "Is this happening often? " + moviesToBeAdded.size());
        this.movies.addAll(moviesToBeAdded);
    }

    public int getMovieCount() {
        return movies.size();
    }


}
