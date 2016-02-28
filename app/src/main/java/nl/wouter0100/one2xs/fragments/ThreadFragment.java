package nl.wouter0100.one2xs.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.wouter0100.one2xs.R;
import nl.wouter0100.one2xs.models.Forum;

/**
 * ThreadFragment, view a threads list
 */
public class ThreadFragment extends Fragment {

    private Forum mForum;

    public ThreadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ThreadFragment.
     */
    public static ThreadFragment newInstance(Forum forum) {
        ThreadFragment fragment = new ThreadFragment();

        Bundle args = new Bundle();
        args.putSerializable("forum", forum);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mForum = (Forum) getArguments().getSerializable("forum");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);

        ((TextView) view.findViewById(R.id.threads_forum_name)).setText(mForum.getName());

        // Set title
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(mForum.getName());

        // Visible the FAB
        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Add new thread", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
