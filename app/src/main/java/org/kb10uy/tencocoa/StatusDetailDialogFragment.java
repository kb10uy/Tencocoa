package org.kb10uy.tencocoa;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaStatus;

import twitter4j.User;


public class StatusDetailDialogFragment extends DialogFragment {
    private StatusDetailInteractionListener mListener;
    private TencocoaStatus mTargetStatus;
    private Context ctx;

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
        ctx = getActivity();
        final Dialog dialog = new Dialog(ctx);

        dialog.getWindow().requestFeature(STYLE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_status_detail_dialog);
        setStatusInfo(dialog);
        return dialog;
    }

    private void setStatusInfo(Dialog view) {
        User user = mTargetStatus.getShowingStatus().getUser();
        ((TextView) view.findViewById(R.id.StatusDetailTextViewUserName)).setText(user.getName());
        ((TextView) view.findViewById(R.id.StatusDetailTextViewScreenName)).setText(user.getScreenName());
        ((TextView) view.findViewById(R.id.StatusDetailTextViewStatusText)).setText(mTargetStatus.getReplacedText());
        ((TextView) view.findViewById(R.id.StatusDetailTextViewCreatedAt)).setText(TencocoaHelper.getAbsoluteTimeString(mTargetStatus.getShowingStatus().getCreatedAt()));
        Glide.with(getActivity()).load(user.getBiggerProfileImageURLHttps()).into(((ImageView) view.findViewById(R.id.StatusDetailImageViewUserProfile)));

        ToggleButton tbFav = (ToggleButton) view.findViewById(R.id.StatusDetailButtonFav);
        tbFav.setChecked(mTargetStatus.isFavorited());
        tbFav.setOnCheckedChangeListener((v, c) -> {
            if (c) {
                mListener.onStatusDetailAction(ACTION_FAVORITE, mTargetStatus);
            } else {
                mListener.onStatusDetailAction(ACTION_UNFAVORITE, mTargetStatus);
            }
            dismiss();
        });
        view.findViewById(R.id.StatusDetailButtonFavRetweet).setOnClickListener(v -> {
            mListener.onStatusDetailAction(ACTION_FAVORITE_AND_RETWEET, mTargetStatus);
            dismiss();
        });
        view.findViewById(R.id.StatusDetailButtonRetweet).setOnClickListener(v -> {
            mListener.onStatusDetailAction(ACTION_RETWEET, mTargetStatus);
            dismiss();
        });
        view.findViewById(R.id.StatusDetailButtonReply).setOnClickListener(v -> {
            mListener.onStatusDetailAction(ACTION_RETWEET, mTargetStatus);
            dismiss();
        });
        view.findViewById(R.id.StatusDetailButtonReplyBlank).setOnClickListener(v -> {
            mListener.onStatusDetailAction(ACTION_RETWEET, mTargetStatus);
            dismiss();
        });
        view.findViewById(R.id.StatusDetailButtonOthers).setOnClickListener(v -> {
            dismiss();
            //favorite();
        });
    }

    private void fetchStatusState() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }
        };
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

    public static final int ACTION_FAVORITE = 1;
    public static final int ACTION_UNFAVORITE = 2;
    public static final int ACTION_FAVORITE_AND_RETWEET = 3;
    public static final int ACTION_RETWEET = 4;
    public static final int ACTION_UNRETWEET = 5;
    public static final int ACTION_REPLY = 6;
    public static final int ACTION_REPLY_BLANK = 7;

    public interface StatusDetailInteractionListener {
        void onStatusDetailAction(int type, TencocoaStatus status);
    }

}
