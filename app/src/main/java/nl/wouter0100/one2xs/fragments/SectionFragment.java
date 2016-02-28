package nl.wouter0100.one2xs.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.wouter0100.one2xs.R;
import nl.wouter0100.one2xs.adapters.SectionPagerAdapter;
import nl.wouter0100.one2xs.models.Forum;
import nl.wouter0100.one2xs.models.Section;

/**
 * SectionFragment, shows the sections and within it there forums
 */
public class SectionFragment extends Fragment {

    private SectionPagerAdapter mAdapter;
    private ViewPager mPager;

    public SectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SectionFragment.
     */
    public static SectionFragment newInstance() {
        return new SectionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_section, container, false);

        // TODO: automatically receive from one2xs, it may be different depending on the user
        Section[] sections = new Section[3];

        Forum[] algemeenForums = new Forum[4];
        algemeenForums[0] = new Forum(1, "Mededeling", null);
        algemeenForums[1] = new Forum(9, "Vragen", null);
        algemeenForums[2] = new Forum(10, "Suggesties", null);
        algemeenForums[3] = new Forum(11, "Bugs", null);

        sections[0] = new Section("Algemeen", algemeenForums);

        Forum[] handelenForums = new Forum[2];
        handelenForums[0] = new Forum(3, "Aanbiedingen", null);
        handelenForums[1] = new Forum(4, "Aanvragen", null);

        sections[1] = new Section("Handelen", handelenForums);

        Forum[] overigeForums = new Forum[6];
        overigeForums[0] = new Forum(8, "Webwereld", null);
        overigeForums[1] = new Forum(13, "Tutorials", null);
        overigeForums[2] = new Forum(15, "Prijsvragen & contests", null);
        overigeForums[3] = new Forum(19, "Games", null);
        overigeForums[4] = new Forum(5, "Offtopic", null);
        overigeForums[5] = new Forum(20, "Onzin", null);

        sections[2] = new Section("Overige", overigeForums);

        // Set Pager adapter
        mAdapter = new SectionPagerAdapter(getFragmentManager(), sections);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mPager);

        // Set Title and back button correct
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Forum");

        return view;
    }
}
