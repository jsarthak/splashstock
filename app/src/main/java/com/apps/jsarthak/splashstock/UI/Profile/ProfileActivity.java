package com.apps.jsarthak.splashstock.UI.Profile;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.apps.jsarthak.splashstock.UI.Account.EditProfileActivity;
import com.apps.jsarthak.splashstock.UI.Account.LoginActivity;
import com.apps.jsarthak.splashstock.UI.Extra.AboutActivity;
import com.apps.jsarthak.splashstock.Utils.Utils;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.apps.jsarthak.splashstock.Widgets.CustomViewPager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private Utils mUtils;
    DataProcessor dataProcessor;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    ImageView bottomSheetUserImage;
    TextView bottomSheetName, bottomSheetUserName;
    SharedPreferences mSharedPreferences;
    LinearLayout addAccountContainer, userOptionsContainer;
    LoginHelper mLoginHelper;

    LinearLayout instagramHolder;
    LinearLayout locationHolder;
    LinearLayout twitterHolder;
    ViewPagerAdapter viewPagerAdapter;
    String username;
    TabLayout tabLayout;
    CustomViewPager customViewPager;

    LinearLayout statsButton, portfolioButton, settingsButton, aboutButton;

int likes =0, photos=0, collections=0;

    public View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;

    ImageView avatar, optionsMenu;
    TextView title, nameTextView, usernameTextView, bioTextView, locationTextView, instagramTextView, twitterTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);
        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_profile);
        dataProcessor = new DataProcessor(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLoginHelper = new LoginHelper(this);
        initViews();
        username = getIntent().getStringExtra(getString(R.string.username));
        usernameTextView.setText("@" + username);
        loadProfile(username);
        loadStats(username);

        optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
    }

    void initViews(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_collapsing_profile);
        ImageView back = toolbar.getRootView().findViewById(R.id.iv_toolbar_profile_back);
        optionsMenu = toolbar.getRootView().findViewById(R.id.toolbar_options_profile);

        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_profile,null);
        bottomSheetDialog.setContentView(bottomSheetView);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title = toolbar.getRootView().findViewById(R.id.tv_toolbar_profile_title);

        portfolioButton = bottomSheetView.getRootView().findViewById(R.id.bs_profile_portfolio);
        statsButton = bottomSheetView.getRootView().findViewById(R.id.bs_profile_stats);
        settingsButton = bottomSheetView.getRootView().findViewById(R.id.bs_settings);
        aboutButton = bottomSheetView.getRootView().findViewById(R.id.bs_about);

        // TODO: add intents here
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addAccountContainer = bottomSheetView.getRootView().findViewById(R.id.bottom_sheet_add_account);
        userOptionsContainer = bottomSheetView.getRootView().findViewById(R.id.bottom_sheet_user_container);

        if (mLoginHelper.isUserLogged()){
            userOptionsContainer.setVisibility(View.VISIBLE);
            addAccountContainer.setVisibility(GONE);
            bottomSheetUserName = bottomSheetView.getRootView().findViewById(R.id.tv_bottom_sheet_username);
            bottomSheetName = bottomSheetView.getRootView().findViewById(R.id.tv_bottom_sheet_user_fullname);
            bottomSheetUserImage = bottomSheetView.getRootView().findViewById(R.id.iv_bottom_sheet_userimage);
            bottomSheetName.setText(mSharedPreferences.getString(ProfileActivity.this.getString(R.string.name), ""));
            bottomSheetUserName.setText(mSharedPreferences.getString(ProfileActivity.this.getString(R.string.username), ""));
            Picasso.get().load(mSharedPreferences.getString(ProfileActivity.this.getString(R.string.profile_image), "")).fit().centerCrop().transform(new CropCircleTransformation()).into(bottomSheetUserImage);
        } else{
            userOptionsContainer.setVisibility(GONE);
            addAccountContainer.setVisibility(View.VISIBLE);
            addAccountContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AboutActivity.class));
            }
        });

        customViewPager = findViewById(R.id.vp_profile);
        tabLayout = findViewById(R.id.tab_profile);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        customViewPager.setAdapter(viewPagerAdapter);
        usernameTextView = collapsingToolbarLayout.getRootView().findViewById(R.id.tv_toolbar_profile_username);
        avatar = collapsingToolbarLayout.getRootView().findViewById(R.id.iv_toolbar_profile_user_image);
        nameTextView = collapsingToolbarLayout.getRootView().findViewById(R.id.tv_toolbar_profile_name);
        bioTextView = collapsingToolbarLayout.getRootView().findViewById(R.id.tv_toolbar_profile_description);
        instagramTextView = collapsingToolbarLayout.getRootView().findViewById(R.id.tv_toolbar_profile_instagram);
        twitterTextView = collapsingToolbarLayout.getRootView().findViewById(R.id.tv_toolbar_profile_twitter);
        instagramHolder = collapsingToolbarLayout.getRootView().findViewById(R.id.instagram_holder);
        twitterHolder = collapsingToolbarLayout.getRootView().findViewById(R.id.twitter_holder);
        locationHolder = collapsingToolbarLayout.getRootView().findViewById(R.id.location_holder);
        locationTextView = collapsingToolbarLayout.getRootView().findViewById(R.id.tv_profile_location);
        customViewPager.setOffscreenPageLimit(3);
    }

    void loadProfile(String username){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("users")
                .appendPath(username)
                .appendPath("statistics")
                .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processStats(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);

            }
        });
        queue.add(stringRequest);
    }

    void processStats(String response){
        try {
            JSONObject stats = new JSONObject(response);
            JSONObject downloads = stats.getJSONObject(getString(R.string.downloads));
            JSONObject views = stats.getJSONObject(getString(R.string.views));
            JSONObject likes = stats.getJSONObject(getString(R.string.likes));
            JSONObject historicalDownload = downloads.getJSONObject("historical");
            JSONObject historicalLikes = likes.getJSONObject("historical");
            JSONObject historicalViews = views.getJSONObject("historical");

            final int totalDownload = downloads.getInt("total");
            final int totalLikes = likes.getInt("total");
            final int totalViews = views.getInt("total");
            final int changeDownloads = historicalDownload.getInt("change");
            final int changeLikes = historicalLikes.getInt("change");
            final int changeViews = historicalViews.getInt("change");

            statsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View messageView = getLayoutInflater().inflate(R.layout.profile_more_dialog, null, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    View messageRootView = messageView.getRootView();

  /*                  TextView infoPicDate = messageRootView.findViewById(R.id.tv_info_dialog_date);
                    infoPicDate.setText("Published on " + dateTaken);*/
                    TextView infoLikesCount = messageRootView.findViewById(R.id.tv_info_dialog_like_count);
                    infoLikesCount.setText(totalLikes+"");
                    TextView likesChange = messageRootView.findViewById(R.id.tv_info_dialog_like_change);
                    likesChange.setText("+" + changeLikes + " since last month");
                    TextView infoViewCount = messageRootView.findViewById(R.id.tv_info_dialog_views_count);
                    infoViewCount.setText(totalViews+"");
                    TextView viewChange = messageRootView.findViewById(R.id.tv_info_dialog_view_change);
                    viewChange.setText("+" + changeViews + " since last month");
                    TextView infoDownloadCount = messageRootView.findViewById(R.id.tv_info_dialog_download_count);
                    infoDownloadCount.setText(totalDownload+"");
                    TextView downloadChange = messageRootView.findViewById(R.id.tv_info_dialog_download_change);
                    downloadChange.setText("+" +changeDownloads + " since last month");

                    builder.setView(messageView);
                    builder.create();
                    builder.show();
                }
            });

        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    void loadStats(String username){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("users")
                .appendPath(username)
                .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);

            }
        });
        queue.add(stringRequest);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new ProfilePhotoFragment();
                case 1:return new ProfileLikesFragment();
                case 2: return new ProfileCollectionFragment();
                default:return new ProfilePhotoFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return photos + " Photos";
                case 1:
                    return likes+" Likes";
                case 2:
                    return collections+" Collections";
                default:
                    return photos+" Photos";
            }
        }
    }


    void processData(String data) {
        try {
            JSONObject user = new JSONObject(data);
            final User u = dataProcessor.processUser(user);
            title.setText(u.name);
            usernameTextView.setText("@"+u.userName);
            twitterHolder.setVisibility(GONE);
            locationHolder.setVisibility(GONE);
            instagramHolder.setVisibility(GONE);
            nameTextView.setText(u.name);
            Picasso.get().load(u.profileImage.medium).placeholder(R.drawable.ic_user).fit().centerCrop().transform(new CropCircleTransformation()).into(avatar);
            if (u.bio.equals("null") || u.bio.isEmpty() || u.bio.equals("")){
                bioTextView.setVisibility(GONE);
            } else {
                bioTextView.setText(u.bio);
                bioTextView.setVisibility(View.VISIBLE);
            }


            if (u.location.equals("null") || u.location.isEmpty() || u.location.equals("")){
                locationTextView.setVisibility(GONE);
                locationHolder.setVisibility(GONE);
            } else {
                locationTextView.setVisibility(View.VISIBLE);
                locationTextView.setText(u.location);
                locationHolder.setVisibility(View.VISIBLE);
            }

            if (u.instagramUserName.equals("null") || u.instagramUserName.isEmpty() || u.instagramUserName.equals("")){
                instagramHolder.setVisibility(GONE);
                instagramTextView.setVisibility(GONE);
            } else {
                instagramHolder.setVisibility(View.VISIBLE);
                instagramTextView.setText("/"+u.instagramUserName);
                instagramTextView.setVisibility(View.VISIBLE);
                instagramHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uriBrowser = Uri.parse("https://instagram.com/"+u.instagramUserName);
                        Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                        feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try{
                            startActivity(feedback);
                        } catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/"+u.instagramUserName)));
                        }                    }
                });
            }
            if (u.twitterUserName.equals("null") || u.twitterUserName.isEmpty() || u.twitterUserName.equals("")){
                twitterTextView.setVisibility(GONE);
                twitterHolder.setVisibility(GONE);
            } else {
                twitterTextView.setText("/"+u.twitterUserName);
                twitterTextView.setVisibility(View.VISIBLE);

                twitterHolder.setVisibility(View.VISIBLE);

                twitterHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uriBrowser = Uri.parse("https://twitter.com/"+u.instagramUserName);
                        Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                        feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try{
                            startActivity(feedback);
                        } catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+u.instagramUserName)));
                        }                    }
                });
            }
            likes = u.totalLikes;
            photos = u.totalPhotos;
            collections = u.totalCollections;

            tabLayout.setupWithViewPager(customViewPager);

            if (u.portfolioUrl.equals("null") || u.portfolioUrl.isEmpty() || u.portfolioUrl.equals("")){
                portfolioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uriBrowser = Uri.parse("https://unsplash.com/users/"+u.id);
                        Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                        feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try{
                            startActivity(feedback);
                        } catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/users/"+u.id)));
                        }
                    }
                });
            } else {
                portfolioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uriBrowser = Uri.parse(u.portfolioUrl);
                        Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                        feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try{
                            startActivity(feedback);
                        } catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(u.portfolioUrl)));
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
