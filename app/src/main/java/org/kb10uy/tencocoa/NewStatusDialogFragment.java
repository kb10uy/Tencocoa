package org.kb10uy.tencocoa;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.EditText;


public class NewStatusDialogFragment extends DialogFragment {

    private NewStatusDialogFragmentInteractionListener mListener;

    public static NewStatusDialogFragment newInstance() {
        return new NewStatusDialogFragment();
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
        dialog.findViewById(R.id.NewStatusDialogFragmentButtonUpdateStatus).setOnClickListener(v -> {
            String text = ((EditText) dialog.findViewById(R.id.NewStatusDialogFragmentEditTextStatusText)).getText().toString();
            dismiss();
            updateStatus(text);
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

    public interface NewStatusDialogFragmentInteractionListener {
        void applyUpdateStatus(String status);
    }

}
