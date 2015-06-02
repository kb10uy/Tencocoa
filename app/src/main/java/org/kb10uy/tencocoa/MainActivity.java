package org.kb10uy.tencocoa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
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
        implements MainDrawerFragment.OnFragmentInteractionListener {

    Twitter mTwitter;
    User currentUser;
    boolean initialized = false;
    SharedPreferences pref;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Context ctx;

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
        ctx=this;

        if (!initialized) initialize();
    }

    void initialize() {
        checkTwitterApiKeys();
        checkTwitterUserExists();
        initializeTwitter();
        initialized = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("initialized", initialized);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        initialized = savedInstanceState.getBoolean("initialized");
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
    protected void onResume() {
        super.onResume();
    }

    private void initializeTwitter() {
        String ck = pref.getString(getString(R.string.preference_twitter_consumer_key), "");
        String cs = pref.getString(getString(R.string.preference_twitter_consumer_secret), "");
        mTwitter = TwitterHelper.getTwitterInstance(ck, cs);
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
        }
    }

    private void startUser() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
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
                }
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        };
        task.execute(info);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
