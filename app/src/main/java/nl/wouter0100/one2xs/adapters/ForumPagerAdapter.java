package nl.wouter0100.one2xs.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nl.wouter0100.one2xs.fragments.ForumListFragment;

public class ForumPagerAdapter extends FragmentPagerAdapter {

    private String[] sections = {
        "Algemeen",
        "Handelen",
        "Overige"
    };

    public ForumPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return sections.length;
    }

    @Override
    public Fragment getItem(int position) {
        return ForumListFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sections[position];
    }
}