package com.apps.jsarthak.splashstock;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.UI.Main.CollectionFragment;
import com.apps.jsarthak.splashstock.UI.Main.EditorialFragment;
import com.apps.jsarthak.splashstock.UI.Main.FeaturedFragment;
import com.apps.jsarthak.splashstock.UI.Main.MeFragment;
import com.apps.jsarthak.splashstock.UI.Search.SearchActivity;
import com.apps.jsarthak.splashstock.Utils.Utils;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.apps.jsarthak.splashstock.Widgets.CustomViewPager;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    public ImageView optionsButtonEditiorial, optionsButtonCollecion, optionsButtonFeatured, searchButton, optionsButtonProfile;


    public CustomViewPager mCustomViewPager;
    public MainViewPagerAdapter mMainViewPagerAdapter;
    public BottomNavigationView mBottomNavigationView;
    public TabLayout tabLayout;
    public LinearLayout instagramHolder;
    public LinearLayout locationHolder;
    public CollapsingToolbarLayout cpt;
    public LinearLayout twitterHolder;
    public TextView twitter, instagram, name, username, bio, location;
    public ImageView avatar;
    SharedPreferences mSharedPreferences;
    LoginHelper mLoginHelper;
    FrameLayout meContainer;
    android.support.v7.widget.Toolbar toolbar;
    private Utils mUtils;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_editorial:
                    mCustomViewPager.setCurrentItem(0);
                    toolbar.setVisibility(View.VISIBLE);
                    optionsButtonEditiorial.setVisibility(View.VISIBLE);
                    optionsButtonCollecion.setVisibility(View.GONE);
                    optionsButtonFeatured.setVisibility(View.GONE);
                    tabLayout.setVisibility(GONE);
                    optionsButtonProfile.setVisibility(View.GONE);
                    meContainer.setVisibility(View.GONE);
                    return true;
                case R.id.nav_collection:
                    mCustomViewPager.setCurrentItem(2);
                    toolbar.setVisibility(View.VISIBLE);
                    optionsButtonEditiorial.setVisibility(View.GONE);
                    optionsButtonCollecion.setVisibility(View.VISIBLE);
                    optionsButtonFeatured.setVisibility(View.GONE);
                    optionsButtonProfile.setVisibility(View.GONE);
                    tabLayout.setVisibility(GONE);
                    meContainer.setVisibility(View.GONE);
                    return true;
                case R.id.nav_featured:
                    mCustomViewPager.setCurrentItem(1);
                    toolbar.setVisibility(View.VISIBLE);
                    optionsButtonEditiorial.setVisibility(View.GONE);
                    optionsButtonCollecion.setVisibility(View.GONE);
                    optionsButtonFeatured.setVisibility(View.VISIBLE);
                    meContainer.setVisibility(View.GONE);
                    tabLayout.setVisibility(GONE);
                    optionsButtonProfile.setVisibility(View.GONE);
                    return true;
                case R.id.nav_me:
                    mCustomViewPager.setCurrentItem(3);
                    optionsButtonEditiorial.setVisibility(View.GONE);
                    optionsButtonCollecion.setVisibility(View.GONE);
                    optionsButtonFeatured.setVisibility(View.GONE);
                    optionsButtonProfile.setVisibility(View.VISIBLE);
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment f = mMainViewPagerAdapter.getFragment(mCustomViewPager, 3, fm);
                    MeFragment meFragment = (MeFragment) f;

                    if (mLoginHelper.isUserLogged()) {

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
                        meContainer.setVisibility(View.VISIBLE);

                        Picasso.get().load(mSharedPreferences.getString(getString(R.string.profile_image), "")).placeholder(R.drawable.ic_user).fit().centerCrop().transform(new CropCircleTransformation()).into(avatar);
                        String mName = mSharedPreferences.getString(getString(R.string.name), "");
                        String mUserName = mSharedPreferences.getString(getString(R.string.name), "");
                        String mBio = mSharedPreferences.getString(getString(R.string.name), "");
                        String mTwitter = mSharedPreferences.getString(getString(R.string.name), "");
                        final String mInsta = mSharedPreferences.getString(getString(R.string.name), "");
                        tabLayout.setVisibility(View.VISIBLE);

                        if (mBio.equals("null") || mBio.isEmpty() || mBio.equals("")) {
                            bio.setVisibility(GONE);
                        } else {
                            bio.setText(mBio);
                            bio.setVisibility(View.VISIBLE);
                        }


                        if (mInsta.equals("null") || mInsta.isEmpty() || mInsta.equals("")) {
                            instagramHolder.setVisibility(GONE);
                            instagram.setVisibility(GONE);
                        } else {
                            instagramHolder.setVisibility(View.VISIBLE);
                            instagram.setText("/" + mInsta);
                            instagram.setVisibility(View.VISIBLE);
                            instagramHolder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uriBrowser = Uri.parse("https://instagram.com/" + mInsta);
                                    Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                                    feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    try {
                                        startActivity(feedback);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/" + mInsta)));
                                    }
                                }
                            });
                        }
                        if (mTwitter.equals("null") || mTwitter.isEmpty() || mTwitter.equals("")) {
                            twitter.setVisibility(GONE);
                            twitterHolder.setVisibility(GONE);
                        } else {
                            twitter.setText("/" + mTwitter);
                            twitter.setVisibility(View.VISIBLE);

                            twitterHolder.setVisibility(View.VISIBLE);

                            twitterHolder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uriBrowser = Uri.parse("https://twitter.com/" + mInsta);
                                    Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                                    feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    try {
                                        startActivity(feedback);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + mInsta)));
                                    }
                                }
                            });
                        }
                        name.setText(mName);
                        username.setText("@" + mUserName);
                        meFragment.initMeView();

                    } else {
                        meContainer.setVisibility(GONE);
                        meFragment.initLoginView();
                    }
                    toolbar.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };


    public void initViews() {
        toolbar = findViewById(R.id.toolbar);
        cpt = findViewById(R.id.toolbar_collapse);
        mCustomViewPager = findViewById(R.id.vp_main);
        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mCustomViewPager.setAdapter(mMainViewPagerAdapter);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mCustomViewPager.setPagingEnabled(false);
        tabLayout = findViewById(R.id.tab_me);
        optionsButtonEditiorial = toolbar.getRootView().findViewById(R.id.toolbar_options_main_editorial);
        optionsButtonCollecion = toolbar.getRootView().findViewById(R.id.toolbar_options_main_collection);
        optionsButtonFeatured = toolbar.getRootView().findViewById(R.id.toolbar_options_main_featured);
        optionsButtonProfile = toolbar.getRootView().findViewById(R.id.toolbar_options_main_profile);
        optionsButtonEditiorial.setVisibility(View.VISIBLE);
        optionsButtonCollecion.setVisibility(View.GONE);
        optionsButtonFeatured.setVisibility(View.GONE);
        tabLayout.setVisibility(GONE);
        optionsButtonProfile.setVisibility(View.GONE);
        meContainer = findViewById(R.id.me_toolbar_container);
        meContainer.setVisibility(View.GONE);
        searchButton = toolbar.getRootView().findViewById(R.id.toolbar_main_search);
        mCustomViewPager.setOffscreenPageLimit(4);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);
        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_main);
        mLoginHelper = new LoginHelper(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initViews();
        mBottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment f = mMainViewPagerAdapter.getFragment(mCustomViewPager, mCustomViewPager.getCurrentItem(), fm);
                if (item.getItemId() == R.id.nav_editorial) {
                    EditorialFragment editorialFragment = (EditorialFragment) f;
                    editorialFragment.getRecyclerView().smoothScrollToPosition(0);
                } else if (item.getItemId() == R.id.nav_featured) {
                    FeaturedFragment featuredFragment = (FeaturedFragment) f;
                    featuredFragment.getRecyclerView().smoothScrollToPosition(0);
                } else if (item.getItemId() == R.id.nav_collection) {
                    CollectionFragment collectionFragment = (CollectionFragment) f;
                    collectionFragment.getRecyclerView().smoothScrollToPosition(0);
                }
            }
        });
    }

    public static class BottomNavigationViewHelper {
        @SuppressLint("RestrictedApi")
        public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("BNVHelper", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                Log.e("BNVHelper", "Unable to change value of shift mode", e);
            }
        }
    }

    class MainViewPagerAdapter extends FragmentPagerAdapter {

        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new EditorialFragment();
                case 1:
                    return new FeaturedFragment();
                case 2:
                    return new CollectionFragment();
                case 3:
                    return new MeFragment();
                default:
                    return new EditorialFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        public Fragment getFragment(CustomViewPager container, int position, FragmentManager fm) {
            String name = makeFragmentName(container.getId(), position);
            return fm.findFragmentByTag(name);
        }

        private String makeFragmentName(int viewId, int index) {
            return "android:switcher:" + viewId + ":" + index;
        }

    }
}
