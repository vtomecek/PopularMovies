package com.github.vtomecek.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vtomecek.popularmovies.R;
import com.github.vtomecek.popularmovies.model.Review;

import java.util.List;

/**
 * Created by vlado on 3/23/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private int mNumberItems;

    final private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        mNumberItems = reviews.size();
        this.reviews = reviews;
    }

    public void addNewMovies(List<Review> reviews) {
        this.reviews.addAll(reviews);
        mNumberItems = this.reviews.size();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, viewGroup, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView mAuthor;
        TextView mContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            mAuthor = (TextView) itemView.findViewById(R.id.review_list_item_author);
            mContent = (TextView) itemView.findViewById(R.id.review_list_item_content);
        }

        void bind(int listIndex) {
            String author = reviews.get(listIndex).getAuthor();
            String content = reviews.get(listIndex).getContent();
            mAuthor.setText(author);
            mContent.setText(content);
        }
    }
}
