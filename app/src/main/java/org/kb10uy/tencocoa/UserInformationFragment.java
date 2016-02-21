package org.kb10uy.tencocoa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import twitter4j.User;


public class UserInformationFragment extends Fragment {

    private Context mContext;
    private SharedPreferences mPreference;
    private ViewPager mViewPager;
    private UserInformationFragmentPagerAdapter mAdapter;
    private User currentUser;


    //private OnFragmentInteractionListener mListener;
    public static UserInformationFragment newInstance() {
        return new UserInformationFragment();
    }

    public UserInformationFragment() {
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
        mContext = getActivity();
        mPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
        View view = inflater.inflate(R.layout.fragment_user_information, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.UserInformationViewPager);
        mAdapter = UserInformationFragmentPagerAdapter.newInstance(
                getChildFragmentManager(),
                getString(R.string.label_activity_main_summary),
                getString(R.string.label_activity_main_tweets));
        mViewPager.setAdapter(mAdapter);
        if (currentUser!=null) mAdapter.updateInformation(currentUser);

        return view;
    }

    public void updateInformation(User user) {
        currentUser = user;
        if (mAdapter != null) mAdapter.updateInformation(user);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public static final class UserInformationFragmentPagerAdapter extends FragmentPagerAdapter {

        private String mSummary, mStatuses;
        private UserInformationSummaryFragment mSummaryFragment;
        private UserInformationStatusesFragment mStatusesFragment;

        public UserInformationFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public static UserInformationFragmentPagerAdapter newInstance(FragmentManager fm, String titleSummary, String titleStatuses) {
            UserInformationFragmentPagerAdapter uifspa = new UserInformationFragmentPagerAdapter(fm);
            uifspa.mStatuses = titleStatuses;
            uifspa.mSummary = titleSummary;
            uifspa.mSummaryFragment = new UserInformationSummaryFragment();
            uifspa.mStatusesFragment = new UserInformationStatusesFragment();
            return uifspa;
        }



        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mSummaryFragment;
                case 1:
                    return mStatusesFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mSummary;
                case 1:
                    return mStatuses;
            }
            return super.getPageTitle(position);
        }

        public void updateInformation(User user) {
            mSummaryFragment.updateInformation(user);
        }
    }

    public static final class UserInformationSummaryFragment extends Fragment {
        private Context mContext;
        private SharedPreferences mPreference;
        private ImageView mImageViewHeader, mImageViewProfile;
        private TextView mTextViewName, mTextViewScreenName, mTextViewDescription;
        private TextView mTextViewStatuses, mTextViewFavorites, mTextViewFriends, mTextViewFollowers;
        private User currentUser;

        public UserInformationSummaryFragment() {
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
            mContext = getActivity();
            mPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
            View view = inflater.inflate(R.layout.fragment_user_information_summary, container, false);
            mImageViewHeader = (ImageView) view.findViewById(R.id.UserInformationImageViewHeader);
            mImageViewProfile = (ImageView) view.findViewById(R.id.UserInformationImageViewProfile);
            mTextViewName = (TextView) view.findViewById(R.id.UserInformationTextViewName);
            mTextViewScreenName = (TextView) view.findViewById(R.id.UserInformationTextViewScreenName);
            mTextViewDescription = (TextView) view.findViewById(R.id.UserInformationTextViewDescription);
            mTextViewStatuses = (TextView) view.findViewById(R.id.UserInformationTextViewStatuses);
            mTextViewFavorites = (TextView) view.findViewById(R.id.UserInformationTextViewFavorites);
            mTextViewFriends = (TextView) view.findViewById(R.id.UserInformationTextViewFriends);
            mTextViewFollowers = (TextView) view.findViewById(R.id.UserInformationTextViewFollowers);
            updateInformation(currentUser);
            return view;
        }

        public void updateInformation(User user) {
            currentUser = user;
            if (mContext == null || currentUser == null) return;
            Glide.with(mContext).load(user.getProfileBannerURL()).into(mImageViewHeader);
            Glide.with(mContext).load(user.getBiggerProfileImageURLHttps()).into(mImageViewProfile);
            mTextViewName.setText(user.getName());
            mTextViewScreenName.setText(user.getScreenName());
            mTextViewDescription.setText(user.getDescription());
            mTextViewStatuses.setText(Integer.toString(user.getStatusesCount()));
            mTextViewFavorites.setText(Integer.toString(user.getFavouritesCount()));
            mTextViewFriends.setText(Integer.toString(user.getFriendsCount()));
            mTextViewFollowers.setText(Integer.toString(user.getFollowersCount()));
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mContext = null;
            //mListener = null;
        }
    }

    public static final class UserInformationStatusesFragment extends TimelineFragment {

    }
}