package com.github.vtomecek.popularmovies.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.vtomecek.popularmovies.R;
import com.github.vtomecek.popularmovies.adapter.VideoAdapter;
import com.github.vtomecek.popularmovies.model.Movie;
import com.github.vtomecek.popularmovies.model.Video;
import com.github.vtomecek.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment implements VideoAdapter.ListItemClickListener {

    private static final String TAG = "VideosFragment";
    VideoAdapter.ListItemClickListener listener = this;
    RecyclerView mGridView;
    ProgressBar mProgressBar;
    VideoAdapter mVideoAdapter;
    Toast mToast;
    List<Video> videoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        mGridView = (RecyclerView) view.findViewById(R.id.video_list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_videos);

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            Movie movieToShow = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
            int id = movieToShow.getId();
            videoListSearchQuery(id);
        }

        return view;
    }

    private void videoListSearchQuery(int id) {
        URL searchUrl = NetworkUtils.buildVideosUrl(id);
        new VideosFragment.VideoListQueryTask().execute(searchUrl);
    }

    public class VideoListQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            Log.v(TAG, "onPreExecute");
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            Log.v(TAG, "onPostExecute" + searchResults);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (searchResults != null && !searchResults.equals("")) {
                videoList = getVideoList(searchResults);
                mVideoAdapter = new VideoAdapter(videoList, listener);
                mGridView.setAdapter(mVideoAdapter);
            } else {
                String message = getString(R.string.no_internet);
                Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }
    }

    private List<Video> getVideoList(String json) {
        List<Video> res = new ArrayList<>();

        try {
            JSONArray videos = new JSONObject(json).getJSONArray("results");

            for (int i = 0; i < videos.length(); i++) {
                Video videoModel = new Video(videos.getJSONObject(i));
                res.add(videoModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        String url = videoList.get(clickedItemIndex).getVideoLink();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
