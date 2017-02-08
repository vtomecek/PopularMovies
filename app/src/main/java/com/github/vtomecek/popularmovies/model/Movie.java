package com.github.vtomecek.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vlado on 2/7/17.
 */

public class Movie implements Parcelable {
    private String poster_path;
    private boolean adult;
    private String overview;
    private String release_date;
    private int id;
    private String original_title;
    private String title;
    private String backdrop_path;
    private double popularity;
    private int vote_count;
    private boolean video;
    private double vote_average;

    private Movie(Parcel in) {
        poster_path = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        release_date = in.readString();
        id = in.readInt();
        original_title = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readDouble();
        vote_count = in.readInt();
        video = in.readByte() != 0;
        vote_average = in.readDouble();
    }

    public Movie(JSONObject json) throws JSONException {
        poster_path = json.getString("poster_path");
        adult = json.getBoolean("adult");
        overview = json.getString("overview");
        release_date = json.getString("release_date");
        id = json.getInt("id");
        original_title = json.getString("original_title");
        title = json.getString("title");
        backdrop_path = json.getString("backdrop_path");
        popularity = json.getDouble("popularity");
        vote_count = json.getInt("vote_count");
        video = json.getBoolean("video");
        vote_average = json.getDouble("vote_average");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeDouble(popularity);
        dest.writeInt(vote_count);
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeDouble(vote_average);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getThumbnailLink() {
        return "http://image.tmdb.org/t/p/w185/" + poster_path;
    }

    public String getBackdropLink() {
        return "http://image.tmdb.org/t/p/w780/" + backdrop_path;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getVoteAverage() {
        return String.format("%.2g%n", vote_average);
    }

    public String getOverview() {
        return overview;
    }
}
