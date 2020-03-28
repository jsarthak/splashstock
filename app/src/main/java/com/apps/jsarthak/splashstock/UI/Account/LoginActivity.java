package com.apps.jsarthak.splashstock.UI.Account;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.jsarthak.splashstock.Data.AccessToken;
import com.apps.jsarthak.splashstock.Data.User;
import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    Utils mUtils;
    Button loginButton, joinButton;
    FrameLayout mLoginContainer;
    WebView mLoginWebView;
    ProgressDialog progressDialog;
    ProgressBar mLoadingProgress;
    String code;
    String oauth_url;
    AccessToken accessToken;
    Intent intent;
    User mUser;
    ImageView closeButton;

    public LoginHelper loginHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);
        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_login);
        intent = getIntent();

        initViews();
        loginHelper = new LoginHelper(LoginActivity.this);

        if (intent.hasExtra("LOGIN")){
            loadLogin();
        } else{
            showContainer();
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadLogin();
                }
            });
        }


    }

    void initViews() {
        mLoginContainer = findViewById(R.id.login_container);
        loginButton = findViewById(R.id.btn_login);
        joinButton = findViewById(R.id.btn_join);
        mLoadingProgress = findViewById(R.id.pb_loading_progress);
        mLoginWebView = findViewById(R.id.wv_login);
        closeButton = findViewById(R.id.iv_close_login);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriBrowser = Uri.parse("https://unsplash.com/join");
                Intent join = new Intent(Intent.ACTION_VIEW, uriBrowser);
                join.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(join);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/join")));
                }         }
        });
    }


    void showContainer() {
        mLoginContainer.setVisibility(View.VISIBLE);
        mLoginWebView.setVisibility(GONE);
        mLoadingProgress.setVisibility(GONE);
    }

    private void showLoadingIndicator() {
        mLoadingProgress.setVisibility(View.VISIBLE);
        mLoginWebView.setVisibility(GONE);
        mLoginContainer.setVisibility(View.GONE);
    }

    void showLoginWebPage() {
        mLoginWebView.setVisibility(View.VISIBLE);
        mLoginContainer.setVisibility(GONE);
        mLoadingProgress.setVisibility(GONE);
    }

    void loadLogin() {
        oauth_url = "https://www.unsplash.com/oauth/authorize?client_id="+getString(R.string.client_id)+"&redirect_uri=splashstock://unsplash-auth-callback&response_type=code&scope=public+read_user+write_user+read_photos+write_photos+write_likes+write_followers+read_collections+write_collections";
        showLoadingIndicator();

        if (oauth_url != null) {
            mLoginWebView.getSettings().setJavaScriptEnabled(true);
            mLoginWebView.loadUrl(oauth_url);
            mLoginWebView.setWebViewClient(new WebViewClient() {
                boolean authComplete = false;
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    showLoginWebPage();
                    if(url.contains("?code=") && authComplete == false) {
                        authComplete = true;
                        Uri uri = Uri.parse(url);
                        code = uri.getQueryParameter("code");
                        new AccessTokenGet().execute();
                    }

                }
            });
        } else {
            showContainer();
            Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT);
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showContainer();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                String url = "https://unsplash.com/oauth/token?client_id="+getString(R.string.client_id) + "&client_secret="+getString(R.string.client_secret)+"&redirect_uri=splashstock://unsplash-auth-callback&code=" + code + "&grant_type=authorization_code";
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String access_token = jsonObject.getString(getString(R.string.access_token));
                                    String tokenType = jsonObject.getString(getString(R.string.token_type));
                                    String scope = jsonObject.getString(getString(R.string.scope));
                                    String refreshToken = jsonObject.getString(getString(R.string.refresh_token));
                                    String createdAt = jsonObject.getString(getString(R.string.token_created_at));
                                    Map<String, Object> map = new HashMap<>();
                                    map.put(getString(R.string.access_token), access_token);
                                    map.put(getString(R.string.refresh_token), refreshToken);
                                    map.put(getString(R.string.scope), scope);
                                    map.put(getString(R.string.token_type), tokenType);
                                    map.put(getString(R.string.token_created_at), createdAt);
                                    accessToken = new AccessToken(LoginActivity.this, map);
                                    loadUser(access_token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                });
                queue.add(stringRequest);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                progressDialog.dismiss();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }
    }

    void loadUser(String token){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("me")
                .appendQueryParameter(getString(R.string.access_token), token);
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject user = new JSONObject(response);
                            JSONObject user_links = user.getJSONObject(getString(R.string.links));
                            JSONObject profileImage = user.getJSONObject(getString(R.string.profile_image));
                            Map<String, Object> map = new HashMap<>();
                            map.put(getString(R.string.user_id), user.get(getString(R.string.id)));
                            map.put(getString(R.string.user_updated_at), user.get(getString(R.string.updated_at)));
                            map.put(getString(R.string.username), user.get(getString(R.string.username)));
                            map.put(getString(R.string.name), user.get(getString(R.string.name)));
                            map.put(getString(R.string.first_name), user.get(getString(R.string.first_name)));
                            map.put(getString(R.string.last_name), user.get(getString(R.string.last_name)));
                            map.put(getString(R.string.twitter_username), user.get(getString(R.string.twitter_username)));
                            map.put(getString(R.string.portfolio_url), user.get(getString(R.string.portfolio_url)));
                            map.put(getString(R.string.bio), user.get(getString(R.string.bio)));
                            map.put(getString(R.string.location), user.get(getString(R.string.location)));
                            map.put(getString(R.string.user_self), user_links.get(getString(R.string.self)));
                            map.put(getString(R.string.user_html), user_links.get(getString(R.string.html)));
                            map.put(getString(R.string.user_photos), user_links.get(getString(R.string.user_photos)));
                            map.put(getString(R.string.user_followers), user_links.get(getString(R.string.user_followers)));
                            map.put(getString(R.string.user_following), user_links.get(getString(R.string.user_following)));
                            map.put(getString(R.string.user_likes), user_links.get(getString(R.string.likes)));
                            map.put(getString(R.string.user_portfolio), user_links.get(getString(R.string.user_portfolio)));
                            map.put(getString(R.string.user_small), profileImage.get(getString(R.string.small)));
                            map.put(getString(R.string.user_medium), profileImage.get(getString(R.string.user_medium)));
                            map.put(getString(R.string.user_large), profileImage.get(getString(R.string.user_large)));
                            map.put(getString(R.string.instagram_username), user.get(getString(R.string.instagram_username)));
                            map.put(getString(R.string.total_photos), user.get(getString(R.string.total_photos)));
                            map.put(getString(R.string.total_likes), user.get(getString(R.string.total_likes)));
                            map.put(getString(R.string.total_collections), user.get(getString(R.string.total_collections)));
                            mUser =new User(LoginActivity.this, map);
                            loginHelper.storeTokenAndUser(accessToken, mUser);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });
        queue.add(stringRequest);
    }

}
