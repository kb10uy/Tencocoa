package org.kb10uy.tencocoa;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.adapters.GeneralListAdapterViewGenerator;
import org.kb10uy.tencocoa.adapters.GeneralReverseListAdapter;
import org.kb10uy.tencocoa.model.HomeTimeLineLister;
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaStatus;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;


public class HomeTimeLineFragment extends Fragment implements HomeTimeLineLister {

    private HomeTimeLineFragmentInteractionListener mListener;
    private ListView mListView;
    private Handler mHandler;
    private GeneralReverseListAdapter<TencocoaStatus> mTimeLineAdapter;
    private TencocoaStreamingService mStreamingService;
    private ArrayList<TencocoaStatus> statuses = new ArrayList<>();
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Pattern mViaPattern = Pattern.compile("<a href=\"(.+)\" rel=\"nofollow\">(.+)</a>");
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.showStatusDetail(((TencocoaStatus) mTimeLineAdapter.getItem(position)).getShowingStatus());
            }
        });
        view.getContext().getTheme().resolveAttribute(R.attr.colorRetweetBackground, mRewteetBackgroundValue, true);

        return view;
    }

    private void initializeAdapter() {
        mTimeLineAdapter = new GeneralReverseListAdapter<>(getActivity(), R.layout.item_status, new GeneralListAdapterViewGenerator<TencocoaStatus>() {
            @Override
            public View generateView(View targetView, TencocoaStatus item) {
                return generateStatusView(targetView, item);
            }
        });
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
        Matcher matcher = mViaPattern.matcher(sourceStatus.getSource());
        //if (matcher.find()) ((TextView) targetView.findViewById(R.id.StatusItemVia)).setText(matcher.group(2));
        Glide.with(getActivity()).load(user.getOriginalProfileImageURLHttps()).into(((ImageView) targetView.findViewById(R.id.StatusItemUserProfileImage)));
        if (status.isRetweet()) {
            ((LinearLayout) targetView.findViewById(R.id.StatusItemLayout)).setBackgroundResource(mRewteetBackgroundValue.resourceId);
            ((LinearLayout) targetView.findViewById(R.id.StatusItemFavRtCounts)).setVisibility(View.VISIBLE);
            ((TextView) targetView.findViewById(R.id.StatusItemFavoriteCount)).setText(TencocoaHelper.getCompressedNumberString(sourceStatus.getFavoriteCount()));
            ((TextView) targetView.findViewById(R.id.StatusItemRetweetCount)).setText(TencocoaHelper.getCompressedNumberString(sourceStatus.getRetweetCount()));
        } else {
            ((LinearLayout) targetView.findViewById(R.id.StatusItemLayout)).setBackgroundResource(R.color.tencocoa_transparent);
            ((LinearLayout) targetView.findViewById(R.id.StatusItemFavRtCounts)).setVisibility(View.GONE);
        }
        return targetView;
    }


    @Override
    public void onHomeTimeLineStatus(Status status) {
        statuses.add(new TencocoaStatus(status));
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTimeLineAdapter.notifyDataSetChanged();
            }
        });
    }

    public interface HomeTimeLineFragmentInteractionListener {
        // TODO: Update argument type and name
        //public void onFragmentInteraction(Uri uri);
        //public TencocoaStreamingService getStreamingService();
        public void showStatusDetail(Status status);
    }

}
