package org.kb10uy.tencocoa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.kb10uy.tencocoa.adapters.GeneralListAdapter;
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.model.TwitterHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class AccountsListActivity extends AppCompatActivity {

    String mCallback;
    Twitter mTwitter;
    RequestToken mRequestToken;

    ListView mListView;
    ArrayList<TwitterAccountInformation> accounts;
    GeneralListAdapter<TwitterAccountInformation> accountsAdapter;
    Intent resultIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        accountsAdapter = new GeneralListAdapter<>(
                this,
                R.layout.item_accounts_list,
                (targetView, item) -> {
                    ((TextView) targetView.findViewById(R.id.AccountsListListViewItemScreenName)).setText(item.getScreenName());
                    ((TextView) targetView.findViewById(R.id.AccountsListListViewItemUserId)).setText(Long.toString(item.getUserId()));
                    return targetView;
                });
        mListView = (ListView) findViewById(R.id.AccountsListListView);
        mListView.setAdapter(accountsAdapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> onAccountSelected(position));

        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_name), 0);
        String ck = pref.getString(getString(R.string.preference_twitter_consumer_key), "");
        String cs = pref.getString(getString(R.string.preference_twitter_consumer_secret), "");
        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(ck, cs);
        mCallback = getString(R.string.uri_twitter_oauth_callback);
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().toString().startsWith(mCallback)) {
            onNewIntent(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("accounts", accounts);
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
                } catch (IllegalStateException e) {
                    return null;
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
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        accounts = (ArrayList<TwitterAccountInformation>) savedInstanceState.getSerializable("accounts");
        accountsAdapter.setList(accounts);
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
                }
            }
        };
        task.execute();

    }

    private void initialize() {
        loadAccounts();
        if (accounts == null) {
            accounts = new ArrayList<>();
            saveAccounts();
        }
        accountsAdapter.setList(accounts);
        accountsAdapter.notifyDataSetChanged();
    }


    private void registerAuthorization(AccessToken accessToken) {
        final TwitterAccountInformation info = new TwitterAccountInformation(accessToken);
        Handler h = new Handler();
        h.post(() -> {
            accounts.add(info);
            accountsAdapter.notifyDataSetChanged();
            saveAccounts();
        });
    }

    private void saveAccounts() {
        try {
            FileOutputStream acfile = openFileOutput("TwitterAccounts.dat", MODE_PRIVATE);
            TencocoaHelper.serializeObjectToFile(accounts, acfile);

            SharedPreferences pref = getSharedPreferences(getString(R.string.preference_name), 0);
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt(getString(R.string.preference_twitter_accounts_count), accounts.size());
            edit.apply();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadAccounts() {
        try {
            FileInputStream acfile = openFileInput("TwitterAccounts.dat");
            accounts = TencocoaHelper.deserializeObjectFromFile(acfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onAccountSelected(int position) {
        resultIntent.putExtra("Information", accounts.get(position));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
