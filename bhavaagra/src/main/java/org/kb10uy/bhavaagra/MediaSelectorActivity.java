package org.kb10uy.bhavaagra;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MediaSelectorActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String CURRENT_INTENT = "CurrentIntent";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private Handler mHandler;
    private Intent mIntent;
    private Context mContext;
    private ContentResolver mResolver;
    private Cursor mMediaCursor;
    private int mResultCode = 0;
    private int mMinCount = 0, mMaxCount = 1;
    private int mMinWidth = -1, mMaxWidth = -1;
    private int mMinHeight = -1, mMaxHeight = -1;
    private List<Uri> mPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_selector);
        mHandler = new Handler();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mIntent = getIntent();
        applyIntent();
    }

    private void applyIntent() {
        mResultCode = mIntent.getIntExtra(RhapsodyBuilder.RESULT_CODE, -1);
        mMinCount = mIntent.getIntExtra(RhapsodyBuilder.MIN_COUNT, -1);
        mMaxCount = mIntent.getIntExtra(RhapsodyBuilder.MAX_COUNT, -1);
        mMinWidth = mIntent.getIntExtra(RhapsodyBuilder.MIN_WIDTH, -1);
        mMinWidth = mIntent.getIntExtra(RhapsodyBuilder.MIN_HEIGHT, -1);
        mMaxWidth = mIntent.getIntExtra(RhapsodyBuilder.MAX_WIDTH, -1);
        mMaxHeight = mIntent.getIntExtra(RhapsodyBuilder.MAX_HEIGHT, -1);
        mPrevious = RhapsodyBuilder.toUriList(mIntent.getStringArrayExtra(RhapsodyBuilder.PREVIOUS_LIST));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        resolveMedia();
    }

    private void resolveMedia() {
        mResolver = getContentResolver();
        mMediaCursor = mResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_media_selector, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MediaSelectorActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
