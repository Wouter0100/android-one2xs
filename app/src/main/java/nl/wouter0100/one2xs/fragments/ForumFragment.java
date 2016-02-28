package nl.wouter0100.one2xs.fragments;

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

public class ForumFragment extends ListFragment {

    // Holds the forums this Fragment is displaying
    private Forum[] mForums;

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
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
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mForums = (Forum[]) getArguments().getSerializable("forums");
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mForums));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }
}