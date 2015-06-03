package org.kb10uy.tencocoa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.kb10uy.tencocoa.model.TencocoaRequestCodes;
import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.model.TwitterAccountInformationReceiver;
import org.kb10uy.tencocoa.model.TwitterHelper;
import org.kb10uy.tencocoa.settings.FirstSettingActivity;
import org.kb10uy.tencocoa.views.AsyncImageView;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;


public class MainActivity
        extends AppCompatActivity
        implements MainDrawerFragment.OnFragmentInteractionListener,
        NewStatusDialogFragment.NewStatusDialogFragmentInteractionListener {

    Twitter mTwitter;
    User currentUser;
    boolean initialized = false;
    SharedPreferences pref;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Context ctx;

    TencocoaStreamingService mStreamingService;
    TencocoaWritePermissionService mWritePermissionService;
    ServiceConnection mStreamingConnection, mWritePermissionConnection;
    boolean mStreamingBound, mWritePermissionBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
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

        pref = getSharedPreferences(getString(R.string.preference_name), 0);
        ctx = this;
    }

    void initialize() {
        checkTwitterApiKeys();
        initializeTwitter();
        startServices();
        initialized = true;
    }

    private void startServices() {
        Intent tss = new Intent(this, TencocoaStreamingService.class);
        Intent twps = new Intent(this, TencocoaWritePermissionService.class);
        startService(tss);
        startService(twps);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("initialized", initialized);
        outState.putSerializable("user", currentUser);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        initialized = savedInstanceState.getBoolean("initialized");
        currentUser = (User) savedInstanceState.getSerializable("user");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        startUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStreamingBound) {
            unbindService(mStreamingConnection);
            mStreamingBound = false;
        }
        if (mWritePermissionBound) {
            unbindService(mWritePermissionConnection);
            mWritePermissionBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mStreamingBound)
            bindService(new Intent(this, TencocoaStreamingService.class), mStreamingConnection, BIND_AUTO_CREATE);
        if (!mWritePermissionBound)
            bindService(new Intent(this, TencocoaWritePermissionService.class), mWritePermissionConnection, BIND_AUTO_CREATE);
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
            Intent intent = new Intent(this, AccountsListActivity.class);
            startActivityForResult(intent, TencocoaRequestCodes.AccountSelect);
        }
    }

    private void startUser() {
        mStreamingConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mStreamingService = ((TencocoaStreamingService.TencocoaStreamingServiceBinder) service).getService();
                mStreamingBound = true;
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
                mWritePermissionBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mWritePermissionService = null;
                mWritePermissionBound = false;
            }
        };
        checkTwitterUserExists();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TencocoaRequestCodes.AccountSelect:
                if (resultCode == RESULT_OK) {
                    TwitterAccountInformation info = (TwitterAccountInformation) data.getSerializableExtra("Information");
                    refreshUserInformation(info);
                    mStreamingService.setStreamingUser(info);
                    mWritePermissionService.setTargetUser(info);
                    mStreamingService.startCurrentUserStream();
                }
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        if (!initialized) initialize();
        if (currentUser != null) updateUserInformations(currentUser);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onExit();
    }

    private void onExit() {
        stopService(new Intent(this, TencocoaStreamingService.class));
        stopService(new Intent(this, TencocoaWritePermissionService.class));
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
                updateUserInformations(user);
            }
        };
        task.execute(info);
    }

    private void updateUserInformations(User user) {
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void applyUpdateStatus(String status) {
        mWritePermissionService.updateStatus(status);
    }
}
