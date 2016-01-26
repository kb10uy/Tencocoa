package org.kb10uy.bhavaagra;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MediaSelectorActivity extends AppCompatActivity
        implements AlbumDrawerFragment.NavigationDrawerCallbacks {

    private static final int CAMERA_INTENT = 0x55A;

    private static final String CURRENT_INTENT = "CurrentIntent";
    public static final String[] MEDIA_BUCKETS = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.DATE_MODIFIED,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT,
    };
    private static final int PERMISSION_REQUEST_READ_STORAGE = 0x4550;
    private static final int PERMISSION_REQUEST_CAMERA = 0x4551;


    private AlbumDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private Handler mHandler;
    private Intent mIntent;
    private ContentResolver mResolver;
    private Cursor mMediaCursor;
    private List<RhapsodyAlbum> mAlbums;
    private RhapsodyAlbum mCurrentAlbum;
    private Rhapsody mCurrentRhapsody;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Uri> mSelectedMedias;

    private GridView mMediaGridView;
    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;

    private GeneralListAdapter<RhapsodyAlbum> mAlbumAdapter;
    private GeneralReverseListAdapter<RhapsodyImage> mMediaAdapter;
    private String mAbsolutePath;

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
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout, mDrawerToggle, mAlbumAdapter);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mTitle = getTitle();
        mMediaGridView = (GridView) findViewById(R.id.MediaSelectorGridView);
        mMediaGridView.setAdapter(mMediaAdapter);
        mIntent = getIntent();
        applyIntent();
    }

    private void applyIntent() {
        mCurrentRhapsody = (Rhapsody) mIntent.getSerializableExtra(RhapsodyBuilder.INTENT_RHAPSODY);
        mSelectedMedias = mIntent.getParcelableArrayListExtra(RhapsodyBuilder.INTENT_RHAPSODY_RESUME);
        if (mSelectedMedias == null) mSelectedMedias = new ArrayList<>();
        refreshSelectionState();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
                return;
            }
        }
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
        RhapsodyAlbum allAlbum = new RhapsodyAlbum(getString(R.string.drawer_all_media), -1);
        mAlbums.add(allAlbum);
        MEDIA_LOOP:
        do {
            long id = mMediaCursor.getLong(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
            int width = mMediaCursor.getInt(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH));
            int height = mMediaCursor.getInt(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT));
            if (mCurrentRhapsody.getMinWidth() != Rhapsody.UNLIMITED && mCurrentRhapsody.getMinWidth() > width) continue;
            if (mCurrentRhapsody.getMinHeight() != Rhapsody.UNLIMITED && mCurrentRhapsody.getMinHeight() > height) continue;
            if (mCurrentRhapsody.getMaxWidth() != Rhapsody.UNLIMITED && mCurrentRhapsody.getMaxWidth() < width) continue;
            if (mCurrentRhapsody.getMaxHeight() != Rhapsody.UNLIMITED && mCurrentRhapsody.getMaxHeight() < height) continue;
            Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            String dname = mMediaCursor.getString(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
            allAlbum.add(new RhapsodyImage(uri, dname, RhapsodyImage.SOURCE_STORAGE));

            long bucketId = mMediaCursor.getLong(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
            for (RhapsodyAlbum album : mAlbums) {
                if (album.getAlbumBucketId() != bucketId) continue;

                album.add(new RhapsodyImage(uri, dname, RhapsodyImage.SOURCE_STORAGE));
                continue MEDIA_LOOP;
            }
            String bucketName = mMediaCursor.getString(mMediaCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
            RhapsodyAlbum newAlbum = new RhapsodyAlbum(bucketName, bucketId);
            newAlbum.add(new RhapsodyImage(uri, dname, RhapsodyImage.SOURCE_STORAGE));
            mAlbums.add(newAlbum);
        } while (mMediaCursor.moveToNext());
        mAlbumAdapter.setList(mAlbums);
        mAlbumAdapter.notifyDataSetChanged();
        initializeMediaGrid();
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

        mMediaAdapter = new GeneralReverseListAdapter<>(this, R.layout.item_media, new GeneralListAdapterViewGenerator<RhapsodyImage>() {
            @Override
            public View generateView(View targetView, RhapsodyImage item) {
                targetView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(v);
                    }
                });
                targetView.setTag(item);
                CheckBox chk = (CheckBox) targetView.findViewById(R.id.ItemMediaSelected);
                chk.setChecked(false);
                for (Uri u : mSelectedMedias) {
                    if (u.equals(item.getImageUri())) {
                        chk.setChecked(true);
                        break;
                    }
                }
                ImageView imageView = (ImageView) targetView.findViewById(R.id.ItemMediaImage);
                Glide.with(ctx).load(item.getImageUri()).into(imageView);
                ((TextView) targetView.findViewById(R.id.ItemMediaFileName)).setText(item.getDisplayName());
                return targetView;
            }
        });
    }

    private void onClickImage(View v) {
        CheckBox chk = (CheckBox) v.findViewById(R.id.ItemMediaSelected);
        RhapsodyImage img = (RhapsodyImage) v.getTag();
        if (!chk.isChecked()) {
            chk.setChecked(true);
            mSelectedMedias.add(img.getImageUri());
        } else {
            chk.setChecked(false);
            mSelectedMedias.remove(img.getImageUri());
        }
        refreshSelectionState();
    }

    private void refreshSelectionState() {

    }

    private void initializeMediaGrid() {
        if (mAlbums.size() == 0) return;
        mNavigationDrawerFragment.selectItem(0);
    }

    @Override
    public void onAlbumDrawerItemSelected(int position) {
        mCurrentAlbum = mAlbums.get(position);
        mMediaAdapter.setList(mCurrentAlbum.getList());
        mMediaAdapter.notifyDataSetChanged();
        setTitle(mCurrentAlbum.getAlbumName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.media_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //なぜAndroid library projectでswitchが使えないのか
        int i = item.getItemId();
        if (i == R.id.action_camera) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                return false;
            }
            startCamera();
            return true;
        } else if (i == R.id.action_uri) {

            return true;
        } else if (i == R.id.action_cancel) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else if (i == R.id.action_ok) {
            Intent result = new Intent();
            result.putParcelableArrayListExtra(RhapsodyBuilder.INTENT_RHAPSODY, (ArrayList<Uri>) mSelectedMedias);
            setResult(RESULT_OK, result);
            finish();
            return true;
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, buildDateTimeImageUri());
        startActivityForResult(intent, CAMERA_INTENT);

    }

    private Uri buildDateTimeImageUri() {
        // http://blog.starrow.net/2012/03/android-intent.html
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dft = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        String mCameraFileName = dft.format(now) + ".jpg";
        String fullPath = Environment.getExternalStorageDirectory().toString();
        fullPath += mCurrentRhapsody.getCameraImagePath();
        mAbsolutePath = fullPath;
        return Uri.fromFile(new File(fullPath, mCameraFileName));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // http://shinbashi.hatenablog.com/entry/2013/06/23/141128
        //ファッキン エクスペリア
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_INTENT:
                if (resultCode == RESULT_OK) {
                    MediaScannerConnection.scanFile(this, new String[]{mAbsolutePath}, new String[]{"image/jpeg"}, null);
                    Toast.makeText(this, R.string.camera_succeed, Toast.LENGTH_SHORT).show();
                    mAlbums.clear();
                    mAlbumAdapter.notifyDataSetChanged();
                    resolveMedia();
                } else {
                    Toast.makeText(this, R.string.camera_fail, Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    resolveMedia();
                } else {

                    // @// TODO
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    // @// TODO: permission denied
                }
            }

        }
    }
}
