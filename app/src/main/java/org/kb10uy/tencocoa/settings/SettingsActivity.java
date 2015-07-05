package org.kb10uy.tencocoa.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.kb10uy.tencocoa.R;
import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.views.AppCompatPreferenceActivity;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        TencocoaHelper.setCurrentTheme(this, pref.getString(getString(R.string.preference_appearance_theme), "Black"));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_header, target);
        PreferenceManager.setDefaultValues(this, R.xml.settings_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.settings_appearance, false);
        PreferenceManager.setDefaultValues(this, R.xml.settings_twitter, false);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        //TODO: 暫定
        return true;
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            switch (getArguments().getString("category")) {
                case "general":
                    addPreferencesFromResource(R.xml.settings_general);
                    break;
                case "appearance":
                    addPreferencesFromResource(R.xml.settings_appearance);
                    break;
                case "twitter":
                    addPreferencesFromResource(R.xml.settings_twitter);
                    findPreference(getString(R.string.preference_twitter_accounts_delete)).setOnPreferenceClickListener(preference -> {
                        getActivity().deleteFile(getString(R.string.accounts_file_name));
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                .putInt(getString(R.string.preference_twitter_accounts_count), 0)
                                .putInt(getString(R.string.preference_twitter_accounts_auto_number), 0)
                                .commit();
                        Toast.makeText(getActivity(), R.string.prefscreen_twitter_account_delete_success, Toast.LENGTH_SHORT);
                        return true;
                    });
                    break;
            }
        }
    }
}
