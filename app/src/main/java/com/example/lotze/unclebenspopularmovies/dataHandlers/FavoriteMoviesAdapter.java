package com.example.lotze.unclebenspopularmovies.dataHandlers;

import android.content.Context;
import android.database.Cursor;
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
import com.example.lotze.unclebenspopularmovies.db.FavoriteMovieContract;

/**
 * Created by Benjamin Lotze on 17.02.2018.
 */

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.MovieViewHolder> {

    private static final String TAG = "FavMoviesAdapter";

    private Cursor cursor;
    private Context context;


    private final ListItemClickListener onClickListener;
    public interface ListItemClickListener {
        void onMoviesListItemClick(int clickedItemIndex);
    }

    public FavoriteMoviesAdapter(ListItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }




    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {


        private ImageView ivMoviePoster;
        private TextView tvMovieTitle;
        private TextView tvReleaseDate;


        public MovieViewHolder(View itemView) {
            super(itemView);

            this.ivMoviePoster = itemView.findViewById(R.id.item_fav_movie_poster);
            this.tvMovieTitle = itemView.findViewById(R.id.item_fav_movie_title);
            this.tvReleaseDate = itemView.findViewById(R.id.item_fav_movie_release_date);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {

            Log.d(TAG, "bind() called, position="+position);

            MovieTMDb movie = getMovieAtPosition(position);
//            int movieId = movie.getId();

            String title = movie.getTitle();
            tvMovieTitle.setText(title);

            String posterUrl = movie.getPosterPath(TMDbImageSize.w342);
            Glide.with(context).load(posterUrl).into(ivMoviePoster);

            String releaseDate = movie.getReleaseDate();
            tvReleaseDate.setText(releaseDate);


            // set favId as tag in holder (for swipe-to-remove)
            cursor.moveToPosition(position);
            int favIdIndex = cursor.getColumnIndex(FavoriteMovieContract.FavMovieEntry._ID);
            final int favId = cursor.getInt(favIdIndex);
            this.itemView.setTag(R.id.id_movie_fav_id, favId);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Log.d("MainActivity", "fav movie clicked, notifying listener, index=" + clickedPosition);
            onClickListener.onMoviesListItemClick(clickedPosition);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rv_item_fav_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
        Log.d(TAG, "swapCursor(): " + getItemCount() + " items");
    }


    public MovieTMDb getMovieAtPosition(int index) {

        cursor.moveToPosition(index);

        int movieId = cursor.getInt(
                cursor.getColumnIndex(FavoriteMovieContract.FavMovieEntry.COLUMN_MOVIE_ID));
        String title = cursor.getString(
                cursor.getColumnIndex(FavoriteMovieContract.FavMovieEntry.COLUMN_TITLE));
        final MovieTMDb movie = new MovieTMDb(movieId, title);

        String posterUrl = cursor.getString(
                cursor.getColumnIndex(FavoriteMovieContract.FavMovieEntry.COLUMN_POSTER_PATH));
        movie.setPosterPath(posterUrl);

        String backdropUrl = cursor.getString(
                cursor.getColumnIndex(FavoriteMovieContract.FavMovieEntry.COLUMN_BACKDROP_PATH));
        movie.setBackdropPath(backdropUrl);

        String releaseDateStr = cursor.getString(
                cursor.getColumnIndex(FavoriteMovieContract.FavMovieEntry.COLUMN_DATE_RELEASED));
        movie.setReleaseDate(releaseDateStr);

        long timestampAddedFav = cursor.getLong(
                cursor.getColumnIndex(FavoriteMovieContract.FavMovieEntry.COLUMN_TIMESTAMP_SAVED));
//        movie.setTimestampAddedAsFav(timestampAddedFav);

        Log.d(TAG, "getMovieAtPosition(), " +
                "timestamp=" + timestampAddedFav +
                ", released=" + releaseDateStr +
                ", poster=" + posterUrl);
        return movie;
    }

}
