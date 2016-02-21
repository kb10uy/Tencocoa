package org.kb10uy.tencocoa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.base.Strings;
import com.twitter.Extractor;

import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.views.TencocoaServiceProvider;

import java.util.List;

import twitter4j.Status;
import twitter4j.User;


public class UserInformationFragment extends Fragment {

    private ViewPager mViewPager;
    private UserInformationFragmentPagerAdapter mAdapter;
    private User currentUser;
    private TencocoaServiceProvider mProvider;


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
        View view = inflater.inflate(R.layout.fragment_user_information, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.UserInformationViewPager);
        mAdapter = UserInformationFragmentPagerAdapter.newInstance(
                getChildFragmentManager(),
                mProvider,
                getString(R.string.label_activity_main_summary),
                getString(R.string.label_activity_main_tweets));
        mViewPager.setAdapter(mAdapter);
        if (currentUser != null) mAdapter.updateInformation(currentUser);

        return view;
    }

    public void updateInformation(User user) {
        currentUser = user;
        if (mAdapter != null) mAdapter.updateInformation(user);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mProvider = (TencocoaServiceProvider) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TencocoaServiceProvider");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mProvider = null;
    }

    public static final class UserInformationFragmentPagerAdapter extends FragmentPagerAdapter {

        private String mSummary, mStatuses;
        private UserInformationSummaryFragment mSummaryFragment;
        private UserInformationStatusesFragment mStatusesFragment;

        public UserInformationFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public static UserInformationFragmentPagerAdapter newInstance(FragmentManager fm, TencocoaServiceProvider mProvider, String titleSummary, String titleStatuses) {
            UserInformationFragmentPagerAdapter uifspa = new UserInformationFragmentPagerAdapter(fm);
            uifspa.mStatuses = titleStatuses;
            uifspa.mSummary = titleSummary;
            uifspa.mSummaryFragment = new UserInformationSummaryFragment();
            uifspa.mStatusesFragment = new UserInformationStatusesFragment();
            //uifspa.mStatusesFragment.setProvider(mProvider);
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
        private TextView mTextViewLink, mTextViewLocation;
        private LinearLayout mLayoutLink, mLayoutLocation;
        private User currentUser;
        private TencocoaServiceProvider mProvider;

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
            mTextViewLink = (TextView) view.findViewById(R.id.UserInformationTextViewLink);
            mTextViewLocation = (TextView) view.findViewById(R.id.UserInformationTextViewLocation);
            mLayoutLink = (LinearLayout) view.findViewById(R.id.UserInformationLink);
            mLayoutLocation = (LinearLayout) view.findViewById(R.id.UserInformationLocation);
            updateInformation(currentUser);
            return view;
        }

        public void updateInformation(User user) {
            currentUser = user;
            if (mContext == null || currentUser == null) return;
            if (!Strings.isNullOrEmpty(user.getProfileBannerURL())) Glide.with(mContext).load(user.getProfileBannerURL()).into(mImageViewHeader);
            if (!Strings.isNullOrEmpty(user.getBiggerProfileImageURLHttps())) Glide.with(mContext).load(user.getBiggerProfileImageURLHttps()).into(mImageViewProfile);
            mTextViewName.setText(user.getName());
            mTextViewScreenName.setText(user.getScreenName());
            if (user.getStatusesCount() != -1) mTextViewStatuses.setText(Integer.toString(user.getStatusesCount()));
            if (user.getFavouritesCount() != -1) mTextViewFavorites.setText(Integer.toString(user.getFavouritesCount()));
            if (user.getFriendsCount() != -1) mTextViewFriends.setText(Integer.toString(user.getFriendsCount()));
            if (user.getFollowersCount() != -1) mTextViewFollowers.setText(Integer.toString(user.getFollowersCount()));
            mLayoutLocation.setVisibility(Strings.isNullOrEmpty(user.getLocation()) ? View.GONE : View.VISIBLE);
            mTextViewLocation.setText(user.getLocation());
            updateComplexText();
        }

        private void updateComplexText() {
            String desc = currentUser.getDescription();
            String uri = currentUser.getURL();
            Extractor extractor = new Extractor();

            List<Extractor.Entity> de = extractor.extractEntitiesWithIndices(desc);
            SpannableString sdesc = new SpannableString(Strings.isNullOrEmpty(desc) ? "" : desc);
            for (Extractor.Entity e : de) {
                ClickableSpan span = null;
                switch (e.getType()) {
                    case URL:
                        span = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(e.getValue())));
                            }
                        };
                        break;
                    case HASHTAG:
                        span = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                mProvider.getReadPermissionService().requestStatusSearch("#" + e.getValue());
                            }
                        };
                        break;
                    case CASHTAG:
                        span = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                mProvider.getReadPermissionService().requestStatusSearch("$" + e.getValue());
                            }
                        };
                        break;
                    case MENTION:
                        span = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                mProvider.getReadPermissionService().requestUserInformation(e.getValue());
                            }
                        };
                        break;
                }
                sdesc.setSpan(span, e.getStart(), e.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mTextViewDescription.setText(sdesc);
            mTextViewDescription.setMovementMethod(LinkMovementMethod.getInstance());

            mLayoutLink.setVisibility(Strings.isNullOrEmpty(uri) ? View.GONE : View.VISIBLE);
            mTextViewLink.setText(uri);
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