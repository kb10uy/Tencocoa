package org.kb10uy.tencocoa.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.kb10uy.tencocoa.MainActivity;
import org.kb10uy.tencocoa.R;
import org.kb10uy.tencocoa.model.TencocoaHelper;

public class FirstSettingActivity extends AppCompatActivity {
    EditText ck, cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        TencocoaHelper.setCurrentTheme(this, pref.getString(getString(R.string.preference_appearance_theme), "Black"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting);
        ck = (EditText) findViewById(R.id.FirstSettingEditTextConsumerKey);
        cs = (EditText) findViewById(R.id.FirstSettingEditTextConsumerSecret);
        final Context ctx = this;

        findViewById(R.id.FirstSettingButtonRegister).setOnClickListener(v -> {
            if (ck.getText().toString().equals("") || cs.getText().toString().equals("")) {
                Toast.makeText(ctx, R.string.toast_activity_first_setting_consumer_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            setCustomKeys();
            Intent main = new Intent(ctx, MainActivity.class);
            main.putExtra("action", "ckcs_set");
            startActivity(main);
            finish();
        });

        findViewById(R.id.FirstSettingButtonUseDefault).setOnClickListener(v -> {
            DialogFragment df = new DefaultKeysConfirmDialogFragment();
            df.show(getFragmentManager(), "Confirm");
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
        //int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    public void setCustomKeys() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString(getString(R.string.preference_twitter_consumer_key), ck.getText().toString());
        edit.putString(getString(R.string.preference_twitter_consumer_secret), cs.getText().toString());
        edit.putBoolean(getString(R.string.preference_twitter_consumer_is_default), false);
        edit.putBoolean(getString(R.string.preference_twitter_consumer_set), true);
        edit.apply();
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
                            Intent main = new Intent(getActivity(), MainActivity.class);
                            main.putExtra("action", "ckcs_set");
                            main.putExtras(new Bundle());
                            startActivity(main);
                            getActivity().finish();
                        }

                        public void setDefaultKeys() {
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString(getString(R.string.preference_twitter_consumer_key), getString(R.string.preference_twitter_default_consumer_key));
                            edit.putString(getString(R.string.preference_twitter_consumer_secret), getString(R.string.preference_twitter_default_consumer_secret));
                            edit.putBoolean(getString(R.string.preference_twitter_consumer_is_default), true);
                            edit.putBoolean(getString(R.string.preference_twitter_consumer_set), true);
                            edit.apply();
                        }
                    })
                    .setNegativeButton(R.string.label_dialog_default_keys_cancel, (dialog, which) -> {

                    });
            return builder.create();
        }
    }
}
