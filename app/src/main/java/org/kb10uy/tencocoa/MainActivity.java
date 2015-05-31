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


public class MainActivity extends AppCompatActivity implements MainDrawerFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        checkTwitterApiKeys();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUser();
    }

    private void checkTwitterApiKeys() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_name), 0);
        if (pref.getBoolean(getString(R.string.preference_twitter_consumer_set), false)) return;

        startActivity(new Intent(this, FirstSettingActivity.class));
        //finish();
    }

    private void startUser() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
