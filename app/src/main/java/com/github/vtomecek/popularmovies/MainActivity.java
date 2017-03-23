package com.github.vtomecek.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.vtomecek.popularmovies.adapter.MovieAdapter;
import com.github.vtomecek.popularmovies.data.FavouritesDbHelper;
import com.github.vtomecek.popularmovies.data.FavouritesContract;
import com.github.vtomecek.popularmovies.model.Movie;
import com.github.vtomecek.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity
    implements MovieAdapter.ListItemClickListener {

    private static final String TAG = "MainActivity";
    final private Context context = this;
    MovieAdapter.ListItemClickListener listener = this;
    RecyclerView mGridView;
    ProgressBar mProgressBar;
    MovieAdapter mMovieAdapter;
    Toast mToast;
    List<Movie> movieList;
    int pages_loaded=0, pages_total=1, movies_loaded=0;
    int choice_order = NetworkUtils.POPULAR_MOVIES;
    private SQLiteDatabase mDb;
    RecyclerView.LayoutManager mLayoutManager;
    Parcelable mListState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable("LIST_STATE_KEY");
            choice_order = savedInstanceState.getInt("choice_order");
            pages_loaded = savedInstanceState.getInt("pages_loaded");
            pages_total = savedInstanceState.getInt("pages_total");
            movies_loaded = savedInstanceState.getInt("movies_loaded");
            Movie[] movieListArray = new Movie[movies_loaded];
            movieListArray = (Movie[]) savedInstanceState.getParcelableArray("movies");
            movieList = new ArrayList<Movie>(Arrays.asList(movieListArray));
        }

        mGridView = (RecyclerView) findViewById(R.id.movie_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLayoutManager = mGridView.getLayoutManager();

        mGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!mGridView.canScrollVertically(1) && pages_loaded<pages_total) {
                    movieListSearchQuery();
                }
            }
        });

        if(choice_order==NetworkUtils.FAVOURITE_MOVIES)
            movieListSearchDbQuery();
        else
            movieListSearchQuery();

        FavouritesDbHelper dbHelper = new FavouritesDbHelper(this);
        mDb = dbHelper.getReadableDatabase();
    }

    private void movieListSearchQuery() {
        URL searchUrl = NetworkUtils.buildUrl(choice_order, pages_loaded+1);
        Log.v(TAG, "loaded: " + pages_loaded);
        Log.v(TAG, "choice: " + choice_order);
        Log.v(TAG, "URL: " + searchUrl.toString());
        pages_loaded++;
        Log.v(TAG, "movieListSearchQuery loaded page #" + Integer.toString(pages_loaded));
        new MovieListQueryTask().execute(searchUrl);
    }

    private void movieListSearchDbQuery() {
        pages_loaded = 1;
        pages_total = 1;
        new MovieListDbQueryTask().execute();
    }

    public class MovieListQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
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
            mProgressBar.setVisibility(View.INVISIBLE);
            if (searchResults != null && !searchResults.equals("")) {
                updatePageResults(searchResults);
            } else {
                String message = getString(R.string.no_internet);
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }
    }

    public class MovieListDbQueryTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            try {
                return getContentResolver().query(FavouritesContract.FavouritesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        FavouritesContract.FavouritesEntry.COLUMN_TIMESTAMP);

            } catch (Exception e) {
                Log.e(TAG, "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (cursor != null) {
                updateFavourites(cursor);
            }
        }
    }

    /*
    private Cursor getAllFavourites() {
        return mDb.query(
                FavouritesContract.FavouritesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavouritesContract.FavouritesEntry.COLUMN_TIMESTAMP
        );
    }
    */

    private void updatePageResults(String searchResults) {
        if (movieList == null || movieList.size()==0) {
            movieList = getMovieList(searchResults);
        } else {
            List<Movie> newMovies = getMovieList(searchResults);
            movieList.addAll(newMovies);
        }
        if(mMovieAdapter==null) {
            mMovieAdapter = new MovieAdapter(movieList, listener);
            mGridView.setAdapter(mMovieAdapter);
        } else {
            //mMovieAdapter.addNewMovies(newMovies);
            mMovieAdapter.addNewMovies(movieList);
            mMovieAdapter.notifyDataSetChanged();
        }
        Log.v(TAG, "movie count: " + Integer.toString(mMovieAdapter.getItemCount()));
        pages_total = getTotalPages(searchResults);
    }

    private void updateFavourites(Cursor cursor) {
        //Cursor cursor = getAllFavourites();
        movieList = getMovieList(cursor);
        mMovieAdapter = new MovieAdapter(movieList, listener);
        mGridView.setAdapter(mMovieAdapter);
        pages_total = 1;
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        pages_loaded = 0;
        pages_total = 1;
        movieList.clear();

        switch (item.getItemId()) {
            case R.id.top_rated:
                choice_order = NetworkUtils.TOP_MOVIES;
                movieListSearchQuery();
                return true;
            case R.id.popular:
                choice_order = NetworkUtils.POPULAR_MOVIES;
                movieListSearchQuery();
                return true;
            case R.id.favourite:
                choice_order = NetworkUtils.FAVOURITE_MOVIES;
                //updateFavourites();
                movieListSearchDbQuery();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getTotalPages(String json) {
        int total_pages = -1;
        try {
            total_pages = new JSONObject(json).getInt("total_pages");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return total_pages;
    }

    private List<Movie> getMovieList(String json) {
        List<Movie> res = new ArrayList<>();

        try {
            JSONArray movies = new JSONObject(json).getJSONArray("results");

            for (int i = 0; i < movies.length(); i++) {
                Movie movieModel = new Movie(movies.getJSONObject(i));
                res.add(movieModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    private List<Movie> getMovieList(Cursor cursor) {
        List<Movie> res = new ArrayList<>();

        int i = 0;
        while (cursor.moveToNext()) {
            Movie movieModel = new Movie(cursor);
            res.add(movieModel);
            i++;
        }

        return res;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        //String toastMessage = movieList.get(clickedItemIndex).getTitle();
        //mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        //mToast.show();

        Movie movieToShow = movieList.get(clickedItemIndex);
        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, movieToShow);
        startActivity(startChildActivityIntent);
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        Movie[] movieListArray = new Movie[movieList.size()];
        movieListArray = movieList.toArray(movieListArray);

        // Save list state
        mListState = mLayoutManager.onSaveInstanceState();
        state.putParcelable("LIST_STATE_KEY", mListState);
        state.putInt("choice_order", choice_order);
        state.putInt("pages_loaded", pages_loaded);
        state.putInt("pages_total", pages_total);
        state.putInt("movies_loaded", movies_loaded);
        state.putParcelableArray("movies", movieListArray);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }
}
