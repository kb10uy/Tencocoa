package org.kb10uy.tencocoa;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.DialogFragment;
import android.text.Editable;
import android.text.method.CharacterPickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class NewStatusDialogFragment extends DialogFragment {

    private NewStatusDialogFragmentInteractionListener mListener;

    public static NewStatusDialogFragment newInstance() {
        NewStatusDialogFragment fragment = new NewStatusDialogFragment();
        return fragment;
    }

    public NewStatusDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog=new Dialog(getActivity());
        dialog.getWindow().requestFeature(STYLE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_new_status_dialog);
        ((Button)dialog.findViewById(R.id.NewStatusDialogFragmentButtonUpdateStatus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ((EditText) dialog.findViewById(R.id.NewStatusDialogFragmentEditTextStatusText)).getText().toString();
                dismiss();
                updateStatus(text);
            }
        });
        return dialog;
    }

    public void updateStatus(String text) {
        if (mListener != null) {
            mListener.applyUpdateStatus(text);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NewStatusDialogFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NewStatusDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface NewStatusDialogFragmentInteractionListener {
        public void applyUpdateStatus(String status);
    }

}
