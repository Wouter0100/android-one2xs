package nl.wouter0100.one2xs.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import nl.wouter0100.one2xs.R;
import nl.wouter0100.one2xs.models.Forum;

/**
 * Forum Fragment, views a list of forums within a section
 */
public class ForumFragment extends ListFragment {

    // Listener to talk with our activity
    private OnForumInteractionListener mListener;

    // Holds the forums this Fragment is displaying
    private Forum[] mForums;

    /**
     * Create a new instance of ForumFragment
     *
     * @param forums An subforum object with all necressey details
     */
    public static ForumFragment newInstance(Forum[] forums) {
        ForumFragment fragment = new ForumFragment();

        Bundle args = new Bundle();
        args.putSerializable("forums", forums);

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * When creating, retrieve this forums from the bundles
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mForums = (Forum[]) getArguments().getSerializable("forums");
    }

    /**
     * The Fragment's UI is just a simple list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    /**
     * Set a list of the mForums
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mForums));
    }

    /**
     * On click handler for the list
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Forum forum = mForums[position];

        Log.i("FragmentList", "Item clicked: " + forum.getName());

        mListener.onForumSelected(forum);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnForumInteractionListener) {
            mListener = (OnForumInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnForumInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnForumInteractionListener {
        void onForumSelected(Forum forum);
    }
}