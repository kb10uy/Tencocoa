package org.kb10uy.tencocoa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.kb10uy.tencocoa.settings.FirstSettingActivity;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;


public class MainActivity extends AppCompatActivity implements MainDrawerFragment.OnFragmentInteractionListener {

    Twitter mTwitter;
    boolean consumerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkTwitterApiKeys();
        initializeTwitter();
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
        mTwitter = TwitterFactory.getSingleton();
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_name), 0);
        String ck = pref.getString(getString(R.string.preference_twitter_consumer_key), "");
        String cs = pref.getString(getString(R.string.preference_twitter_consumer_secret), "");
        mTwitter.setOAuthConsumer(ck, cs);
    }

    private void checkTwitterApiKeys() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_name), 0);
        if (pref.getBoolean(getString(R.string.preference_twitter_consumer_set), false)) return;

        startActivity(new Intent(this, FirstSettingActivity.class));
        finish();
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
                startActivity(new Intent(this, AccountsListActivity.class));
                return true;
            case R.id.action_main_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
