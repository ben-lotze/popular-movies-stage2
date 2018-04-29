package com.example.lotze.unclebenspopularmovies.dataHandlers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lotze.unclebenspopularmovies.R;
import com.example.lotze.unclebenspopularmovies.data.MovieReview;

import java.util.List;

/**
 * Created by Benjamin Lotze on 14.04.2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<MovieReview> reviews;

    private boolean isInPreviewMode;
    private static final int REVIEW_PREVIEWS_LENGTH = 3;

//    private final ListItemClickListener onClickListener;

//    public ReviewAdapter(ReviewAdapter.ListItemClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }

//    public interface ListItemClickListener {
//        void onTrailerListItemClick(int clickedItemIndex);
//    }


    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rv_item_review, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    public void swapReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
        Log.d("ReviewAdapter", "populated with reviews: " + getItemCount());
    }

    public void setPreviewMode(boolean isInPreviewMode) {
        this.isInPreviewMode = isInPreviewMode;
    }

    public boolean isInPreviewMode() {
        return isInPreviewMode;
    }


    public MovieReview getReview(int position) {
        return reviews.get(position);
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        }
        // check if in preview mode (in movie detail activity and reduce number of used reviews)
        int itemCount = reviews.size();
        if (isInPreviewMode && itemCount > REVIEW_PREVIEWS_LENGTH) {
            itemCount = REVIEW_PREVIEWS_LENGTH;
        }
        return itemCount;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder
//            implements View.OnClickListener
    {

        private TextView tvAuthor;
        private TextView tvContents;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            this.tvAuthor = itemView.findViewById(R.id.rv_item_reviews_author);
            this.tvContents = itemView.findViewById(R.id.rv_item_reviews_content);
//            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            MovieReview review = getReview(position);
            String author = review.getAuthor();
            this.tvAuthor.setText(author + ":");
            String contents = review.getContent();
            this.tvContents.setText(contents);
        }

//        @Override
//        public void onClick(View v) {
//            int clickedPosition = getAdapterPosition();
//            Log.d("ReviewAdapter", "trailer clicked, notifying listener, index=" + clickedPosition);
//            onClickListener.onTrailerListItemClick(clickedPosition);
//        }
    }
}
