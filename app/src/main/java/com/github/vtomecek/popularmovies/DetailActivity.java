package com.github.vtomecek.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.vtomecek.popularmovies.data.FavouritesDbHelper;
import com.github.vtomecek.popularmovies.data.FavouritesContract;
import com.github.vtomecek.popularmovies.fragments.DetailFragmentPagerAdapter;
import com.github.vtomecek.popularmovies.model.Movie;

import com.github.vtomecek.popularmovies.data.FavouritesContract.*;

/**
 * Created by vlado on 3/22/17.
 */

public class DetailActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private static final String TAG = "DetailActivity";
    Movie movieToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        DetailFragmentPagerAdapter dfpa = new DetailFragmentPagerAdapter(getSupportFragmentManager(),
                DetailActivity.this);
        viewPager.setAdapter(dfpa);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        FavouritesDbHelper dbHelper = new FavouritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            movieToShow = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean favourite = isMovieInDb();
        int icon;
        if (favourite) {
            icon = android.R.drawable.btn_star_big_on;
        } else {
            icon = android.R.drawable.btn_star_big_off;
        }

        getMenuInflater().inflate(R.menu.detail, menu);
        menu.getItem(0).setIcon(icon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favourite_star:
                toggleFavourite(item);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleFavourite(MenuItem item) {
        boolean favourite = isMovieInDb();
        String toastMessage;
        int icon;

        if (favourite) {
            toastMessage = getString(R.string.favourite_del);
            icon = android.R.drawable.btn_star_big_off;
            deleteMovieFromDb();
        } else {
            toastMessage = getString(R.string.favourite_add);
            icon = android.R.drawable.btn_star_big_on;
            addMovieToDb();
        }

        item.setIcon(icon);
        Toast mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();
    }

    private void addMovieToDb() {
        ContentValues cv = new ContentValues();
        cv.put(FavouritesEntry.COLUMN_POSTER_PATH, movieToShow.getPosterPath());
        cv.put(FavouritesEntry.COLUMN_ADULT, movieToShow.getAdult());
        cv.put(FavouritesEntry.COLUMN_OVERVIEW, movieToShow.getOverview());
        cv.put(FavouritesEntry.COLUMN_RELEASE_DATE, movieToShow.getReleaseDate());
        cv.put(FavouritesEntry.COLUMN_ID, movieToShow.getId());
        cv.put(FavouritesEntry.COLUMN_ORIGINAL_TITLE, movieToShow.getOriginalTitle());
        cv.put(FavouritesEntry.COLUMN_TITLE, movieToShow.getTitle());
        cv.put(FavouritesEntry.COLUMN_BACKDROP_PATH, movieToShow.getBackDropPath());
        cv.put(FavouritesEntry.COLUMN_POPULARITY, movieToShow.getPopularity());
        cv.put(FavouritesEntry.COLUMN_VOTE_COUNT, movieToShow.getVoteCount());
        cv.put(FavouritesEntry.COLUMN_VIDEO, movieToShow.getVideo());
        cv.put(FavouritesEntry.COLUMN_VOTE_AVERAGE, movieToShow.getVoteAverage_double());
        Log.v(TAG, "Movie " + movieToShow.getTitle() + " saved in db.");
        //return mDb.insert(FavouritesEntry.TABLE_NAME, null, cv);
        Uri uri = getContentResolver().insert(FavouritesEntry.CONTENT_URI, cv);
        Log.v(TAG, "Movie uri: " + uri.toString());
    }

    private boolean deleteMovieFromDb() {
        long id = movieToShow.getId();
        Log.v(TAG, "Movie " + movieToShow.getTitle() + " deleted from db.");
        /*return mDb.delete(FavouritesEntry.TABLE_NAME,
                FavouritesEntry.COLUMN_ID + "=" + id, null) > 0;*/
        return getContentResolver().delete(Uri.parse(FavouritesEntry.CONTENT_URI.toString() + "/" + id),
                null, null) > 0;
    }

    private boolean isMovieInDb() {
        long id = movieToShow.getId();
        /*return mDb.query(
                FavouritesEntry.TABLE_NAME,
                null,
                FavouritesEntry.COLUMN_ID + "=" + id,
                null,
                null,
                null,
                FavouritesContract.FavouritesEntry.COLUMN_TIMESTAMP
        ).getCount() > 0;*/
        return getContentResolver().query(FavouritesEntry.CONTENT_URI,
                null,
                FavouritesEntry.COLUMN_ID + "=" + id,
                null,
                null).getCount() > 0;
    }

}
