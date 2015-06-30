package org.kb10uy.tencocoa;

import android.app.Activity;
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
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import twitter4j.Status;
import twitter4j.User;

public class HomeTimeLineFragment extends Fragment implements HomeTimeLineLister {

    private HomeTimeLineFragmentInteractionListener mListener;
    private ListView mListView;
    private Handler mHandler;
    private GeneralReverseListAdapter<TencocoaStatus> mTimeLineAdapter;
    //private TencocoaStreamingService mStreamingService;
    private ArrayList<TencocoaStatus> statuses = new ArrayList<>();
    //private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
    //private Pattern mViaPattern = Pattern.compile("<a href=\"(.+)\" rel=\"nofollow\">(.+)</a>");
    private TypedValue mRewteetBackgroundValue = new TypedValue();

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

        return view;
    }

    private void initializeAdapter() {
        mTimeLineAdapter = new GeneralReverseListAdapter<>(getActivity(), R.layout.item_status, this::generateStatusView);
        mTimeLineAdapter.setList(statuses);
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

        ((TextView) targetView.findViewById(R.id.StatusItemUserName)).setText(user.getName());
        ((TextView) targetView.findViewById(R.id.StatusItemUserScreenName)).setText(user.getScreenName());
        ((TextView) targetView.findViewById(R.id.StatusItemStatusText)).setText(sourceStatus.getText());
        ((TextView) targetView.findViewById(R.id.StatusItemCreatedAt)).setText(TencocoaHelper.getRelativeTimeString(sourceStatus.getCreatedAt()));
        //Matcher matcher = mViaPattern.matcher(sourceStatus.getSource());
        //if (matcher.find()) ((TextView) targetView.findViewById(R.id.StatusItemVia)).setText(matcher.group(2));
        Glide.with(getActivity()).load(user.getOriginalProfileImageURLHttps()).into(((ImageView) targetView.findViewById(R.id.StatusItemUserProfileImage)));
        if (status.isRetweet()) {
            (targetView.findViewById(R.id.StatusItemLayout)).setBackgroundResource(mRewteetBackgroundValue.resourceId);
            (targetView.findViewById(R.id.StatusItemFavRtCounts)).setVisibility(View.VISIBLE);
            ((TextView) targetView.findViewById(R.id.StatusItemFavoriteCount)).setText(TencocoaHelper.getCompressedNumberString(sourceStatus.getFavoriteCount()));
            ((TextView) targetView.findViewById(R.id.StatusItemRetweetCount)).setText(TencocoaHelper.getCompressedNumberString(sourceStatus.getRetweetCount()));

            User retweeter = status.getRetweeter();
            (targetView.findViewById(R.id.StatusItemRetweeterFrame)).setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(retweeter.getOriginalProfileImageURLHttps()).into(((ImageView) targetView.findViewById(R.id.StatusItemRetweetedUserProfile)));
            ((TextView) targetView.findViewById(R.id.StatusItemRetweetedUserName)).setText(retweeter.getName());
        } else {
            (targetView.findViewById(R.id.StatusItemLayout)).setBackgroundResource(R.color.tencocoa_transparent);
            (targetView.findViewById(R.id.StatusItemFavRtCounts)).setVisibility(View.GONE);
            (targetView.findViewById(R.id.StatusItemRetweeterFrame)).setVisibility(View.GONE);
        }
        return targetView;
    }


    @Override
    public void onHomeTimeLineStatus(Status status) {
        statuses.add(new TencocoaStatus(status));
        mHandler.post(mTimeLineAdapter::notifyDataSetChanged);
    }

    public interface HomeTimeLineFragmentInteractionListener {
        //public void onFragmentInteraction(Uri uri);
        //public TencocoaStreamingService getStreamingService();
        void showStatusDetail(TencocoaStatus status);
    }

}
