package com.github.vtomecek.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vlado on 3/22/17.
 */

public class Video implements Parcelable {
    String id;
    String iso_639_1;
    String iso_3166_1;
    String key;
    String name;
    String site;
    int size;
    String type;

    private Video(Parcel in) {
        id = in.readString();
        iso_639_1 = in.readString();
        iso_3166_1 = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public Video(JSONObject json) throws JSONException {
        id = json.getString("id");
        iso_639_1 = json.getString("iso_639_1");
        iso_3166_1 = json.getString("iso_3166_1");
        key = json.getString("key");
        name = json.getString("name");
        site = json.getString("site");
        size = json.getInt("size");
        type = json.getString("type");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(iso_639_1);
        dest.writeString(iso_3166_1);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type);
    }

    public static final Parcelable.Creator<Video> CREATOR
            = new Parcelable.Creator<Video>() {

        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getIso1() {
        return iso_639_1;
    }

    public String getIso2() {
        return iso_3166_1;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public String getThumbnailLink() {
        if (site.equals("YouTube"))
            return "http://img.youtube.com/vi/" + getKey() + "/0.jpg";
        else
            return "http://img.youtube.com/vi/0/0.jpg";
    }

    public String getVideoLink() {
        if (site.equals("YouTube"))
            return "https://www.youtube.com/embed/" + getKey();
        else
            return "http://img.youtube.com/vi/0/0.jpg";
    }

    public String getCaption() {
        return getName() + " (" + getType() + " " + Integer.toString(getSize()) + "p)";
    }
}
