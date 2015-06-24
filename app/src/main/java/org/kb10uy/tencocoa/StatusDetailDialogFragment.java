package org.kb10uy.tencocoa;

import android.app.Activity;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import twitter4j.Status;


public class StatusDetailDialogFragment extends DialogFragment {
    //private OnFragmentInteractionListener mListener;
    private Status mTargetStatus;

    // TODO: Rename and change types and number of parameters
    public static StatusDetailDialogFragment newInstance(Status status) {
        StatusDetailDialogFragment fragment = new StatusDetailDialogFragment();
        Bundle arg = new Bundle();
        arg.putSerializable("Status", status);
        fragment.setArguments(arg);
        return fragment;
    }

    public StatusDetailDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTargetStatus = (Status) getArguments().getSerializable("Status");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_detail_dialog, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    */
}
