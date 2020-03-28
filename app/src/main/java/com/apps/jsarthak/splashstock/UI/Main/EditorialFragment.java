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

public class EditorialFragment extends Fragment {
    private static final String TAG = EditorialFragment.class.getSimpleName();
    public RecyclerView mRecyclerView;
    public LinearLayout latestButton, popularButton, oldestButton, settingsButton, aboutButton, collectionContainer, photosMenuContainer;
    public View bottomSheetView;
    View rootView;
    String editorialResponse = null;
    FrameLayout editorialContainer;
    ImageView bottomSheetUserImage;
    TextView bottomSheetName, bottomSheetUserName;
    SharedPreferences mSharedPreferences;
    LoginHelper mLoginHelper;
    LinearLayout errorLayout;
    TextView errorDetail, errorButton;
    LinearLayout addAccountContainer, userOptionsContainer;
    LinearLayoutManager mLayoutManager;
    EndlessScrollListener scrollListener;
    PhotoAdapter photoAdapter;
    ArrayList<Photo> mPhotoList = new ArrayList<>();
    MainActivity mainActivity;
    String mSortMode = "latest";
    BottomSheetDialog bottomSheetDialog;
    DataProcessor dataProcessor;
    NetworkUtils mNetworkUtils;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page = 1;
    private int per_page = 30;

    public EditorialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_editorial, container, false);
        dataProcessor = new DataProcessor(getActivity());
        mNetworkUtils = new NetworkUtils(getActivity());
        initViews();
        loadEditorial(mSortMode);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEditorial(mSortMode);
            }
        });

        latestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortMode = "latest";
                loadEditorial(getString(R.string.latest));
                bottomSheetDialog.dismiss();
            }
        });

        popularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortMode = "popular";
                loadEditorial(mSortMode);
                bottomSheetDialog.dismiss();
            }
        });

        oldestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortMode = "oldest";
                loadEditorial(mSortMode);
                bottomSheetDialog.dismiss();
            }
        });

        return rootView;
    }

    void initViews() {
        editorialContainer = rootView.findViewById(R.id.fl_editorial_container);
        editorialContainer.setVisibility(View.VISIBLE);
        mLoginHelper = new LoginHelper(getActivity());
        mainActivity = (MainActivity) getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSwipeRefreshLayout = rootView.findViewById(R.id.srl_editorial);
        mRecyclerView = rootView.findViewById(R.id.rv_editorial);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_editorial, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        errorLayout = rootView.findViewById(R.id.error_container);
        errorButton = rootView.findViewById(R.id.tv_error_retry_button);
        errorDetail = rootView.findViewById(R.id.tv_error_detail);
        errorLayout.setVisibility(GONE);
        errorButton.setText("RETRY");
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEditorial(mSortMode);
            }
        });
        latestButton = bottomSheetView.getRootView().findViewById(R.id.bs_latest);
        oldestButton = bottomSheetView.getRootView().findViewById(R.id.bs_oldest);
        popularButton = bottomSheetView.getRootView().findViewById(R.id.bs_popular);
        settingsButton = bottomSheetView.getRootView().findViewById(R.id.bs_settings);
        aboutButton = bottomSheetView.getRootView().findViewById(R.id.bs_about);
        addAccountContainer = bottomSheetView.getRootView().findViewById(R.id.bottom_sheet_add_account);
        userOptionsContainer = bottomSheetView.getRootView().findViewById(R.id.bottom_sheet_user_container);

        if (mLoginHelper.isUserLogged()) {
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

        } else {
            userOptionsContainer.setVisibility(GONE);
            addAccountContainer.setVisibility(View.VISIBLE);
            addAccountContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    bottomSheetDialog.dismiss();
                    startActivity(intent);
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

        mainActivity.optionsButtonEditiorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        scrollListener = new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page, mSortMode);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    void loadMore(int page, String sort_mode) {
        if (mNetworkUtils.isNetworkAvailable()) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath(getString(R.string.editorial_path))
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                    .appendQueryParameter(getString(R.string.order_by), sort_mode)
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
            String url = builder.build().toString();
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            editorialResponse = response;
                            processData(editorialResponse);
                            photoAdapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);

                }
            });
            queue.add(stringRequest);
        } else {
            Toast.makeText(mainActivity, "No Network...", Toast.LENGTH_SHORT).show();
        }
    }

    void loadEditorial(String sort_mode) {
        if (mNetworkUtils.isNetworkAvailable()) {
            mSwipeRefreshLayout.setRefreshing(true);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath(getString(R.string.editorial_path))
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                    .appendQueryParameter(getString(R.string.order_by), sort_mode)
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
            String url = builder.build().toString();
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            editorialResponse = response;
                            mPhotoList.clear();
                            processData(editorialResponse);
                            mSwipeRefreshLayout.setRefreshing(false);
                            photoAdapter = new PhotoAdapter(getActivity(), mPhotoList);
                            mRecyclerView.setAdapter(photoAdapter);
                            errorLayout.setVisibility(GONE);
                            editorialContainer.setVisibility(View.VISIBLE);
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
                            loadEditorial(mSortMode);
                        }
                    });
                    editorialContainer.setVisibility(GONE);
                }
            });
            queue.add(stringRequest);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            editorialContainer.setVisibility(GONE);
            errorDetail.setText("Network error occured.");
            errorButton.setText("RETRY");
            errorButton.setVisibility(View.VISIBLE);
            errorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadEditorial(mSortMode);
                }
            });
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
            errorLayout.setVisibility(View.VISIBLE);
            errorDetail.setText("Some error occured.");
            editorialContainer.setVisibility(GONE);
            errorButton.setVisibility(GONE);
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}

