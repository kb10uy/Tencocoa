package org.kb10uy.tencocoa.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.kb10uy.tencocoa.R;

public class FirstSettingActivity extends AppCompatActivity {
    EditText ck, cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting);
        ck = (EditText) findViewById(R.id.FirstSettingEditTextConsumerKey);
        cs = (EditText) findViewById(R.id.FirstSettingEditTextConsumerSecret);
        final Context ctx = this;

        findViewById(R.id.FirstSettingButtonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ck.getText().toString().equals("") || cs.getText().toString().equals("")) {
                    Toast.makeText(ctx, R.string.toast_activity_first_setting_consumer_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                setCustomKeys();
                finish();
            }
        });

        findViewById(R.id.FirstSettingButtonUseDefault).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment df = new DefaultKeysConfirmDialogFragment();
                df.show(getFragmentManager(), "Confirm");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_setting, menu);
        return true;
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

    public void setCustomKeys() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString(getString(R.string.preference_twitter_consumer_key), ck.getText().toString());
        edit.putString(getString(R.string.preference_twitter_consumer_secret), cs.getText().toString());
        edit.putBoolean(getString(R.string.preference_twitter_consumer_is_default), false);
        edit.putBoolean(getString(R.string.preference_twitter_consumer_set), true);
        edit.commit();
    }

    //Checking for default keys
    public static class DefaultKeysConfirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.title_dialog_default_keys)
                    .setPositiveButton(R.string.label_dialog_default_keys_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setDefaultKeys();
                            getActivity().finish();
                        }

                        public void setDefaultKeys() {
                            SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.preference_name), 0);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString(getString(R.string.preference_twitter_consumer_key), getString(R.string.preference_twitter_default_consumer_key));
                            edit.putString(getString(R.string.preference_twitter_consumer_secret), getString(R.string.preference_twitter_default_consumer_secret));
                            edit.putBoolean(getString(R.string.preference_twitter_consumer_is_default), true);
                            edit.putBoolean(getString(R.string.preference_twitter_consumer_set), true);
                            edit.commit();
                        }
                    })
                    .setNegativeButton(R.string.label_dialog_default_keys_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }
    }
}
