package org.kb10uy.tencocoa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.model.TencocoaRequestCodes;
import org.kb10uy.tencocoa.model.TencocoaUserStreamLister;
import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.model.TwitterHelper;
import org.kb10uy.tencocoa.settings.FirstSettingActivity;

import java.util.concurrent.CountDownLatch;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;


public class MainActivity
        extends AppCompatActivity
        implements MainDrawerFragment.OnFragmentInteractionListener,
        NewStatusDialogFragment.NewStatusDialogFragmentInteractionListener,
        HomeTimeLineFragment.HomeTimeLineFragmentInteractionListener {

    Twitter mTwitter;
    User currentUser;
    boolean initialized = false;
    SharedPreferences pref;

    DrawerLayout mDrawerLayout;
    FrameLayout mFrameLayout;
    ActionBarDrawerToggle mDrawerToggle;
    HomeTimeLineFragment mHomeTimeLineFragment;
    Context ctx;

    TencocoaStreamingService mStreamingService;
    TencocoaWritePermissionService mWritePermissionService;
    ServiceConnection mStreamingConnection, mWritePermissionConnection;
    TencocoaUserStreamLister mUserStreamListener;
    CountDownLatch mServiceLatch;
    boolean mStreamingBound, mWritePermissionBound;
    boolean mIsRestoring, mIsUserStreamEstablished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        mHomeTimeLineFragment = new HomeTimeLineFragment();
        mUserStreamListener = new TencocoaUserStreamLister(mHomeTimeLineFragment);
        pref = getSharedPreferences(getString(R.string.preference_name), 0);
        ctx = getApplicationContext();
        startTencocoaServices();
        initializeTwitter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startUser();
        initializeFragments();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        initialized = savedInstanceState.getBoolean("initialized");
        currentUser = (User) savedInstanceState.getSerializable("user");
        mIsUserStreamEstablished = savedInstanceState.getBoolean("mIsUserStreamEstablished");
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
        bindTencocoaServices();
    }

    //running

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("initialized", initialized);
        outState.putSerializable("user", currentUser);
        outState.putBoolean("mIsUserStreamEstablished", mIsUserStreamEstablished);
        mIsRestoring = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindTencocoaServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mIsRestoring) onExitTencocoa();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Intent td = data;
        bindTencocoaServices();
        switch (requestCode) {
            case TencocoaRequestCodes.AccountSelect:
                if (resultCode == RESULT_OK) {
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                mServiceLatch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            TwitterAccountInformation info = (TwitterAccountInformation) td.getSerializableExtra("Information");
                            refreshUserInformation(info);
                            mStreamingService.setStreamingUser(info);
                            mWritePermissionService.setTargetUser(info);
                            mStreamingService.addUserStreamListener(mUserStreamListener);
                            mStreamingService.startCurrentUserStream();
                            mIsUserStreamEstablished = true;
                        }
                    };
                    task.execute();
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
                return true;
        }

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    private void initializeFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.MainActivityFragmentFrame, mHomeTimeLineFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void startTencocoaServices() {
        Intent tss = new Intent(ctx, TencocoaStreamingService.class);
        Intent twps = new Intent(ctx, TencocoaWritePermissionService.class);
        startService(tss);
        startService(twps);
        Log.d(getString(R.string.app_name), "Services started now");
    }

    private void stopTencocoaServices() {
        stopService(new Intent(ctx, TencocoaStreamingService.class));
        stopService(new Intent(ctx, TencocoaWritePermissionService.class));
        Log.d(getString(R.string.app_name), "Services stopped now");
    }

    private void bindTencocoaServices() {
        if (mServiceLatch == null) mServiceLatch = new CountDownLatch(2);
        if (!mStreamingBound)
            mStreamingBound = ctx.bindService(new Intent(this, TencocoaStreamingService.class), mStreamingConnection, 0);
        if (!mWritePermissionBound)
            mWritePermissionBound = ctx.bindService(new Intent(this, TencocoaWritePermissionService.class), mWritePermissionConnection, 0);
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
        mIsUserStreamEstablished = false;
    }


    private void initializeTwitter() {
        String ck = pref.getString(getString(R.string.preference_twitter_consumer_key), "");
        String cs = pref.getString(getString(R.string.preference_twitter_consumer_secret), "");
        mTwitter = TwitterHelper.getTwitterInstance(ck, cs);
    }

    private void checkTwitterApiKeys() {
        if (pref.getBoolean(getString(R.string.preference_twitter_consumer_set), false)) return;
        startActivity(new Intent(this, FirstSettingActivity.class));
    }

    private void checkTwitterUserExists() {
        if (pref.getInt(getString(R.string.preference_twitter_accounts_count), 0) == 0) {
            bindTencocoaServices();
            Intent intent = new Intent(this, AccountsListActivity.class);
            startActivityForResult(intent, TencocoaRequestCodes.AccountSelect);
        }
    }

    private void startUser() {
        createServiceConnections();
        checkTwitterUserExists();
    }

    private void createServiceConnections() {
        mStreamingConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mStreamingService = ((TencocoaStreamingService.TencocoaStreamingServiceBinder) service).getService();
                //mStreamingBound = true;
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
                //mWritePermissionBound = true;
                mServiceLatch.countDown();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mWritePermissionService = null;
                mWritePermissionBound = false;
            }
        };
    }

    private void onStreamingServiceConnected() {
        //mHomeTimeLineFragment.start(mStreamingService);
        if (mIsUserStreamEstablished)
            mStreamingService.getCurrentUserStreamListener().changeHomeTimeLineLister(mHomeTimeLineFragment);
    }

    private void refreshUserInformation(final TwitterAccountInformation info) {
        AsyncTask<TwitterAccountInformation, Void, User> task = new AsyncTask<TwitterAccountInformation, Void, User>() {
            @Override
            protected User doInBackground(TwitterAccountInformation... params) {
                try {
                    mTwitter.setOAuthAccessToken(new AccessToken(info.getAccessToken(), info.getAccessTokenSecret()));
                    return mTwitter.users().showUser(params[0].getUserId());
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                } finally {

                }
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
    }

    @Override
    public void onDrawerFragmentInteraction(int action) {
        switch (action) {
            case 0:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.show(mHomeTimeLineFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                break;
        }
    }

    @Override
    public void applyUpdateStatus(String status) {
        mWritePermissionService.updateStatus(status);
    }
}
