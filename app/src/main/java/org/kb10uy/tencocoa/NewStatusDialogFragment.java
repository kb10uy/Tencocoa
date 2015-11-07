package org.kb10uy.tencocoa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.kb10uy.bhavaagra.BhavaAgra;
import org.kb10uy.bhavaagra.Rhapsody;
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaStatus;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.StatusUpdate;


public class NewStatusDialogFragment extends DialogFragment {

    public static final int INTENT_CAMERA = 0x105;
    private NewStatusDialogFragmentInteractionListener mListener;
    private Status replyToStatus;
    private List<Uri> mSelectedImage;
    private SharedPreferences pref;

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
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(STYLE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_new_status_dialog);
        EditText teStatus = (EditText) dialog.findViewById(R.id.NewStatusDialogFragmentStatusText);

        dialog.findViewById(R.id.NewStatusDialogFragmentButtonUpdateStatus).setOnClickListener(v -> {
            String text = teStatus.getText().toString();
            dismiss();
            updateStatus(text);
        });
        dialog.findViewById(R.id.NewStatusDialogFragmentButtonAddImage).setOnClickListener(v -> {
            startActivityForResult(
                    BhavaAgra
                            .from(getActivity())
                            .cameraPath(Rhapsody.CAMERA_DCIM + getString(R.string.uri_camera_suffix))
                            .count(0, 4)
                            .maxQuality(2048, 2048)
                            .resume(mSelectedImage)
                            .build(),
                    INTENT_CAMERA
            );
        });

        mSelectedImage = new ArrayList<>();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedImage = BhavaAgra.parse(data);
                }
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void updateStatus(String text) {
        if (mListener == null) return;
        if (replyToStatus != null) {
            StatusUpdate update = new StatusUpdate(text);
            update.inReplyToStatusId(replyToStatus.getId());
            mListener.applyUpdateStatus(update, mSelectedImage);
        } else {
            mListener.applyUpdateStatus(text, mSelectedImage);
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
        void applyUpdateStatus(String status, List<Uri> mediaUris);

        void applyUpdateStatus(StatusUpdate status, List<Uri> mediaUris);
    }

}
