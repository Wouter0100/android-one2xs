package nl.wouter0100.one2xs.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.wouter0100.one2xs.R;

public class MessageFragment extends Fragment {

    private OnMessageInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set Title and back button correct
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.private_messages);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMessageInteractionListener) {
            mListener = (OnMessageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMessageInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSwipeRefreshLayout.setRefreshing(false);
        mListener = null;
    }

    public interface OnMessageInteractionListener {
        void onMessageClicked(int id, String title);
    }
}
