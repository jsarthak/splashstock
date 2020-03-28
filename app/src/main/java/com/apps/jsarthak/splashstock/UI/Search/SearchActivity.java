package com.apps.jsarthak.splashstock.UI.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jsarthak.splashstock.Data.Photo;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    Utils mUtils;

    public ImageView clearButton, backButton;
    public EditText searchEditText;

    private ViewPager mViewPager;
    public TabLayout tabLayout;

    String searchText;


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        FragmentTransaction transactionPhoto = getSupportFragmentManager().beginTransaction();
        FragmentTransaction transactionCollection = getSupportFragmentManager().beginTransaction();
        FragmentTransaction transactionUser = getSupportFragmentManager().beginTransaction();

        String text = searchEditText.getText().toString();
        if (!text.equals("")) {
            transactionPhoto.replace(R.id.photo_search_container, PhotoSearchFragment.newInstance(text)).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            transactionCollection.replace(R.id.collection_search_container, CollectionSearchFragment.newInstance(text)).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            transactionUser.replace(R.id.user_search_container, UserSearchFragment.newInstance(text)).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);
        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clearButton = toolbar.getRootView().findViewById(R.id.iv_toolbar_search_clear);
        backButton = toolbar.getRootView().findViewById(R.id.iv_toolbar_search_back);
        clearButton = toolbar.getRootView().findViewById(R.id.iv_toolbar_search_clear);
        searchEditText = toolbar.getRootView().findViewById(R.id.et_search_text);
        searchEditText.setOnEditorActionListener(this);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                clearButton.setVisibility(View.VISIBLE);
                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchEditText.setText("");
                    }
                });
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(PhotoSearchFragment.newInstance(null), "Photos");
        mSectionsPagerAdapter.addFragment(CollectionSearchFragment.newInstance(null), "Collections");
        mSectionsPagerAdapter.addFragment(UserSearchFragment.newInstance(null), "Users");

        mViewPager = (ViewPager) findViewById(R.id.vp_search);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

         tabLayout =  findViewById(R.id.tabs_search);
        tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();
        if (intent.hasExtra("query")){
            searchEditText.setText(intent.getStringExtra("query"));
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return fragmentTitleList.get(position);
        }
    }

}
