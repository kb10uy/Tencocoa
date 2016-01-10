package org.kb10uy.tencocoa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.twitter.Extractor;

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
    private LinearLayout mImagesLinearLayout;
    private String mCurrentText = "";
    private EditText mStatusText;
    private TextView mTextLength;

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
        mStatusText = (EditText) dialog.findViewById(R.id.NewStatusDialogFragmentStatusText);
        mTextLength = (TextView) dialog.findViewById(R.id.NewStatusDialogFragmentLengthText);
        mImagesLinearLayout = (LinearLayout) dialog.findViewById(R.id.NewStatusDialogFragmentImages);

        dialog.findViewById(R.id.NewStatusDialogFragmentButtonUpdateStatus).setOnClickListener(v -> {
            String text = mStatusText.getText().toString();
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

        mStatusText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCurrentText = mStatusText.getText().toString();
                recalculateLength(mCurrentText);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSelectedImage = new ArrayList<>();
        Bundle arguments = getArguments();
        if (arguments == null) return dialog;
        Status reply = (Status) arguments.getSerializable("ReplyTo");
        if (reply != null) {
            replyToStatus = reply;
            String template = TencocoaHelper.createReplyTemplate(new TencocoaStatus(reply));
            mStatusText.setText(template);
            mStatusText.setSelection(template.length());
            ((TextView) dialog.findViewById(R.id.NewStatusDialogFragmentTitle)).setText(getString(R.string.label_dialog_new_status_reply));
            ((TextView) dialog.findViewById(R.id.NewStatusDialogFragmentReplyUserName)).setText(reply.getUser().getName());
            ((TextView) dialog.findViewById(R.id.NewStatusDialogFragmentReplyText)).setText(reply.getText());
            dialog.findViewById(R.id.NewStatusDialogFragmentReply).setVisibility(View.VISIBLE);
        }

        recalculateLength("");
        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedImage = BhavaAgra.parse(data);
                    updateImageList();
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


    private void updateImageList() {
        if (mSelectedImage.size() == 0) {
            mImagesLinearLayout.setVisibility(View.GONE);
            mCurrentText = mStatusText.getText().toString();
            recalculateLength(mCurrentText);
            return;
        }

        mImagesLinearLayout.setVisibility(View.VISIBLE);
        mImagesLinearLayout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Uri uri : mSelectedImage) {
            View av = inflater.inflate(R.layout.item_status_image, mImagesLinearLayout, false);
            ImageView imv = (ImageView) av.findViewById(R.id.StatusItemImageItem);
            Glide.with(getActivity()).load(uri).into(imv);
            mImagesLinearLayout.addView(av);
        }
        mCurrentText = mStatusText.getText().toString();
        recalculateLength(mCurrentText);
    }

    private void recalculateLength(String s) {
        int rawLength = s.trim().length();
        Extractor ext = new Extractor();
        List<String> urls = ext.extractURLs(s);
        for (String u : urls) rawLength -= (u.length() - 23);
        if (mSelectedImage.size() != 0) rawLength += 25;

        StringBuilder sb = new StringBuilder();
        sb.append(rawLength);
        sb.append("/");
        sb.append(140);

        mTextLength.setText(sb.toString());
    }

    public interface NewStatusDialogFragmentInteractionListener {
        void applyUpdateStatus(String status, List<Uri> mediaUris);

        void applyUpdateStatus(StatusUpdate status, List<Uri> mediaUris);
    }

}
