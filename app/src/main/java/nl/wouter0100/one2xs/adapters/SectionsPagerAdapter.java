package nl.wouter0100.one2xs.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nl.wouter0100.one2xs.fragments.ForumsFragment;
import nl.wouter0100.one2xs.models.Section;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Section[] mSections;

    public SectionsPagerAdapter(FragmentManager fm, Section[] sections) {
        super(fm);

        this.mSections = sections;
    }

    @Override
    public int getCount() {
        return mSections.length;
    }

    @Override
    public Fragment getItem(int position) {
        return ForumsFragment.newInstance(mSections[position].getSubforums());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mSections[position].getName();
    }
}