package org.kb10uy.tencocoa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.adapters.GeneralReverseListAdapter;
import org.kb10uy.tencocoa.model.HomeTimeLineLister;
import org.kb10uy.tencocoa.model.TencocoaDatabaseHelper;
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaStatus;
import org.kb10uy.tencocoa.model.TencocoaStatusCache;

import java.util.ArrayList;

import io.realm.Realm;
import twitter4j.Status;
import twitter4j.User;

public class HomeTimeLineFragment extends Fragment implements HomeTimeLineLister {

    private HomeTimeLineFragmentInteractionListener mListener;
    private ListView mListView;
    private Handler mHandler;
    private GeneralReverseListAdapter<TencocoaStatus> mTimeLineAdapter;
    //private TencocoaStreamingService mStreamingService;
    private ArrayList<TencocoaStatus> statuses = new ArrayList<>();
    //private Pattern mViaPattern = Pattern.compile("<a href=\"(.+)\" rel=\"nofollow\">(.+)</a>");
    private TypedValue mRewteetBackgroundValue = new TypedValue();
    private Context ctx;

    public HomeTimeLineFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home_time_line, container, false);
        mHandler = new Handler();
        mListView = (ListView) view.findViewById(R.id.HomeTimeLineDrawerListViewTimeLine);
        initializeAdapter();
        mListView.setAdapter(mTimeLineAdapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> mListener.showStatusDetail(((TencocoaStatus) mTimeLineAdapter.getItem(position))));
        view.getContext().getTheme().resolveAttribute(R.attr.colorRetweetBackground, mRewteetBackgroundValue, true);
        ctx = getActivity();
        return view;
    }

    private void initializeAdapter() {
        mTimeLineAdapter = new GeneralReverseListAdapter<>(getActivity(), R.layout.item_status, this::generateStatusView);
        mTimeLineAdapter.setList(statuses);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HomeTimeLineFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement HomeTimeLineFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private View generateStatusView(View targetView, TencocoaStatus status) {
        Status sourceStatus = status.getShowingStatus();
        User user = sourceStatus.getUser();
        View itemLayout = targetView.findViewById(R.id.StatusItemLayout);
        View privateMark = targetView.findViewById(R.id.StatusItemUserPrivateMark);

        ((TextView) targetView.findViewById(R.id.StatusItemUserName)).setText(user.getName());
        ((TextView) targetView.findViewById(R.id.StatusItemUserScreenName)).setText(user.getScreenName());
        ((TextView) targetView.findViewById(R.id.StatusItemStatusText)).setText(sourceStatus.getText());
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

        if(user.isProtected()) {
            privateMark.setVisibility(View.VISIBLE);
        } else {
            privateMark.setVisibility(View.GONE);
        }
        return targetView;
    }


    @Override
    public void onHomeTimeLineStatus(Status status) {
        TencocoaStatus tstatus = new TencocoaStatus(status);
        Realm realm = Realm.getInstance(ctx);
        if (TencocoaDatabaseHelper.checkFavoritedStatus(realm, tstatus.getShowingStatus().getId())) {
            tstatus.favorite();
        }
        realm.close();
        synchronized(statuses) {
            statuses.add(tstatus);
            mHandler.post(mTimeLineAdapter::notifyDataSetChanged);
        }
    }

    @Override
    public void onFavorite(Status status) {
        for (TencocoaStatus ts : statuses) {
            if (ts.getShowingStatus().getId() == status.getId()) {
                updateFavoriteStatus(ts, true);
            }
        }
        mHandler.post(mTimeLineAdapter::notifyDataSetChanged);
    }

    @Override
    public void onUnfavorite(Status status) {
        for (TencocoaStatus ts : statuses) {
            if (ts.getShowingStatus().getId() == status.getId()) {
                updateFavoriteStatus(ts, false);
            }
        }
        mHandler.post(mTimeLineAdapter::notifyDataSetChanged);
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

    public interface HomeTimeLineFragmentInteractionListener {
        //public void onFragmentInteraction(Uri uri);
        //public TencocoaStreamingService getStreamingService();
        void showStatusDetail(TencocoaStatus status);
    }

}
