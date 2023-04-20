package com.sandhya.youtube.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sandhya.youtube.fragements.SubscriberFragment;
import com.sandhya.youtube.fragements.channeltabs.AboutFragment;
import com.sandhya.youtube.fragements.channeltabs.HomeFragment;
import com.sandhya.youtube.fragements.channeltabs.PlaylistFragment;
import com.sandhya.youtube.fragements.channeltabs.SubscriptionFragment;
import com.sandhya.youtube.fragements.channeltabs.VIdeosFragment;

public class ChannelTabAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;

    public ChannelTabAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;

            case 1:
                PlaylistFragment playlistFragment = new PlaylistFragment();
                return playlistFragment;

            case 2:
                VIdeosFragment vIdeosFragment = new VIdeosFragment();
                return vIdeosFragment;


            case 3:
                SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
                return subscriptionFragment;

            case 4:
                AboutFragment aboutFragment = new AboutFragment();
                return aboutFragment;
            default:
                return null;
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}