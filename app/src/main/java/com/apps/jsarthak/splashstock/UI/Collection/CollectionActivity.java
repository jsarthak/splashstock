package com.apps.jsarthak.splashstock.UI.Collection;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.apps.jsarthak.splashstock.Data.Adapters.CollectionAdapter;
import com.apps.jsarthak.splashstock.Data.Adapters.PhotoAdapter;
import com.apps.jsarthak.splashstock.Data.Adapters.TagAdapter;
import com.apps.jsarthak.splashstock.Data.Collection;
import com.apps.jsarthak.splashstock.Data.DataProcessor;
import com.apps.jsarthak.splashstock.Data.Photo;
import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.Account.EditProfileActivity;
import com.apps.jsarthak.splashstock.UI.Account.LoginActivity;
import com.apps.jsarthak.splashstock.UI.Extra.AboutActivity;
import com.apps.jsarthak.splashstock.UI.Extra.SettingsActivity;
import com.apps.jsarthak.splashstock.UI.Profile.ProfileActivity;
import com.apps.jsarthak.splashstock.Utils.EndlessScrollListener;
import com.apps.jsarthak.splashstock.Utils.Utils;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class CollectionActivity extends AppCompatActivity {
    private static final String TAG = CollectionActivity.class.getSimpleName();

    public View bottomSheetView;
    RecyclerView mRecyclerView, tagsRecycler;
    FrameLayout container;
    int page = 1, per_page = 20;
    SwipeRefreshLayout mSwipeRefreshLayout;
    CollapsingToolbarLayout toolbar;
    Utils mUtils;
    ImageView bottomSheetUserImage;
    TextView bottomSheetName, bottomSheetUserName;
    SharedPreferences mSharedPreferences;
    LinearLayout addAccountContainer, userOptionsContainer;
    LoginHelper mLoginHelper;
    DataProcessor dataProcessor;
    long id;

    LinearLayout shareButton, openButton, settingsButton, aboutButton;
    ArrayList<Photo> collectionArrayList = new ArrayList<>();
    PhotoAdapter photoAdapter;
    EndlessScrollListener endlessScrollListener;
    LinearLayoutManager mLayoutManager;
    LinearLayout userContainer;
    BottomSheetDialog bottomSheetDialog;

    TextView collectionTitle, collectionDescription, collectionCreatorName, collectionCreatorUserName, photoCount, updatedAt, publishedAt;
    ImageView collectionCover, userImage, backButton, optionsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);

        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_collection);
        dataProcessor = new DataProcessor(this);
        initViews();
        id = getIntent().getLongExtra(getString(R.string.collection_id), 0);

        loadCollectionInfo(id);
        loadCollections();



        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCollections();
            }
        });

        endlessScrollListener = new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page);
            }
        };
        //Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(endlessScrollListener);
    }

    void initViews() {
        mLoginHelper = new LoginHelper(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        container = findViewById(R.id.fl_collections_container_activity);
        toolbar = findViewById(R.id.toolbar);
        View toolbarView = toolbar.getRootView();

        backButton = toolbarView.findViewById(R.id.iv_toolbar_collection_back);
        optionsButton = toolbarView.findViewById(R.id.iv_toolbar_collection_more);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_collection_activity, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        collectionCover = toolbarView.findViewById(R.id.iv_collection_toolbar_cover);
        collectionTitle = toolbarView.findViewById(R.id.tv_toolbar_collection_title);
        tagsRecycler = toolbarView.findViewById(R.id.rv_collection_toolbar_tags);
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        collectionDescription = toolbarView.findViewById(R.id.tv_toolbar_collection_description);
        collectionCreatorName = toolbarView.findViewById(R.id.tv_toolbar_collection_name);
        collectionCreatorUserName = toolbarView.findViewById(R.id.tv_toolbar_collection_username);
        updatedAt = toolbarView.findViewById(R.id.tv_collection_updated_at);
        publishedAt = toolbarView.findViewById(R.id.tv_collection_published_at);
        photoCount = toolbarView.findViewById(R.id.tv_toolbar_collection_photo_count);
        userImage = toolbarView.findViewById(R.id.iv_toolbar_collection_user);
        mRecyclerView = findViewById(R.id.rv_collentions_main);
        mSwipeRefreshLayout = findViewById(R.id.srl_collections_main);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        userContainer = toolbarView.findViewById(R.id.ll_toolbar_collection_by);

        shareButton = bottomSheetView.getRootView().findViewById(R.id.bs_collection_share);
        openButton = bottomSheetView.getRootView().findViewById(R.id.bs_collection_open_in_browser);
        settingsButton = bottomSheetView.getRootView().findViewById(R.id.bs_settings);
        aboutButton = bottomSheetView.getRootView().findViewById(R.id.bs_about);

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CollectionActivity.this, SettingsActivity.class));
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
            bottomSheetName.setText(mSharedPreferences.getString(CollectionActivity.this.getString(R.string.name), ""));
            bottomSheetUserName.setText(mSharedPreferences.getString(CollectionActivity.this.getString(R.string.username), ""));
            Picasso.get().load(mSharedPreferences.getString(CollectionActivity.this.getString(R.string.profile_image), "")).fit().centerCrop().transform(new CropCircleTransformation()).into(bottomSheetUserImage);
        } else{
            userOptionsContainer.setVisibility(GONE);
            addAccountContainer.setVisibility(View.VISIBLE);
            addAccountContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CollectionActivity.this, LoginActivity.class);
                    bottomSheetDialog.dismiss();
                    startActivity(intent);
                }
            });
        }

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CollectionActivity.this, AboutActivity.class));
                bottomSheetDialog.dismiss();
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) container.getLayoutParams();
       /* AppBarLayout.ScrollingViewBehavior scrollingViewBehavior = (AppBarLayout.ScrollingViewBehavior) params.getBehavior();
        scrollingViewBehavior.setOverlayTop(128);*/
        final LinearLayout infoContainer = findViewById(R.id.collection_toolbar_info_container);
        AppBarLayout appBarLayout = findViewById(R.id.appbar_collection);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    infoContainer.setVisibility(View.INVISIBLE);
                } else if (verticalOffset < appBarLayout.getTotalScrollRange() / 2) {
                    infoContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && infoContainer.getVisibility() == View.VISIBLE){
                    infoContainer.setVisibility(View.INVISIBLE);
                } else if (dy < 0 && infoContainer.getVisibility() != View.VISIBLE){
                    infoContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    void loadCollections() {
        mSwipeRefreshLayout.setRefreshing(true);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath(getString(R.string.collections_path))
                .appendPath(String.valueOf(id))
                .appendPath("photos")
                .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(CollectionActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        collectionArrayList.clear();
                        processData(response);
                        mSwipeRefreshLayout.setRefreshing(false);
                        photoAdapter = new PhotoAdapter(CollectionActivity.this, collectionArrayList);
                        mRecyclerView.setAdapter(photoAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });
        queue.add(stringRequest);
    }

    void loadCollectionInfo(long id) {
        toolbar.setVisibility(View.INVISIBLE);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath(getString(R.string.collections_path))
                .appendPath(String.valueOf(id))
                .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(CollectionActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processCollectionData(response);
                        toolbar.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });
        queue.add(stringRequest);
    }

    void loadMore(int page) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath(getString(R.string.collections_path))
                .appendPath(String.valueOf(id))
                .appendPath("photos")
                .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
        String url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue((CollectionActivity.this));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processData(response);
                        photoAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });
        queue.add(stringRequest);
    }

    void processCollectionData(String data) {
        try {
            final JSONObject collection = new JSONObject(data);
            final Collection c = dataProcessor.processCollection(collection);
            collectionTitle.setText(c.title);
            if (c.description.equals("null")){
                collectionDescription.setVisibility(GONE);
            } else {
                collectionDescription.setVisibility(View.VISIBLE);
                collectionDescription.setText(c.description);
            }
            Picasso.get().load(c.photo.urls.regular).into(collectionCover);
            Picasso.get().load(c.photo.user.profileImage.medium).fit().centerCrop().transform(new CropCircleTransformation()).into(userImage);
            collectionCreatorName.setText(c.photo.user.name);
            collectionCreatorUserName.setText(c.photo.user.userName);
            photoCount.setText(c.totalPhotos + " Photos");
            updatedAt.setText(c.updatedAt);
            publishedAt.setText(c.publishedAt);
            TagAdapter tagsAdapter = new TagAdapter(CollectionActivity.this, c.tags);
            tagsRecycler.setAdapter(tagsAdapter);
            userContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startProfileActivity = new Intent(CollectionActivity.this, ProfileActivity.class);
                    startProfileActivity.putExtra(getString(R.string.username), c.photo.user.userName);
                    startActivity(startProfileActivity);
                }
            });
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "https://unsplash.com/collections/"+c.id);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    bottomSheetDialog.dismiss();

                }
            });

            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriBrowser = Uri.parse("https://unsplash.com/collections/"+c.id);
                    Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                    feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try{
                        startActivity(feedback);
                    } catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/collections/"+c.id)));
                    }
                    bottomSheetDialog.dismiss();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void processData(String data) {
        try {
            JSONArray jsonElements = new JSONArray(data);
            for (int i = 0; i < jsonElements.length(); i++) {
                Photo p = dataProcessor.processPhoto(jsonElements.getJSONObject(i));
                if (!collectionArrayList.contains(p)) {
                    collectionArrayList.add(p);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
