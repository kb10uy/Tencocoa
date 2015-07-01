package org.kb10uy.tencocoa;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class SearchFragment extends Fragment {

    //private OnNotificationFragmentInteractionListener mListener;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (getArguments() != null) {

        }
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
        try {
            mListener = (OnNotificationFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNotificationFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /*
    public interface OnSearchFragmentInteractionListener {
        void onNotificationFragmentInteraction();
    }
    */
}
