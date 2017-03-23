package com.github.vtomecek.popularmovies.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vtomecek.popularmovies.R;
import com.github.vtomecek.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class SynopsisFragment extends Fragment {

    ImageView mBackdrop;
    TextView mTitle;
    TextView mReleaseDate;
    TextView mVoteAverage;
    TextView mOverview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        //setContentView(R.layout.fragment_detail);

        mBackdrop = (ImageView) view.findViewById(R.id.backdrop);
        mTitle = (TextView) view.findViewById(R.id.title);
        mReleaseDate = (TextView) view.findViewById(R.id.release_date);
        mVoteAverage = (TextView) view.findViewById(R.id.vote_average);
        mOverview = (TextView) view.findViewById(R.id.overview);

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            Movie movieToShow = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
            String link = movieToShow.getBackdropLink();
            Picasso.with(mBackdrop.getContext()).load(link).into(mBackdrop);
            mTitle.setText(movieToShow.getTitle());
            mReleaseDate.setText(movieToShow.getReleaseDate());
            mVoteAverage.setText(movieToShow.getVoteAverage());
            mOverview.setText(movieToShow.getOverview());
        }

        return view;
    }
}
