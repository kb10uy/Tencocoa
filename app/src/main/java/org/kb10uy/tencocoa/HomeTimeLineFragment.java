package org.kb10uy.tencocoa;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.adapters.GeneralListAdapterViewGenerator;
import org.kb10uy.tencocoa.adapters.GeneralReverseListAdapter;
import org.kb10uy.tencocoa.model.HomeTimeLineLister;
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaStatus;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public HomeTimeLineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private View generateStatusView(View targetView, TencocoaStatus status) {
        Status sourceStatus = status.getSourceStatus();
        User user = sourceStatus.getUser();
        ((TextView) targetView.findViewById(R.id.StatusItemUserName)).setText(user.getName());
        ((TextView) targetView.findViewById(R.id.StatusItemUserScreenName)).setText(user.getScreenName());
        ((TextView) targetView.findViewById(R.id.StatusItemFavoriteCount)).setText(TencocoaHelper.getCompressedNumberString(sourceStatus.getFavoriteCount()));
        ((TextView) targetView.findViewById(R.id.StatusItemRetweetCount)).setText(TencocoaHelper.getCompressedNumberString(sourceStatus.getRetweetCount()));
        ((TextView) targetView.findViewById(R.id.StatusItemStatusText)).setText(sourceStatus.getText());
        Glide.with(getActivity()).load(user.getOriginalProfileImageURLHttps()).into(((ImageView) targetView.findViewById(R.id.StatusItemUserProfileImage)));
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
    }

}
