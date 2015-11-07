package org.kb10uy.bhavaagra;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MediaSelectorActivity extends AppCompatActivity
        implements AlbumDrawerFragment.NavigationDrawerCallbacks {

    private static final String CURRENT_INTENT = "CurrentIntent";
    public static final String[] MEDIA_BUCKETS = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.DATE_MODIFIED,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT,
    };

    private AlbumDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private Handler mHandler;
    private Intent mIntent;
    private ContentResolver mResolver;
    private Cursor mMediaCursor;
    private List<RhapsodyAlbum> mAlbums;
    private Rhapsody mCurrentRhapsody;
    private ActionBarDrawerToggle mDrawerToggle;

    private GridView mMediaGridView;
    private DrawerLayout mDrawerLayout;
    private android.support.v7.app.ActionBar mActionBar;

    private GeneralListAdapter<RhapsodyAlbum> mAlbumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_selector);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mHandler = new Handler();
        initializeAdapters();
        mNavigationDrawerFragment = (AlbumDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout, mDrawerToggle, mAlbumAdapter);
        mTitle = getTitle();
        mMediaGridView = (GridView) findViewById(R.id.MediaSelectorGridView);
        mIntent = getIntent();
        applyIntent();
    }

    private void applyIntent() {
        mCurrentRhapsody = (Rhapsody) mIntent.getSerializableExtra(RhapsodyBuilder.INTENT_RHAPSODY);
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
        if (!mMediaCursor.moveToFirst()) return;
        mAlbums = new ArrayList<>();
        mAlbumAdapter.setList(mAlbums);
        MEDIA_LOOP:
        do {
            long id = mMediaCursor.getLong(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
            int width = mMediaCursor.getInt(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH));
            int height = mMediaCursor.getInt(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT));
            if (mCurrentRhapsody.getMinWidth() != Rhapsody.UNLIMITED && mCurrentRhapsody.getMinWidth() > width) continue;
            if (mCurrentRhapsody.getMinHeight() != Rhapsody.UNLIMITED && mCurrentRhapsody.getMinHeight() > height) continue;
            if (mCurrentRhapsody.getMaxWidth() != Rhapsody.UNLIMITED && mCurrentRhapsody.getMaxWidth() < width) continue;
            if (mCurrentRhapsody.getMaxHeight() != Rhapsody.UNLIMITED && mCurrentRhapsody.getMaxHeight() < height) continue;
            long bucketId = mMediaCursor.getLong(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));

            for (RhapsodyAlbum album : mAlbums) {
                if (album.getAlbumBucketId() != bucketId) continue;
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                album.add(new RhapsodyImage(uri, RhapsodyImage.SOURCE_STORAGE));
                continue MEDIA_LOOP;
            }
            String bucketName = mMediaCursor.getString(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
            RhapsodyAlbum newAlbum = new RhapsodyAlbum(bucketName, bucketId);
            Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            newAlbum.add(new RhapsodyImage(uri, RhapsodyImage.SOURCE_STORAGE));
            mAlbums.add(newAlbum);
        } while (mMediaCursor.moveToNext());
        mAlbumAdapter.notifyDataSetChanged();
    }

    private void initializeAdapters() {
        final Context ctx = this;
        mAlbumAdapter = new GeneralListAdapter<>(this, R.layout.item_album, new GeneralListAdapterViewGenerator<RhapsodyAlbum>() {
            @Override
            public View generateView(View targetView, RhapsodyAlbum item) {
                ((TextView) targetView.findViewById(R.id.ItemAlbumName)).setText(item.getAlbumName());
                ((TextView) targetView.findViewById(R.id.ItemAlbumMediaCount)).setText(String.format("%d", item.getMediaCount()));
                Glide.with(ctx).load(item.get(0).getImageUri()).into((ImageView) targetView.findViewById(R.id.ItemAlbumThumbnail));
                return targetView;
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

    }

    public void onSectionAttached(int number) {
        switch (number) {

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
}
