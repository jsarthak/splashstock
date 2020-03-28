package com.apps.jsarthak.splashstock.UI.Extra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.Account.EditProfileActivity;
import com.apps.jsarthak.splashstock.UI.Account.LoginActivity;
import com.apps.jsarthak.splashstock.Utils.Utils;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static android.view.View.GONE;

public class SettingsActivity extends AppCompatActivity {

    Utils mUtils;

    private final static String TAG = "SettingsActivity";
    private static boolean settingChanged = false;
    private static boolean activityRestarted = false;
    LoginHelper mLoginHelper;
    Toolbar toolbar;
    SharedPreferences mSharedPreferences;
    LinearLayout aboutButton;
    ImageView backButton;
    LinearLayout addAccount;
    LinearLayout userContainer;
    LinearLayout editButton;
    LinearLayout logoutButton;
    ImageView avatar;
    TextView username, name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);
        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction().replace(R.id.pref_content, new SettingsFragment()).commit();
        if (!activityRestarted) {
            settingChanged = false;
        }

        activityRestarted = false;
        mLoginHelper = new LoginHelper(this);
        toolbar = findViewById(R.id.toolbar);
        backButton = toolbar.getRootView().findViewById(R.id.iv_toolbar_back);
        logoutButton = findViewById(R.id.settings_logout);
        aboutButton = findViewById(R.id.about_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
            }
        });
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        addAccount = findViewById(R.id.add_account_button_settings);
        userContainer = findViewById(R.id.ll_settingsuser_container);
        editButton = findViewById(R.id.settings_edit_profile);
        avatar = findViewById(R.id.iv_settings_user_image);
        name = findViewById(R.id.tv_settings_name);
        username = findViewById(R.id.tv_settings_username);
        if (mLoginHelper.isUserLogged()){
            addAccount.setVisibility(GONE);
            userContainer.setVisibility(View.VISIBLE);
            Picasso.get().load(mSharedPreferences.getString(getString(R.string.profile_image), "")).fit().centerCrop().transform(new CropCircleTransformation()).into(avatar);
            name.setText(mSharedPreferences.getString(getString(R.string.name), ""));
            username.setText("@"+mSharedPreferences.getString(getString(R.string.username), ""));
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class));
                }
            });
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoginHelper.logout();
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        } else{
            addAccount.setVisibility(View.VISIBLE);
            userContainer.setVisibility(GONE);
            addAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.putExtra("LOGIN", "LOGIN");
                    startActivity(intent);
                }
            });
        }
    }



    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        private void restartActivity(){
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);
            activityRestarted = true;
        }
    }

    private static long dirSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    result += dirSize(aFileList);
                } else {
                    result += aFileList.length();
                }
            }
            return result / 1024 / 1024;
        }
        return 0;
    }


}
