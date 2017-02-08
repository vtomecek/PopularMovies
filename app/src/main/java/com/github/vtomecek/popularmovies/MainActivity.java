package com.github.vtomecek.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.vtomecek.popularmovies.adapter.MovieAdapter;
import com.github.vtomecek.popularmovies.model.Movie;
import com.github.vtomecek.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
    implements MovieAdapter.ListItemClickListener {

    final private Context context = this;
    MovieAdapter.ListItemClickListener listener = this;
    RecyclerView mGridView;
    ProgressBar mProgressBar;
    MovieAdapter mMovieAdapter;
    Toast mToast;
    List<Movie> movieList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (RecyclerView) findViewById(R.id.movie_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        movieListSearchQuery(NetworkUtils.POPULAR_MOVIES);
    }

    private void movieListSearchQuery(int choice) {
        URL searchUrl = NetworkUtils.buildUrl(choice);
        new MovieListQueryTask().execute(searchUrl);
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
                movieList = getMovieList(searchResults);
                mMovieAdapter = new MovieAdapter(movieList, listener);
                mGridView.setAdapter(mMovieAdapter);
            } else {
                String message = getString(R.string.no_internet);
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_rated:
                movieListSearchQuery(NetworkUtils.TOP_MOVIES);
                return true;
            case R.id.popular:
                movieListSearchQuery(NetworkUtils.POPULAR_MOVIES);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
}
