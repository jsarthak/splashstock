package com.apps.jsarthak.splashstock.UI.Account;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.jsarthak.splashstock.Data.DataProcessor;
import com.apps.jsarthak.splashstock.Data.User;
import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.Utils.NetworkUtils;
import com.apps.jsarthak.splashstock.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    Utils mUtils;

    NetworkUtils mNetworkUtils;
    DataProcessor dataProcessor;
    LoginHelper mLoginHelper;
    Toolbar toolbar;
    SharedPreferences mSharedPreferences;
    User mUser;
    ImageView backButton;

    ProgressDialog progressDialog;
    EditText usernameET, firstNameET, lastNameET, emailET, locationET, bioET, urlET, instaET;

    String username, firstname, lastname, email, location, bio, url, insta;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);
        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_edit_profile);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLoginHelper = new LoginHelper(this);
        mNetworkUtils = new NetworkUtils(this);
        dataProcessor = new DataProcessor(this);

        initViews();

        loadMe();



    }

    void initViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        backButton = toolbar.getRootView().findViewById(R.id.iv_toolbar_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        usernameET = findViewById(R.id.et_ep_username);
        firstNameET = findViewById(R.id.et_ep_first_name);
        lastNameET = findViewById(R.id.et_ep_lastname);
        emailET = findViewById(R.id.et_ep_email);
        locationET = findViewById(R.id.et_ep_location);
        bioET = findViewById(R.id.et_ep_bio);
        urlET = findViewById(R.id.et_ep_url);
        instaET = findViewById(R.id.et_ep_insta);
        submit = findViewById(R.id.btn_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    void updateProfile(){
        progressDialog.show();
        username = usernameET.getText().toString();
        firstname = firstNameET.getText().toString();
        lastname = lastNameET.getText().toString();
        email = emailET.getText().toString();
        location = locationET.getText().toString();
        bio = bioET.getText().toString();
        url = urlET.getText().toString();
        insta = instaET.getText().toString();

        if (isValidEmail(email)){
            if (mNetworkUtils.isNetworkAvailable()){
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority(getString(R.string.base_url))
                        .appendPath("me")
                        .appendQueryParameter(getString(R.string.username), username)
                        .appendQueryParameter(getString(R.string.first_name), firstname)
                        .appendQueryParameter(getString(R.string.last_name), lastname)
                        .appendQueryParameter("email", email)
                        .appendQueryParameter(getString(R.string.location), location)
                        .appendQueryParameter(getString(R.string.bio), bio)
                        .appendQueryParameter(getString(R.string.instagram_username), insta)
                        .appendQueryParameter(getString(R.string.portfolio_url), url)
                        .appendQueryParameter(getString(R.string.access_token), mSharedPreferences.getString(getString(R.string.access_token), ""));
                String url = builder.build().toString();
                RequestQueue queue = Volley.newRequestQueue(this);
                StringRequest request = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ");
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(request);
            }
        } else {
            progressDialog.dismiss();
            emailET.setError("Invalid email");
            return;
        }
    }

    void loadMe(){
        progressDialog.show();
        submit.setEnabled(false);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("me")
                .appendQueryParameter(getString(R.string.access_token), mSharedPreferences.getString(getString(R.string.access_token), ""));
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject user = new JSONObject(response);
                            mUser = dataProcessor.processUser(user);

                            if (mUser.getUserName() != null || !mUser.getUserName().isEmpty() || !mUser.getUserName().equals("")){
                                usernameET.setText(mUser.userName);
                            } else{
                                usernameET.setText("");
                            }

                            if (mUser.getFirstName() != null || !mUser.getFirstName().isEmpty() || !mUser.getFirstName().equals("")) {
                                firstNameET.setText(mUser.getFirstName());
                            }else{
                                firstNameET.setText("");
                            }


                            if (mUser.getLastName() != null || !mUser.getLastName().isEmpty() || !mUser.getLastName().equals("")){
                                lastNameET.setText(mUser.lastName);
                            }else{
                                lastNameET.setText("");
                            }

                            if (mUser.getInstagramUserName() != null || !mUser.getInstagramUserName().isEmpty() || !mUser.getInstagramUserName().equals("")){
                                instaET.setText(mUser.getInstagramUserName());
                            }else{
                                instaET.setText("");
                            }

                            if (mUser.getPortfolioUrl() != null || !mUser.getPortfolioUrl().isEmpty() || !mUser.getPortfolioUrl().equals("")){
                                urlET.setText(mUser.getPortfolioUrl());
                            }else{
                                urlET.setText("");
                            }

                            if (mUser.getBio() != null || !mUser.getBio().isEmpty() || !mUser.getBio().equals("")){
                                bioET.setText(mUser.getBio());
                            }else{
                                bioET.setText("");
                            }

                            if (mUser.getLocation() != null || !mUser.getLocation().isEmpty() || !mUser.getLocation().equals("")){
                                locationET.setText(mUser.getLocation());
                            }else{
                                usernameET.setText("");
                            }

                            if (mUser.getLastName() != null || !mUser.getLastName().isEmpty() || !mUser.getLastName().equals("")){
                                lastNameET.setText(mUser.lastName);
                            }
                            if (user.getString("email") !=null || !user.getString("email").isEmpty() || !user.getString("email").equals("")){
                                emailET.setText(user.getString("email"));
                            }

                            progressDialog.dismiss();
                            submit.setEnabled(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            submit.setEnabled(true);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
                progressDialog.dismiss();
                submit.setEnabled(true);
                finish();
            }
        });
        queue.add(stringRequest);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}


