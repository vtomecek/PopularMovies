package com.github.vtomecek.popularmovies.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.github.vtomecek.popularmovies.R;
import com.github.vtomecek.popularmovies.adapter.ReviewAdapter;
import com.github.vtomecek.popularmovies.model.Movie;
import com.github.vtomecek.popularmovies.model.Review;
import com.github.vtomecek.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment {

    private static final String TAG = "ReviewsFragment";
    RecyclerView mGridView;
    ProgressBar mProgressBar;
    ReviewAdapter mReviewAdapter;
    Toast mToast;
    List<Review> reviewList;
    int pages_loaded=0, pages_total=1;
    int movieId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        mGridView = (RecyclerView) view.findViewById(R.id.review_list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_reviews);

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            Movie movieToShow = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
            movieId = movieToShow.getId();
            reviewListSearchQuery();
        }

        mGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!mGridView.canScrollVertically(1) && pages_loaded<pages_total) {
                    reviewListSearchQuery();
                }
            }
        });

        return view;
    }

    private void reviewListSearchQuery() {
        pages_loaded++;
        URL searchUrl = NetworkUtils.buildReviewsUrl(movieId, pages_loaded);
        new ReviewsFragment.ReviewListQueryTask().execute(searchUrl);
    }

    public class ReviewListQueryTask extends AsyncTask<URL, Void, String> {

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
                if (reviewList == null) {
                    reviewList = getReviewList(searchResults);
                    mReviewAdapter = new ReviewAdapter(reviewList);
                    mGridView.setAdapter(mReviewAdapter);
                } else {
                    List<Review> newMovies = getReviewList(searchResults);
                    mReviewAdapter.addNewMovies(newMovies);
                    mReviewAdapter.notifyDataSetChanged();
                    Log.v(TAG, "movie count: " + Integer.toString(mReviewAdapter.getItemCount()));
                }
                pages_total = getTotalPages(searchResults);
            } else {
                String message = getString(R.string.no_internet);
                Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }
    }

    private List<Review> getReviewList(String json) {
        List<Review> res = new ArrayList<>();

        try {
            JSONArray reviews = new JSONObject(json).getJSONArray("results");

            for (int i = 0; i < reviews.length(); i++) {
                Review reviewModel = new Review(reviews.getJSONObject(i));
                res.add(reviewModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    private int getTotalPages(String json) {
        int total_pages = -1;
        try {
            total_pages = new JSONObject(json).getInt("total_pages");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "total pages: " + Integer.toString(total_pages));
        return total_pages;
    }
}