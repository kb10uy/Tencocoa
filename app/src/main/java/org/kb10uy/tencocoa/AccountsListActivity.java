package org.kb10uy.tencocoa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.kb10uy.tencocoa.model.TwitterAccountInformation;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class AccountsListActivity extends AppCompatActivity {

    String mCallback;
    Twitter mTwitter;
    RequestToken mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTwitter = TwitterFactory.getSingleton();
        mCallback = getString(R.string.uri_twitter_oauth_callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accounts_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_accounts_list_new_account:
                newOAuthAuthorize();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void newOAuthAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallback);
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {

                }
            }
        };
        task.execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || intent.getData() == null || !intent.getData().toString().startsWith(mCallback))
            return;
        String verifier = intent.getData().getQueryParameter("oauth_verifier");
        final Activity ta = this;

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    Toast.makeText(ta, R.string.text_activity_accounts_list_success, Toast.LENGTH_SHORT).show();
                    registerAuthorization(accessToken);
                } else {
                    Toast.makeText(ta, R.string.text_activity_accounts_list_failed, Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute(verifier);
    }

    private void registerAuthorization(AccessToken accessToken) {
        TwitterAccountInformation info = new TwitterAccountInformation(accessToken);
    }
}
