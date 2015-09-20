package org.kb10uy.tencocoa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.model.DoubleTapHelper;
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaRequestCodes;
import org.kb10uy.tencocoa.model.TencocoaStatus;
import org.kb10uy.tencocoa.model.TencocoaStatusCache;
import org.kb10uy.tencocoa.model.TencocoaUserStreamLister;
import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.model.TwitterAccountInformationReceiver;
import org.kb10uy.tencocoa.settings.FirstSettingActivity;
import org.kb10uy.tencocoa.settings.SettingsActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import io.realm.Realm;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


public class MainActivity
        extends AppCompatActivity
        implements MainDrawerFragment.OnDrawerFragmentInteractionListener,
        NewStatusDialogFragment.NewStatusDialogFragmentInteractionListener,
        HomeTimeLineFragment.HomeTimeLineFragmentInteractionListener,
        StatusDetailDialogFragment.StatusDetailInteractionListener,
        TwitterAccountInformationReceiver {

    private Twitter mTwitter;
    private User currentUser;
    private boolean initialized = false;
    private SharedPreferences pref;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mCurrentNetworkInfo;

    private DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private HomeTimeLineFragment mHomeTimeLineFragment;
    private NotificationsFragment mNotificationsFragment;
    private UserInformationFragment mUserInformationFragment;
    private DirectMessageFragment mDirectMessageFragment;
    private SearchFragment mSearchFragment;
    private ActionBar mActionBar;
    private Context ctx;

    private TencocoaStreamingService mStreamingService;
    private TencocoaWritePermissionService mWritePermissionService;
    private TencocoaReadPermissionService mReadPermissionService;
    private ServiceConnection mStreamingConnection, mWritePermissionConnection, mReadPermissionConnection;
    private TencocoaUserStreamLister mUserStreamListener;
    private CountDownLatch mServiceLatch;
    private DoubleTapHelper mBackDoubleTapHelper;
    private boolean mStreamingBound, mWritePermissionBound, mReadPermissionBound;
    private boolean mIsRestoring, mAutoConnectTried,
            mHasShownFirstAccountActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        checkTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mFrameLayout = (FrameLayout) findViewById(R.id.MainActivityFragmentFrame);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.general_open,
                R.string.general_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.ic_launcher);
        //getSupportActionBar().setIcon(R.drawable.ic_launcher);

        ctx = this;
        mBackDoubleTapHelper = new DoubleTapHelper(ctx, getString(R.string.notification_double_tap_to_exit), 1000, 500);

        startTencocoaServices();
        createServiceConnections();
        initializeFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        bindTencocoaServices();
        initializeTwitter();
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        initialized = savedInstanceState.getBoolean("initialized");
        currentUser = (User) savedInstanceState.getSerializable("user");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        if (!initialized) onInitializeTencocoa();
        if (currentUser != null) updateUserInformation(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTwitterUserExists();
        mCurrentNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        //bindTencocoaServices();
    }

