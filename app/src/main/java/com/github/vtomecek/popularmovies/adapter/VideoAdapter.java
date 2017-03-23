package com.github.vtomecek.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vtomecek.popularmovies.R;
import com.github.vtomecek.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vlado on 3/22/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    final private ListItemClickListener mOnClickListener;

    private int mNumberItems;

    final private List<Video> videos;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public VideoAdapter(List<Video> videos, ListItemClickListener listener) {
        mNumberItems = videos.size();
        mOnClickListener = listener;
        this.videos = videos;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, viewGroup, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView mImage;
        TextView mCaption;

        public VideoViewHolder(View itemView) {
            super(itemView);

            mImage = (ImageView) itemView.findViewById(R.id.video_list_item);
            mCaption = (TextView) itemView.findViewById(R.id.video_list_item_caption);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            String link = videos.get(listIndex).getThumbnailLink();
            Picasso.with(mImage.getContext()).load(link).into(mImage);
            String caption = videos.get(listIndex).getCaption();
            mCaption.setText(caption);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
