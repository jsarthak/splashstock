package com.apps.jsarthak.splashstock.UI.Main;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.apps.jsarthak.splashstock.Data.Adapters.PhotoAdapter;
import com.apps.jsarthak.splashstock.Data.DataProcessor;
import com.apps.jsarthak.splashstock.Data.Photo;
import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.MainActivity;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.Account.LoginActivity;
import com.apps.jsarthak.splashstock.UI.Extra.AboutActivity;
import com.apps.jsarthak.splashstock.UI.Extra.SettingsActivity;
import com.apps.jsarthak.splashstock.Utils.EndlessScrollListener;
import com.apps.jsarthak.splashstock.Utils.NetworkUtils;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeaturedFragment extends Fragment {

    private static final String TAG = FeaturedFragment.class.getSimpleName();
    public RecyclerView mRecyclerView;

    ImageView bottomSheetUserImage;
    TextView bottomSheetName, bottomSheetUserName;
    SharedPreferences mSharedPreferences;
    LinearLayout addAccountContainer, userOptionsContainer;

    NetworkUtils mNetworkUtils;
    LoginHelper mLoginHelper;

    LinearLayout errorLayout;
    TextView errorDetail, errorButton;

    public LinearLayout latestButton, popularButton, oldestButton, settingsButton, aboutButton, collectionContainer, photosMenuContainer;
    public View bottomSheetView;
    View rootView;
    LinearLayoutManager mLayoutManager;
    FrameLayout featuredContainer;
    EndlessScrollListener scrollListener;
    PhotoAdapter photoAdapter;
    ArrayList<Photo> mPhotoList = new ArrayList<>();
    MainActivity mainActivity;
    String mSortMode = "latest";
    BottomSheetDialog bottomSheetDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page = 1;
    private int per_page = 30;
    DataProcessor dataProcessor;


    public FeaturedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_featured, container, false);
        mNetworkUtils = new NetworkUtils(getActivity());
        dataProcessor = new DataProcessor(getActivity());
        initViews();

        loadFeatured(mSortMode);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFeatured(mSortMode);
            }
        });

        latestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortMode = "latest";
                loadFeatured(mSortMode);
                bottomSheetDialog.dismiss();
            }
        });

        popularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortMode = "popular";
                loadFeatured(mSortMode);
                bottomSheetDialog.dismiss();
            }
        });

        oldestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortMode = "oldest";
                loadFeatured(mSortMode);
                bottomSheetDialog.dismiss();
            }
        });

        return rootView;
    }

    void initViews() {
        featuredContainer = rootView.findViewById(R.id.fl_featured_container);
        featuredContainer.setVisibility(View.VISIBLE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLoginHelper = new LoginHelper(getActivity());
        mainActivity = (MainActivity) getActivity();
        mSwipeRefreshLayout = rootView.findViewById(R.id.srl_featured);
        mRecyclerView = rootView.findViewById(R.id.rv_featured);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_featured, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        latestButton = bottomSheetView.getRootView().findViewById(R.id.bs_latest);
        oldestButton = bottomSheetView.getRootView().findViewById(R.id.bs_oldest);
        popularButton = bottomSheetView.getRootView().findViewById(R.id.bs_popular);
        settingsButton = bottomSheetView.getRootView().findViewById(R.id.bs_settings);
        aboutButton = bottomSheetView.getRootView().findViewById(R.id.bs_about);
        collectionContainer = bottomSheetView.getRootView().findViewById(R.id.bs_collections_container);
        photosMenuContainer = bottomSheetView.getRootView().findViewById(R.id.bs_photos_container);

        errorLayout = rootView.findViewById(R.id.error_container);
        errorButton = rootView.findViewById(R.id.tv_error_retry_button);
        errorDetail = rootView.findViewById(R.id.tv_error_detail);
        errorLayout.setVisibility(View.GONE);
        errorButton.setVisibility(View.VISIBLE);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                loadFeatured(mSortMode);
            }
        });

        mainActivity.optionsButtonFeatured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
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
            bottomSheetName.setText(mSharedPreferences.getString(getActivity().getString(R.string.name), ""));
            bottomSheetUserName.setText(mSharedPreferences.getString(getActivity().getString(R.string.username), ""));
            Picasso.get().load(mSharedPreferences.getString(getActivity().getString(R.string.profile_image), "")).fit().centerCrop().transform(new CropCircleTransformation()).into(bottomSheetUserImage);
            userOptionsContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).mCustomViewPager.setCurrentItem(3);
                    bottomSheetDialog.dismiss();
                    ((MainActivity) getActivity()).mBottomNavigationView.setSelectedItemId(R.id.nav_me);

                }
            });
        } else{
            userOptionsContainer.setVisibility(GONE);
            addAccountContainer.setVisibility(View.VISIBLE);
            addAccountContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    //getActivity().finish();
                }
            });
        }
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

        scrollListener = new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page, mSortMode);
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    void loadFeatured(String sort_mode) {
        if (mNetworkUtils.isNetworkAvailable()){
            mSwipeRefreshLayout.setRefreshing(true);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendEncodedPath(getString(R.string.editorial_path))
                    .appendPath(getString(R.string.featured_path))
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.order_by), sort_mode)
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
            String url = builder.build().toString();
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mPhotoList.clear();
                            processData(response);
                            mSwipeRefreshLayout.setRefreshing(false);
                            photoAdapter= new PhotoAdapter(getActivity(), mPhotoList);
                            mRecyclerView.setAdapter(photoAdapter);
                            errorLayout.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "onErrorResponse: " + error);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorDetail.setText("Some error occured");
                    errorButton.setText("RETRY");
                    errorButton.setVisibility(View.VISIBLE);
                    errorButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadFeatured(mSortMode);
                        }
                    });
                    featuredContainer.setVisibility(GONE);
                }
            });
            queue.add(stringRequest);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            featuredContainer.setVisibility(GONE);
            errorDetail.setText("Network error occured.");
            errorButton.setText("RETRY");
            errorButton.setVisibility(View.VISIBLE);
            errorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFeatured(mSortMode);
                }
            });
        }

    }

    void loadMore(int page, String sort_mode) {
        if (mNetworkUtils.isNetworkAvailable()){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendEncodedPath(getString(R.string.editorial_path))
                    .appendPath(getString(R.string.featured_path))
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.order_by), sort_mode)
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
            String url = builder.build().toString();
            RequestQueue queue = Volley.newRequestQueue(getActivity());
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
        } else {
            Toast.makeText(mainActivity, "No Network...", Toast.LENGTH_SHORT).show();
        }

    }


    void processData(String data) {
        try {
            JSONArray jsonElements = new JSONArray(data);
                for (int i = 0; i < jsonElements.length(); i++) {
                    Photo p = dataProcessor.processPhoto(jsonElements.getJSONObject(i));
                    if (!mPhotoList.contains(p)) {
                        mPhotoList.add(p);
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
            errorLayout.setVisibility(View.VISIBLE);
            errorDetail.setText("Some error occured.");
            featuredContainer.setVisibility(GONE);
            errorButton.setVisibility(GONE);
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

}
