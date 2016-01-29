package org.kb10uy.tencocoa;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.adapters.GeneralReverseListAdapter;
import org.kb10uy.tencocoa.model.TencocoaDatabaseHelper;
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaStatus;
import org.kb10uy.tencocoa.model.TencocoaStatusCache;
import org.kb10uy.tencocoa.model.TencocoaUriInfo;
import org.kb10uy.tencocoa.views.ImageViewerActivity;

import java.util.List;

import io.realm.Realm;
import twitter4j.Status;
import twitter4j.User;

public class TimelineFragment extends Fragment {

    private TimelineFragmentInteractionListener mListener;
    private ListView mListView;
    private Handler mHandler;
    private LayoutInflater mInflater;
    private GeneralReverseListAdapter<TencocoaStatus> mTimeLineAdapter;
    //private Pattern mViaPattern = Pattern.compile("<a href=\"(.+)\" rel=\"nofollow\">(.+)</a>");
    private TypedValue mRewteetBackgroundValue = new TypedValue();
    private Activity ctx;
    private List<TencocoaStatus> backingCache;

    private ImageView mPopupIcon, mPopupSource;
    private TextView mPopupCaption, mPopupDescription;
    private LinearLayout mPopup;
    private long currentUserId;
    private SharedPreferences pref;
    private Drawable mFavoriteIcon;
    private LinearLayout mUpdatePopup;
    private TextView mUpdatePopupText;
    private boolean mHasNewTweet = false;
    private int mRestNewTweet = 0, mLastTotal = 0;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ctx = getActivity();
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        mInflater = inflater;
        getFavoriteIcon();
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        mHandler = new Handler();
        mListView = (ListView) view.findViewById(R.id.TimelineDrawerListViewTimeLine);
        mTimeLineAdapter = new GeneralReverseListAdapter<>(getActivity(), R.layout.item_status, this::generateStatusView);
        if (backingCache != null) mTimeLineAdapter.setList(backingCache);
        mListView.setAdapter(mTimeLineAdapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> mListener.showStatusDetail(((TencocoaStatus) mTimeLineAdapter.getItem(position))));
        mPopupIcon = (ImageView) view.findViewById(R.id.TimelineImageViewPopupIcon);
        mPopupSource = (ImageView) view.findViewById(R.id.TimelineImageViewPopupSource);
        mPopupCaption = (TextView) view.findViewById(R.id.TimelineTextViewCaption);
        mPopupDescription = (TextView) view.findViewById(R.id.TimelineTextViewDescription);
        mPopup = (LinearLayout) view.findViewById(R.id.TimelinePopup);
        mUpdatePopupText = (TextView) view.findViewById(R.id.TimelineUpdatePopupText);
        mUpdatePopup = (LinearLayout) view.findViewById(R.id.TimelineUpdatePopup);

