package org.kb10uy.tencocoa;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.model.TencocoaStatus;

import twitter4j.Status;
import twitter4j.User;


public class StatusDetailDialogFragment extends DialogFragment {
    private StatusDetailInteractionListener mListener;
    private TencocoaStatus mTargetStatus;

    public static StatusDetailDialogFragment newInstance(TencocoaStatus status) {
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(STYLE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_status_detail_dialog);
        setStatusInfo(dialog);
        return dialog;
    }

    private void setStatusInfo(Dialog view) {
        User user = mTargetStatus.getShowingStatus().getUser();
        ((TextView) view.findViewById(R.id.StatusDetailTextViewUserName)).setText(user.getName());
        ((TextView) view.findViewById(R.id.StatusDetailTextViewScreenName)).setText(user.getScreenName());
        ((TextView) view.findViewById(R.id.StatusDetailTextViewStatusText)).setText(mTargetStatus.getShowingStatus().getText());
        Glide.with(getActivity()).load(user.getOriginalProfileImageURLHttps()).into(((ImageView) view.findViewById(R.id.StatusDetailImageViewUserProfile)));

        ((Button) view.findViewById(R.id.StatusDetailButtonFav)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite();
                dismiss();
            }
        });
        ((Button) view.findViewById(R.id.StatusDetailButtonFavRetweet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteAndRetweet();
                dismiss();
            }
        });
        ((Button) view.findViewById(R.id.StatusDetailButtonRetweet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retweet();
                dismiss();

            }
        });
        ((Button) view.findViewById(R.id.StatusDetailButtonOthers)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                //favorite();
            }
        });
    }

    private void favorite() {
        mListener.getWritePermissionService().favoriteStatus(mTargetStatus.getSourceStatus().getId());
    }

    private void favoriteAndRetweet() {
        mListener.getWritePermissionService().favrtStatus(mTargetStatus.getSourceStatus().getId());
    }

    private void retweet() {
        mListener.getWritePermissionService().retweetStatus(mTargetStatus.getSourceStatus().getId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTargetStatus = (TencocoaStatus) getArguments().getSerializable("Status");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (StatusDetailInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement StatusDetailInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface StatusDetailInteractionListener {
        public TencocoaWritePermissionService getWritePermissionService();
    }

}
