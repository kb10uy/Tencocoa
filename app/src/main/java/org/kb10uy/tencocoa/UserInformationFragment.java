package org.kb10uy.tencocoa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
    private ImageView mImageViewHeader, mImageViewProfile;
    private TextView mTextViewName, mTextViewScreenName, mTextViewDescription;
    private TextView mTextViewStatuses, mTextViewFavorites, mTextViewFriends, mTextViewFollowers;
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
        mImageViewHeader = (ImageView) view.findViewById(R.id.UserInformationImageViewHeader);
        mImageViewProfile = (ImageView) view.findViewById(R.id.UserInformationImageViewProfile);
        mTextViewName = (TextView) view.findViewById(R.id.UserInformationTextViewName);
        mTextViewScreenName = (TextView) view.findViewById(R.id.UserInformationTextViewScreenName);
        mTextViewDescription=(TextView) view.findViewById(R.id.UserInformationTextViewDescription);
        mTextViewStatuses=(TextView) view.findViewById(R.id.UserInformationTextViewStatuses);
        mTextViewFavorites=(TextView) view.findViewById(R.id.UserInformationTextViewFavorites);
        mTextViewFriends=(TextView) view.findViewById(R.id.UserInformationTextViewFriends);
        mTextViewFollowers=(TextView) view.findViewById(R.id.UserInformationTextViewFollowers);
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
        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        //mListener = null;
    }
    /*
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
   */
}