//running

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("initialized", initialized);
        outState.putSerializable("user", currentUser);
        mIsRestoring = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unbindTencocoaServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindTencocoaServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (!mIsRestoring) */
        onExitTencocoa();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        final TwitterAccountInformation info = (TwitterAccountInformation) data.getSerializableExtra("Information");
        bindTencocoaServices();
        switch (requestCode) {
            case TencocoaRequestCodes.AccountSelect:
                if (resultCode == RESULT_OK) {
                    startUserStream(info);
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_main_new_status:
                NewStatusDialogFragment dialog = NewStatusDialogFragment.newInstance();
                dialog.show(getFragmentManager(), "NewStatus");
                break;
            case R.id.action_main_accounts:
                Intent intent = new Intent(this, AccountsListActivity.class);
                startActivityForResult(intent, TencocoaRequestCodes.AccountSelect);
                return true;
            case R.id.action_main_settings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                boolean fin = mBackDoubleTapHelper.onTap();
                if (fin) {
                    finish();
                }
                return fin;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkTheme() {
        String type = pref.getString(getString(R.string.preference_appearance_theme), "Black");
        TencocoaHelper.setCurrentTheme(this, type);
    }

    private void initializeFragments() {
        if (mHomeTimeLineFragment == null) mHomeTimeLineFragment = new HomeTimeLineFragment();
        if (mNotificationsFragment == null)
            mNotificationsFragment = NotificationsFragment.newInstance();
        if (mUserInformationFragment == null)
            mUserInformationFragment = UserInformationFragment.newInstance();
        if (mDirectMessageFragment == null)
            mDirectMessageFragment = DirectMessageFragment.newInstance();
        if (mSearchFragment == null) mSearchFragment = SearchFragment.newInstance();
        mUserStreamListener = new TencocoaUserStreamLister(mHomeTimeLineFragment, this);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.MainActivityFragmentFrame, mHomeTimeLineFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void startTencocoaServices() {
        Intent tss = new Intent(ctx, TencocoaStreamingService.class);
        Intent twps = new Intent(ctx, TencocoaWritePermissionService.class);
        Intent trps = new Intent(ctx, TencocoaReadPermissionService.class);
        startService(tss);
        startService(twps);
        startService(trps);
        Log.d(getString(R.string.app_name), "Services started now");
    }

    private void stopTencocoaServices() {
        stopService(new Intent(ctx, TencocoaStreamingService.class));
        stopService(new Intent(ctx, TencocoaWritePermissionService.class));
        stopService(new Intent(ctx, TencocoaReadPermissionService.class));
        Log.d(getString(R.string.app_name), "Services stopped now");
    }

    private void bindTencocoaServices() {
        if (mServiceLatch == null) mServiceLatch = new CountDownLatch(3);
        if (!mStreamingBound)
            mStreamingBound = ctx.bindService(new Intent(this, TencocoaStreamingService.class), mStreamingConnection, 0);
        if (!mWritePermissionBound)
            mWritePermissionBound = ctx.bindService(new Intent(this, TencocoaWritePermissionService.class), mWritePermissionConnection, 0);
        if (!mReadPermissionBound)
            mReadPermissionBound = ctx.bindService(new Intent(this, TencocoaReadPermissionService.class), mReadPermissionConnection, 0);
        Log.d(getString(R.string.app_name), "Services are now bound");
    }

    private void unbindTencocoaServices() {
        if (mStreamingBound) {
            ctx.unbindService(mStreamingConnection);
            mStreamingBound = false;
            mStreamingService = null;
        }
        if (mWritePermissionBound) {
            ctx.unbindService(mWritePermissionConnection);
            mWritePermissionBound = false;
            mWritePermissionService = null;
        }
        if (mReadPermissionBound) {
            ctx.unbindService(mReadPermissionConnection);
            mReadPermissionBound = false;
            mReadPermissionService = null;
        }
        Log.d(getString(R.string.app_name), "Services are now unbound");
        mServiceLatch = null;
        createServiceConnections();
    }

    void onInitializeTencocoa() {
        checkTwitterApiKeys();
        initialized = true;
    }

    private void onExitTencocoa() {
        stopTencocoaServices();
    }


    private void initializeTwitter() {
        checkTwitterApiKeys();
    }

    private void checkTwitterApiKeys() {
        if (pref.getBoolean(getString(R.string.preference_twitter_consumer_set), false)) return;
        startActivity(new Intent(this, FirstSettingActivity.class));
        finish();
    }

    private void checkTwitterUserExists() {
        if (pref.getInt(getString(R.string.preference_twitter_accounts_count), 0) == 0) {
            Intent intent = new Intent(this, AccountsListActivity.class);
            startActivityForResult(intent, TencocoaRequestCodes.AccountSelect);
            mHasShownFirstAccountActivity = true;
        }
    }

    private void startUserStream(TwitterAccountInformation info) {
        if (mCurrentNetworkInfo == null || !(mCurrentNetworkInfo.isConnected())) {
            showToast(getString(R.string.notification_network_unavailable));
            return;
        }
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mServiceLatch.await();
                    mStreamingService.setTargetUser(info);
                    mWritePermissionService.setTarget(mStreamingService.getTargetUserTwitterInstance(), mStreamingService.getTargetUserInformation());
                    mReadPermissionService.setTarget(mStreamingService.getTargetUserTwitterInstance(), mStreamingService.getTargetUserInformation());
                    mStreamingService.startCurrentUserStream(mUserStreamListener);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                refreshUserInformation(info);
            }
        };
        task.execute();
    }

    private void autoConnectUserStream() {
        if (mStreamingService.isUserStreamRunning()) return;
        if (pref.getInt(getString(R.string.preference_twitter_accounts_count), 0) > pref.getInt(getString(R.string.preference_twitter_accounts_auto_number), 0)) {
            TwitterAccountInformation i = null;
            try {
                FileInputStream acfile = openFileInput(getString(R.string.accounts_file_name));
                ArrayList<TwitterAccountInformation> accounts = TencocoaHelper.deserializeObjectFromFile(acfile);
                if (accounts != null) {
                    i = accounts.get(pref.getInt(getString(R.string.preference_twitter_accounts_auto_number), 0));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            startUserStream(i);
        }
    }

    private void createServiceConnections() {
        mStreamingConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mStreamingService = ((TencocoaStreamingService.TencocoaStreamingServiceBinder) service).getService();
                mServiceLatch.countDown();
                onStreamingServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mStreamingService = null;
                mStreamingBound = false;
            }
        };
        mWritePermissionConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mWritePermissionService = ((TencocoaWritePermissionService.TencocoaWritePermissionServiceBinder) service).getService();
                mServiceLatch.countDown();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mWritePermissionService = null;
                mWritePermissionBound = false;
            }
        };
        mReadPermissionConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mReadPermissionService = ((TencocoaReadPermissionService.TencocoaReadPermissionServiceBinder) service).getService();
                mServiceLatch.countDown();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mReadPermissionService = null;
                mReadPermissionBound = false;
            }
        };
    }

    private void onStreamingServiceConnected() {
        //mHomeTimeLineFragment.start(mStreamingService);
        if (!mAutoConnectTried) {
            mAutoConnectTried = true;
            if (pref.getBoolean(getString(R.string.preference_login_auto_login_enabled), true)) {
                autoConnectUserStream();
            }
        }
        /*
        if (!mStreamingService.isUserStreamRunning())
            mStreamingService.startCurrentUserStream(mUserStreamListener);
        */
    }

    private void refreshUserInformation(final TwitterAccountInformation info) {
        AsyncTask<TwitterAccountInformation, Void, User> task = new AsyncTask<TwitterAccountInformation, Void, User>() {
            @Override
            protected User doInBackground(TwitterAccountInformation... params) {
                if (mReadPermissionService == null) return null;
                return mReadPermissionService.getTargetUser();
            }

            @Override
            protected void onPostExecute(User user) {
                if (user == null) return;
                currentUser = user;
                updateUserInformation(user);
            }
        };
        task.execute(info);
    }

    private void updateUserInformation(User user) {
        ImageView profile = (ImageView) mDrawerLayout.findViewById(R.id.MainDrawerImageViewUserProfileImage);
        ImageView header = (ImageView) mDrawerLayout.findViewById(R.id.MainDrawerImageViewUserHeaderImage);
        TextView userName = (TextView) mDrawerLayout.findViewById(R.id.MainDrawerTextViewUserName);
        TextView screenName = (TextView) mDrawerLayout.findViewById(R.id.MainDrawerTextViewScreenName);
        TextView statuses = (TextView) mDrawerLayout.findViewById(R.id.MainDrawerTextViewStatuses);
        TextView favorites = (TextView) mDrawerLayout.findViewById(R.id.MainDrawerTextViewFavorites);
        TextView friends = (TextView) mDrawerLayout.findViewById(R.id.MainDrawerTextViewFriends);
        TextView followers = (TextView) mDrawerLayout.findViewById(R.id.MainDrawerTextViewFollowers);

        Glide.with(ctx).load(user.getBiggerProfileImageURLHttps()).into(profile);
        Glide.with(ctx).load(user.getProfileBannerMobileURL()).into(header);

        userName.setText(user.getName());
        screenName.setText(user.getScreenName());
        statuses.setText(Integer.toString(user.getStatusesCount()));
        favorites.setText(Integer.toString(user.getFavouritesCount()));
        friends.setText(Integer.toString(user.getFriendsCount()));
        followers.setText(Integer.toString(user.getFollowersCount()));
        getSupportActionBar().setTitle(String.format("%s(@%s)", user.getName(), user.getScreenName()));
    }

    @Override
    public void onDrawerFragmentMainMenuInteraction(int action) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (action) {
            case 0:
                transaction.replace(R.id.MainActivityFragmentFrame, mHomeTimeLineFragment);
                break;
            case 1:
                transaction.replace(R.id.MainActivityFragmentFrame, mNotificationsFragment);
                break;
            case 2:
                transaction.replace(R.id.MainActivityFragmentFrame, mUserInformationFragment);
                break;
            case 3:
                transaction.replace(R.id.MainActivityFragmentFrame, mSearchFragment);
                break;
            case 4:
                transaction.replace(R.id.MainActivityFragmentFrame, mDirectMessageFragment);
                break;
        }
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    @Override
    public void onDrawerFragmentMiscInteraction(String action) {
        switch (action) {
            case "AccountSelect":
                Intent intent = new Intent(this, AccountsListActivity.class);
                startActivityForResult(intent, TencocoaRequestCodes.AccountSelect);
                break;
        }
    }

    @Override
    public void applyUpdateStatus(String status) {
        mWritePermissionService.updateStatus(status);
    }

    @Override
    public void applyUpdateStatus(StatusUpdate status) {
        mWritePermissionService.updateStatus(status);
    }

    @Override
    public void showStatusDetail(TencocoaStatus status) {
        StatusDetailDialogFragment dialog = StatusDetailDialogFragment.newInstance(status);
        dialog.show(getFragmentManager(), "NewStatus");
    }

    @Override
    public void onStatusDetailAction(int type, TencocoaStatus status) {
        final long sourceId = status.getSourceStatus().getId();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Realm realm = Realm.getInstance(ctx);
                TencocoaStatusCache statusCache = realm.where(TencocoaStatusCache.class).equalTo("statusId", sourceId).findFirst();
                realm.beginTransaction();
                if (statusCache == null) {
                    statusCache = realm.createObject(TencocoaStatusCache.class);
                    statusCache.setStatusId(status.getShowingStatus().getId());
                }

                switch (type) {
                    case StatusDetailDialogFragment.ACTION_FAVORITE:
                        mWritePermissionService.favoriteStatus(sourceId);
                        statusCache.setIsFavorited(true);
                        break;
                    case StatusDetailDialogFragment.ACTION_UNFAVORITE:
                        mWritePermissionService.unfavoriteStatus(sourceId);
                        statusCache.setIsFavorited(false);
                        break;
                    case StatusDetailDialogFragment.ACTION_RETWEET:
                        mWritePermissionService.retweetStatus(sourceId);
                        statusCache.setIsRetweeted(true);
                        break;
                    case StatusDetailDialogFragment.ACTION_UNRETWEET:
                        statusCache.setIsRetweeted(false);
                        break;
                    case StatusDetailDialogFragment.ACTION_FAVORITE_AND_RETWEET:
                        mWritePermissionService.favoriteStatus(sourceId);
                        mWritePermissionService.retweetStatus(sourceId);
                        statusCache.setIsRetweeted(true);
                        statusCache.setIsFavorited(true);
                        break;
                    case StatusDetailDialogFragment.ACTION_REPLY:
                        NewStatusDialogFragment dialog = NewStatusDialogFragment.newInstance(status);
                        dialog.show(getFragmentManager(), "NewStatus");
                        break;
                    case StatusDetailDialogFragment.ACTION_REPLY_BLANK:
                        StatusUpdate update = new StatusUpdate(TencocoaHelper.createReplyTemplate(status));
                        update.setInReplyToStatusId(status.getShowingStatus().getId());
                        mWritePermissionService.updateStatus(update);
                        break;
                }
                realm.commitTransaction();
                if (realm != null) realm.close();
                return null;
            }
        };
        task.execute();
    }

    private void showBehaviorNotification(String title, String description) {

    }

    private void showToast(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTwitterAccountInformationReceived(User info) {
        new Handler(Looper.getMainLooper()).post(() -> {
            currentUser = info;
            updateUserInformation(info);
        });
    }

    @Override
    public long getTargetAccountId() {
        return currentUser.getId();
    }
}
