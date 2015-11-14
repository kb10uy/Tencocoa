package org.kb10uy.tencocoa.settings;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import org.kb10uy.tencocoa.R;
import org.kb10uy.tencocoa.model.TencocoaHelper;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        TencocoaHelper.setCurrentTheme(this, pref.getString(getString(R.string.preference_appearance_theme), "Black"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        ((WebView)findViewById(R.id.LicenseWebView)).loadUrl(getString(R.string.uri_asset_licenses));
    }
}
