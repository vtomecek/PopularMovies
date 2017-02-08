package com.github.vtomecek.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import com.github.vtomecek.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by vlado on 2/7/17.
 */

public class NetworkUtils {

    // TODO change this key
    final static String API_KEY = "<YOUR_API_KEY>";

    final static String TOP_MOVIES_URL = "http://api.themoviedb.org/3/movie/top_rated";
    final static String POPULAR_MOVIES_URL = "http://api.themoviedb.org/3/movie/popular";
    final static String PARAM_API = "api_key";

    // choices
    public final static int TOP_MOVIES = 1;
    public final static int POPULAR_MOVIES = 2;

    public static URL buildUrl(int choice) {
        String base_url;

        switch(choice) {
            case TOP_MOVIES:
                base_url = TOP_MOVIES_URL;
                break;
            case POPULAR_MOVIES:
                base_url = POPULAR_MOVIES_URL;
                break;
            default:
                base_url = TOP_MOVIES_URL;
        }

        Uri builtUri = Uri.parse(base_url).buildUpon()
                .appendQueryParameter(PARAM_API, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(3000);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}