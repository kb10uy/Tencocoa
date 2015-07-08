package org.kb10uy.tencocoa;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaStatus;

import twitter4j.Status;
import twitter4j.StatusUpdate;


public class NewStatusDialogFragment extends DialogFragment {

    private NewStatusDialogFragmentInteractionListener mListener;
    private Status replyToStatus;

    public static NewStatusDialogFragment newInstance() {
        return new NewStatusDialogFragment();
    }

    public static NewStatusDialogFragment newInstance(TencocoaStatus reply) {
        NewStatusDialogFragment dialog = new NewStatusDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ReplyTo", reply.getShowingStatus());
        dialog.setArguments(bundle);
        return dialog;
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
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(STYLE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_new_status_dialog);
        EditText teStatus = (EditText) dialog.findViewById(R.id.NewStatusDialogFragmentStatusText);
        dialog.findViewById(R.id.NewStatusDialogFragmentButtonUpdateStatus).setOnClickListener(v -> {
            String text = teStatus.getText().toString();
            dismiss();
            updateStatus(text);
        });

        Bundle arguments = getArguments();
        if (arguments == null) return dialog;
        Status reply = (Status) arguments.getSerializable("ReplyTo");
        if (reply != null) {
            replyToStatus = reply;
            String template = TencocoaHelper.createReplyTemplate(new TencocoaStatus(reply));
            teStatus.setText(template);
            teStatus.setSelection(template.length());
            ((TextView) dialog.findViewById(R.id.NewStatusDialogFragmentTitle)).setText(getString(R.string.label_dialog_new_status_reply));
            ((TextView) dialog.findViewById(R.id.NewStatusDialogFragmentReplyUserName)).setText(reply.getUser().getName());
            ((TextView) dialog.findViewById(R.id.NewStatusDialogFragmentReplyText)).setText(reply.getText());
            dialog.findViewById(R.id.NewStatusDialogFragmentReply).setVisibility(View.VISIBLE);
        }
        return dialog;
    }

    public void updateStatus(String text) {
        if (mListener == null) return;
        if (replyToStatus != null) {
            StatusUpdate update = new StatusUpdate(text);
            update.inReplyToStatusId(replyToStatus.getId());
            mListener.applyUpdateStatus(update);
        } else {
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

        void applyUpdateStatus(StatusUpdate status);
    }

}
