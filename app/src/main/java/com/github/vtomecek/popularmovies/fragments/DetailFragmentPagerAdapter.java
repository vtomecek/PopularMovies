package com.github.vtomecek.popularmovies.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.vtomecek.popularmovies.R;

/**
 * Created by vlado on 3/22/17.
 */

public class DetailFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private Context context;
    private String tabTitles[];

    public DetailFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabTitles = new String[] {
            context.getString(R.string.synopsis),
            context.getString(R.string.trailers),
            context.getString(R.string.reviews)
        };
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SynopsisFragment();
            case 1:
                return new VideosFragment();
            case 2:
                return new ReviewsFragment();
            default:
                return new SynopsisFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}