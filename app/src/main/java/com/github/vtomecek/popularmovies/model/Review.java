package com.github.vtomecek.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vlado on 3/23/17.
 */

public class Review implements Parcelable {
    String author;
    String content;

    private Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public Review(JSONObject json) throws JSONException {
        author = json.getString("author");
        content = json.getString("content");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<Review> CREATOR
            = new Parcelable.Creator<Review>() {

        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
