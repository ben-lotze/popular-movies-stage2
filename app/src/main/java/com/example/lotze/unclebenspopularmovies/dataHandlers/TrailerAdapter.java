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
import com.example.lotze.unclebenspopularmovies.data.Trailer;

import java.util.List;

/**
 * Created by Benjamin Lotze on 14.04.2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Context context;
    private List<Trailer> trailers;

    private final ListItemClickListener onClickListener;

    public TrailerAdapter(TrailerAdapter.ListItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface ListItemClickListener {
        void onTrailerListItemClick(int clickedItemIndex);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rv_item_trailer, parent, false);
        TrailerAdapter.TrailerViewHolder holder = new TrailerAdapter.TrailerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    public void swapTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
        Log.d("TrailerAdapter", "populated with trailers: " + getItemCount());
    }

    public Trailer getTrailer(int position) {
        return trailers.get(position);
    }
    @Override
    public int getItemCount() {
        if (trailers == null) { return 0; }
        return trailers.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView ivThumbnail;
        private TextView tvTitle;
        private TextView tvAuthor;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            this.ivThumbnail = itemView.findViewById(R.id.item_trailer_thumbnail);
            this.tvTitle = itemView.findViewById(R.id.item_trailer_title);
            this.tvAuthor = itemView.findViewById(R.id.item_trailer_author);

            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Trailer trailer = getTrailer(position);
            String trailerName = trailer.getName();
            this.tvTitle.setText(trailerName);
            String author = trailer.getAuthorName();
            this.tvAuthor.setText(author);
            String thumbUrl = trailer.getThumbnailUrl();
            Glide.with(context).load(thumbUrl).into(ivThumbnail);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Log.d("TrailerAdapter", "trailer clicked, notifying listener, index=" + clickedPosition);
            onClickListener.onTrailerListItemClick(clickedPosition);
        }
    }
}
