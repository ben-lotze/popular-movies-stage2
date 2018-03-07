package com.example.lotze.unclebenspopularmovies.dataHandlers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lotze.unclebenspopularmovies.R;
import com.example.lotze.unclebenspopularmovies.data.MovieTMDb;
import com.example.lotze.unclebenspopularmovies.data.TMDbImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Lotze on 17.02.2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<MovieTMDb> movies;
    private Context context;

    private final ListItemClickListener onClickListener;

    public MoviesAdapter(ListItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface ListItemClickListener {
        void onMoviesListItemClick(int clickedItemIndex);
    }


    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView ivMovePoster;
        private TextView tvMovieTitle;
        private TextView tvMovieRating;
        private TextView tvMovieReleaseYear;

        public MovieViewHolder(View itemView) {
            super(itemView);
            this.ivMovePoster = itemView.findViewById(R.id.iv_rv_item_movie_poster);
            this.tvMovieTitle = itemView.findViewById(R.id.tv_rv_item_movie_title);
            this.tvMovieRating = itemView.findViewById(R.id.tv_rv_item_movie_rating);
            this.tvMovieReleaseYear = itemView.findViewById(R.id.tv_rv_item_movie_year);

            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            MovieTMDb movie = getMovie(position);

            String posterUrl = movie.getPosterPath(TMDbImageSize.w342);
            Glide.with(context).load(posterUrl).into(ivMovePoster);

            String title = movie.getTitle();
            tvMovieTitle.setText(title);

            float voteAvg = movie.getVoteAverage();
            tvMovieRating.setText(String.format("%.1f", voteAvg));

            // TODO: more stable/reliable parsing of release year for stage 2
            String releaseYear = movie.getReleaseDate().split("-")[0];
            tvMovieReleaseYear.setText(releaseYear);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Log.d("MainActivity", "movie clicked, notifying listener, index=" + clickedPosition);
            onClickListener.onMoviesListItemClick(clickedPosition);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rv_item_movie, parent, false);
        MovieViewHolder holder = new MovieViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    // TODO: better implementation of swapMovies()
    public void swapMovies(List<MovieTMDb> movies) {
        this.movies = movies;
        notifyDataSetChanged();
        Log.d("MoviesAdapter", "populated with movies: " + getItemCount());
    }

    public void addMovies(List<MovieTMDb> movies) {

        if (this.movies == null) {
            this.movies = new ArrayList<>();
        }
        // just add at end of list (movies from each Json response page are ordered as defined in request)
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public List<MovieTMDb> getMovies() {
        return movies;
    }

    public MovieTMDb getMovie(int position) {
        if (movies == null) {
            return null;
        }
        return movies.get(position);
    }
}