        view.getContext().getTheme().resolveAttribute(R.attr.colorRetweetBackground, mRewteetBackgroundValue, true);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < mRestNewTweet) {
                    rewriteNewTweets(--mRestNewTweet);
                    if (mRestNewTweet == 0) {
                        popdownNewTweets();
                        mHasNewTweet = false;
                    }
                }
            }
        });

        return view;
    }

    private void getFavoriteIcon() {
        int index = Integer.parseInt(pref.getString(getString(R.string.preference_appearance_like_mark), "20"));
        TypedArray icons = getResources().obtainTypedArray(R.array.favorite_icons);
        mFavoriteIcon = icons.getDrawable(index);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backingCache = mTimeLineAdapter.getList();
        //Log.i("Tencocoa", "Timeline's view was destroyed and saved statuses.");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TimelineFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TimelineFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void clearStatuses() {
        mTimeLineAdapter.clear();
    }

    public void setStreamingUser(long user) {
        currentUserId = user;
    }

    public void onStreamingStatus(Status status) {
        TencocoaStatus tstatus = new TencocoaStatus(status);
        Realm realm = Realm.getInstance(ctx);
        if (TencocoaDatabaseHelper.checkFavoritedStatus(realm, tstatus.getShowingStatus().getId())) {
            tstatus.favorite();
        }
        realm.close();
        int prevCount = mTimeLineAdapter.getCount();
        int y = mListView.getChildAt(0).getTop();
        int item = mListView.getFirstVisiblePosition();

        mHandler.post(() -> {
            mTimeLineAdapter.add(tstatus);
            if (item != 0 || y != 0) {
                if (!mHasNewTweet) {
                    mHasNewTweet = true;
                    popupNewTweets();
                    mRestNewTweet = 0;
                }
                rewriteNewTweets(++mRestNewTweet);
                mListView.setSelectionFromTop(item + 1, y);
            }
        });
    }

    public void onFavorite(User source, User target, Status status) {
        if (source.getId() == currentUserId) {
            for (TencocoaStatus ts : mTimeLineAdapter.getList()) {
                if (ts.getShowingStatus().getId() == status.getId()) {
                    updateFavoriteStatus(ts, true);
                }
            }
            mHandler.post(mTimeLineAdapter::notifyDataSetChanged);
        }
        if (target.getId() == currentUserId) {
            String caption = getString(R.string.popup_notification_favorited, source.getName());
            String description = status.getText();
            mHandler.post(() -> showNotificationPopup(R.drawable.tencocoa_star1, source, caption, description));
        }
    }

    public void onUnfavorite(User source, User target, Status status) {
        if (source.getId() == currentUserId) {
            for (TencocoaStatus ts : mTimeLineAdapter.getList()) {
                if (ts.getShowingStatus().getId() == status.getId()) {
                    updateFavoriteStatus(ts, false);
                }
            }
            mHandler.post(mTimeLineAdapter::notifyDataSetChanged);
        }
        if (target.getId() == currentUserId) {
            String caption = getString(R.string.popup_notification_unfavorited, source.getName());
            String description = status.getText();
            mHandler.post(() -> showNotificationPopup(R.drawable.tencocoa_star1, source, caption, description));
        }
    }

    protected View generateStatusView(View targetView, TencocoaStatus status) {
        Status sourceStatus = status.getShowingStatus();
        User user = sourceStatus.getUser();
        View itemLayout = targetView.findViewById(R.id.StatusItemLayout);
        View privateMark = targetView.findViewById(R.id.StatusItemUserPrivateMark);

        ((ImageView) targetView.findViewById(R.id.StatusItemFavoriteCountMark)).setImageDrawable(mFavoriteIcon);
        ((ImageView) targetView.findViewById(R.id.StatusItemFavorited)).setImageDrawable(mFavoriteIcon);
        ((TextView) targetView.findViewById(R.id.StatusItemUserName)).setText(user.getName());
        ((TextView) targetView.findViewById(R.id.StatusItemUserScreenName)).setText(user.getScreenName());
        ((TextView) targetView.findViewById(R.id.StatusItemStatusText)).setText(status.getReplacedText());
        ((TextView) targetView.findViewById(R.id.StatusItemCreatedAt)).setText(TencocoaHelper.getRelativeTimeString(sourceStatus.getCreatedAt()));
        //Matcher matcher = mViaPattern.matcher(sourceStatus.getSource());
        //if (matcher.find()) ((TextView) targetView.findViewById(R.id.StatusItemVia)).setText(matcher.group(2));
        Glide.with(getActivity()).load(user.getBiggerProfileImageURLHttps()).into(((ImageView) targetView.findViewById(R.id.StatusItemUserProfileImage)));
        if (status.isFavorited()) {
            targetView.findViewById(R.id.StatusItemFavorited).setVisibility(View.VISIBLE);
        } else {
            targetView.findViewById(R.id.StatusItemFavorited).setVisibility(View.GONE);
        }
        if (status.isRetweet()) {
            itemLayout.setBackgroundResource(mRewteetBackgroundValue.resourceId);
            (targetView.findViewById(R.id.StatusItemFavRtCounts)).setVisibility(View.VISIBLE);
            ((TextView) targetView.findViewById(R.id.StatusItemFavoriteCount)).setText(TencocoaHelper.getCompressedNumberString(sourceStatus.getFavoriteCount()));
            ((TextView) targetView.findViewById(R.id.StatusItemRetweetCount)).setText(TencocoaHelper.getCompressedNumberString(sourceStatus.getRetweetCount()));

            User retweeter = status.getRetweeter();
            (targetView.findViewById(R.id.StatusItemRetweeterFrame)).setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(retweeter.getMiniProfileImageURLHttps()).into(((ImageView) targetView.findViewById(R.id.StatusItemRetweetedUserProfile)));
            ((TextView) targetView.findViewById(R.id.StatusItemRetweetedUserName)).setText(retweeter.getName());
        } else {
            itemLayout.setBackgroundResource(R.color.tencocoa_color_transparent);
            (targetView.findViewById(R.id.StatusItemFavRtCounts)).setVisibility(View.GONE);
            (targetView.findViewById(R.id.StatusItemRetweeterFrame)).setVisibility(View.GONE);
        }

        if (status.hasMedia()) {
            LinearLayout mlist = (LinearLayout) targetView.findViewById(R.id.StatusItemMediaList);
            mlist.removeAllViews();
            mlist.setVisibility(View.VISIBLE);
            LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (TencocoaUriInfo info : status.getMedias()) {
                View av = inflater.inflate(R.layout.item_status_image, mlist, false);
                ImageView imv = (ImageView) av.findViewById(R.id.StatusItemImageItem);
                Glide.with(getActivity()).load(info.getThumbnailImageUri()).into(imv);
                final Uri tu = info.getFullImageUri();
                imv.setOnClickListener((v) -> startImageViewer(tu));
                mlist.addView(av);
            }
        } else {
            (targetView.findViewById(R.id.StatusItemMediaList)).setVisibility(View.GONE);
        }

        if (user.isProtected()) {
            privateMark.setVisibility(View.VISIBLE);
        } else {
            privateMark.setVisibility(View.GONE);
        }
        return targetView;
    }

    protected void startImageViewer(Uri uri) {
        Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
        intent.putExtra("Uri", uri.toString());
        startActivity(intent);
    }

    private void updateFavoriteStatus(TencocoaStatus status, boolean s) {
        Realm realm = Realm.getInstance(ctx);
        realm.executeTransaction(realm1 -> {
            TencocoaStatusCache statusCache = realm.where(TencocoaStatusCache.class).equalTo("statusId", status.getShowingStatus().getId()).findFirst();
            if (statusCache == null) {
                statusCache = realm.createObject(TencocoaStatusCache.class);
                statusCache.setStatusId(status.getShowingStatus().getId());
            }
            statusCache.setIsFavorited(s);
        });
        realm.close();
        if (s) {
            status.favorite();
        } else {
            status.unfavorite();
        }
    }

    protected void showNotificationPopup(int iconResource, User source, String caption, String description) {
        mPopupIcon.setImageResource(iconResource);
        Glide.with(getActivity()).load(source.getBiggerProfileImageURLHttps()).into(mPopupSource);
        mPopupCaption.setText(caption);
        mPopupDescription.setText(description);
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.notification_popup);
        set.setTarget(mPopup);
        set.start();
    }

    protected void popupNewTweets() {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.new_tweet_popup);
        set.setTarget(mUpdatePopup);
        set.start();
    }

    protected void popdownNewTweets() {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.new_tweet_popdown);
        set.setTarget(mUpdatePopup);
        set.start();
    }

    private void rewriteNewTweets(int count) {
        mUpdatePopupText.setText(getString(R.string.popup_update_count, count));
    }

    protected GeneralReverseListAdapter<TencocoaStatus> getTimelineAdapter() {
        return mTimeLineAdapter;
    }

    protected void sendToHandler(Runnable r) {
        mHandler.post(r);
    }

    public interface TimelineFragmentInteractionListener {
        //public void onFragmentInteraction(Uri uri);
        //public TencocoaStreamingService getStreamingService();
        void showStatusDetail(TencocoaStatus status);
    }

}
