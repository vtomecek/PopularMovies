package com.github.vtomecek.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vtomecek.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    ImageView mBackdrop;
    TextView mTitle;
    TextView mReleaseDate;
    TextView mVoteAverage;
    TextView mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBackdrop = (ImageView) findViewById(R.id.backdrop);
        mTitle = (TextView) findViewById(R.id.title);
        mReleaseDate = (TextView) findViewById(R.id.release_date);
        mVoteAverage = (TextView) findViewById(R.id.vote_average);
        mOverview = (TextView) findViewById(R.id.overview);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            Movie movieToShow = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
            String link = movieToShow.getBackdropLink();
            Picasso.with(mBackdrop.getContext()).load(link).into(mBackdrop);
            mTitle.setText(movieToShow.getTitle());
            mReleaseDate.setText(movieToShow.getReleaseDate());
            mVoteAverage.setText(movieToShow.getVoteAverage());
            mOverview.setText(movieToShow.getOverview());
        }
    }
}
