package com.apps.jsarthak.splashstock.UI.Main;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.apps.jsarthak.splashstock.MainActivity;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.Account.EditProfileActivity;
import com.apps.jsarthak.splashstock.UI.Account.LoginActivity;
import com.apps.jsarthak.splashstock.UI.Extra.AboutActivity;
import com.apps.jsarthak.splashstock.UI.Extra.SettingsActivity;
import com.apps.jsarthak.splashstock.UI.Main.Me.MeCollectionFragment;
import com.apps.jsarthak.splashstock.UI.Main.Me.MeLikesFragment;
import com.apps.jsarthak.splashstock.UI.Main.Me.MePhotosFragment;
import com.apps.jsarthak.splashstock.UI.Profile.ProfileActivity;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = MeFragment.class.getSimpleName();
    public View bottomSheetView;
    public TabLayout tabLayout;
    public LinearLayout instagramHolder;
    public LinearLayout locationHolder;
    public CollapsingToolbarLayout cpt;
    public LinearLayout twitterHolder;
    public TextView twitter, instagram, name, username, bio, location;
    public ImageView avatar;
    public LinearLayout settingsButton, aboutButton, logoutButton, statsButton;
    int photos = 0, likes = 0, collections = 0;
    ImageView bottomSheetUserImage;
    TextView bottomSheetName, bottomSheetUserName;
    BottomSheetDialog bottomSheetDialog;
    String photosData, likesData, collectionsData;
    LinearLayout addAccountContainer, userOptionsContainer;

    View rootView;
    User mUser;
    LoginHelper mLoginHelper;

    ViewPager viewPager;
    DataProcessor dataProcessor;
    MePagerAdapter mePagerAdapter;
    SharedPreferences mSharedPreferences;

    Button loginButton;
    Button signupButton;

    MainActivity mainActivity;
    LinearLayout loginContainer;
    FrameLayout meContainer;

    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (newValue.toString().isEmpty()) {
            return false;
        } else {
            initMeView();
            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_me, container, false);
        mLoginHelper = new LoginHelper(getActivity());
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dataProcessor = new DataProcessor(getActivity());
        loginContainer = rootView.findViewById(R.id.me_login_container);
        meContainer = rootView.findViewById(R.id.me_container);
        if (mLoginHelper.isUserLogged()) {
            initMeView();
        } else {
            initLoginView();
        }
        return rootView;
    }

    public void initLoginView() {
        loginContainer.setVisibility(View.VISIBLE);
        meContainer.setVisibility(View.GONE);
        loginButton = rootView.findViewById(R.id.btn_login);
        signupButton = rootView.findViewById(R.id.btn_join);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("LOGIN", "LOGIN");
                startActivity(intent);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriBrowser = Uri.parse("https://unsplash.com/join");
                Intent join = new Intent(Intent.ACTION_VIEW, uriBrowser);
                join.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(join);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/join")));
                }
            }
        });
    }

    public void initMeView() {
        mainActivity = (MainActivity) getActivity();
        loginContainer.setVisibility(View.GONE);
        meContainer.setVisibility(View.VISIBLE);
        viewPager = rootView.findViewById(R.id.vp_me);
        mePagerAdapter = new MePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mePagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        cpt = getActivity().findViewById(R.id.toolbar_collapse);
        avatar = cpt.getRootView().findViewById(R.id.iv_toolbar_me_user_image);
        name = cpt.getRootView().findViewById(R.id.tv_toolbar_me_name);
        username = cpt.getRootView().findViewById(R.id.tv_toolbar_me_username);
        bio = cpt.getRootView().findViewById(R.id.tv_toolbar_me_description);
        twitter = cpt.getRootView().findViewById(R.id.tv_toolbar_profile_twitter);
        instagram = cpt.getRootView().findViewById(R.id.tv_toolbar_profile_instagram);
        location = cpt.getRootView().findViewById(R.id.tv_profile_location);
        instagramHolder = cpt.getRootView().getRootView().findViewById(R.id.instagram_holder);
        twitterHolder = cpt.getRootView().findViewById(R.id.twitter_holder);
        locationHolder = cpt.getRootView().findViewById(R.id.location_holder);
        twitterHolder.setVisibility(GONE);
        locationHolder.setVisibility(GONE);
        instagramHolder.setVisibility(GONE);
        bio.setVisibility(GONE);

        loadUser();

        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_me, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        statsButton = bottomSheetView.getRootView().findViewById(R.id.bs_stats);
        settingsButton = bottomSheetView.getRootView().findViewById(R.id.bs_settings);
        aboutButton = bottomSheetView.getRootView().findViewById(R.id.bs_about);
        addAccountContainer = bottomSheetView.getRootView().findViewById(R.id.bottom_sheet_add_account);
        userOptionsContainer = bottomSheetView.getRootView().findViewById(R.id.bottom_sheet_user_container);
        mainActivity.optionsButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        if (mLoginHelper.isUserLogged()) {

            userOptionsContainer.setVisibility(View.VISIBLE);
            addAccountContainer.setVisibility(GONE);
            bottomSheetUserName = bottomSheetView.getRootView().findViewById(R.id.tv_bottom_sheet_username);
            bottomSheetName = bottomSheetView.getRootView().findViewById(R.id.tv_bottom_sheet_user_fullname);
            bottomSheetUserImage = bottomSheetView.getRootView().findViewById(R.id.iv_bottom_sheet_userimage);
            bottomSheetName.setText(mSharedPreferences.getString(getActivity().getString(R.string.name), ""));
            bottomSheetUserName.setText(mSharedPreferences.getString(getActivity().getString(R.string.username), ""));
            Picasso.get().load(mSharedPreferences.getString(getActivity().getString(R.string.profile_image), "")).fit().centerCrop().transform(new CropCircleTransformation()).into(bottomSheetUserImage);

            aboutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AboutActivity.class));
                    bottomSheetDialog.dismiss();
                }
            });
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    bottomSheetDialog.dismiss();
                }
            });
        }

    }

    void loadUser() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("me")
                .appendQueryParameter(getString(R.string.access_token), mSharedPreferences.getString(getActivity().getString(R.string.access_token), ""));
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Log.d(TAG, "onResponse: " + response);
                            JSONObject user = new JSONObject(response);
                            mUser = dataProcessor.processUser(user);

                            Picasso.get().load(mUser.profileImage.medium).placeholder(R.drawable.ic_user).fit().centerCrop().transform(new CropCircleTransformation()).into(avatar);
                            if (mUser.bio.equals("null") || mUser.bio.isEmpty() || mUser.bio.equals("")) {
                                bio.setVisibility(GONE);
                            } else {
                                bio.setText(mUser.bio);
                                bio.setVisibility(View.VISIBLE);
                            }

                            if (mUser.location.equals("null") || mUser.location.isEmpty() || mUser.location.equals("")) {
                                location.setVisibility(GONE);
                                locationHolder.setVisibility(GONE);
                            } else {
                                location.setVisibility(View.VISIBLE);
                                location.setText(mUser.location);
                                locationHolder.setVisibility(View.VISIBLE);
                            }

                            if (mUser.instagramUserName.equals("null") || mUser.instagramUserName.isEmpty() || mUser.instagramUserName.equals("")) {
                                instagramHolder.setVisibility(GONE);
                                instagram.setVisibility(GONE);
                            } else {
                                instagramHolder.setVisibility(View.VISIBLE);
                                instagram.setText("/" + mUser.instagramUserName);
                                instagram.setVisibility(View.VISIBLE);
                                instagramHolder.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Uri uriBrowser = Uri.parse("https://instagram.com/" + mUser.instagramUserName);
                                        Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                                        feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        try {
                                            startActivity(feedback);
                                        } catch (ActivityNotFoundException e) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/" + mUser.instagramUserName)));
                                        }
                                    }
                                });
                            }
                            if (mUser.twitterUserName.equals("null") || mUser.twitterUserName.isEmpty() || mUser.twitterUserName.equals("")) {
                                twitter.setVisibility(GONE);
                                twitterHolder.setVisibility(GONE);
                            } else {
                                twitter.setText("/" + mUser.twitterUserName);
                                twitter.setVisibility(View.VISIBLE);

                                twitterHolder.setVisibility(View.VISIBLE);

                                twitterHolder.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Uri uriBrowser = Uri.parse("https://twitter.com/" + mUser.instagramUserName);
                                        Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                                        feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        try {
                                            startActivity(feedback);
                                        } catch (ActivityNotFoundException e) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + mUser.instagramUserName)));
                                        }
                                    }
                                });
                            }
                            name.setText(mUser.name);
                            username.setText("@" + mUser.userName);

                            photos = mUser.totalPhotos;
                            likes = mUser.totalLikes;
                            collections = mUser.totalCollections;
                            mainActivity.tabLayout.setupWithViewPager(viewPager);

                            loadStats(mUser.userName);


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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                .appendPath("statistics")
                .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
    class MePagerAdapter extends FragmentPagerAdapter {


        public MePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MePhotosFragment();
                case 1:
                    return new MeLikesFragment();
                case 2:
                    return new MeCollectionFragment();
                default:
                    return new MePhotosFragment();
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return photos + " Photos";
                case 1:
                    return likes + " Likes";
                case 2:
                    return collections + " Collections";
                default:
                    return photos + " Photos";
            }
        }
    }


}
