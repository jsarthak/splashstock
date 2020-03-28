package com.apps.jsarthak.splashstock.UI.Main;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.apps.jsarthak.splashstock.Data.Adapters.CollectionAdapter;
import com.apps.jsarthak.splashstock.Data.Collection;
import com.apps.jsarthak.splashstock.Data.DataProcessor;
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
public class CollectionFragment extends Fragment {


    private static final String TAG = CollectionFragment.class.getSimpleName();
    public View bottomSheetView;
    NetworkUtils mNetworkUtils;

    FrameLayout collectionContainer;
    ProgressDialog progressDialog;
    ImageView bottomSheetUserImage;
    TextView bottomSheetName, bottomSheetUserName;
    SharedPreferences mSharedPreferences;
    LinearLayout addAccountContainer, userOptionsContainer;


    LoginHelper mLoginHelper;
    View rootView;
    int page = 1, per_page = 30;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager mLayoutManager;
    EndlessScrollListener scrollListener;
    LinearLayout curatedButton, featuredButton, settingsButton, aboutButton;
    MainActivity mainActivity;
    String mSortMode = "featured";
    BottomSheetDialog bottomSheetDialog;
    CollectionAdapter collectionAdapter;

    FloatingActionButton fab;

    LinearLayout errorLayout;
    TextView errorDetail, errorButton;

    DataProcessor dataProcessor;

    ArrayList<Collection> collectionArrayList = new ArrayList<>();


    public CollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        dataProcessor = new DataProcessor(getActivity());
        mNetworkUtils = new NetworkUtils(getActivity());
        initViews();
        loadCollections(mSortMode);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCollections(mSortMode);
            }
        });

        featuredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortMode = "featured";
                loadCollections(mSortMode);
                bottomSheetDialog.dismiss();
            }
        });

        curatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortMode = "curated";
                loadCollections(mSortMode);
                bottomSheetDialog.dismiss();
            }
        });

        return rootView;
    }


    void initViews() {

        progressDialog = new ProgressDialog(getActivity());
        collectionContainer = rootView.findViewById(R.id.fl_collection_container);
        collectionContainer.setVisibility(View.VISIBLE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLoginHelper = new LoginHelper(getActivity());
        mainActivity = (MainActivity) getActivity();
        fab = rootView.findViewById(R.id.fab_create_collection);
        mSwipeRefreshLayout = rootView.findViewById(R.id.srl_collections);
        mRecyclerView = rootView.findViewById(R.id.rv_collections);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_collection, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        curatedButton = bottomSheetView.getRootView().findViewById(R.id.bs_collection_curated);
        featuredButton = bottomSheetView.getRootView().findViewById(R.id.bs_featured);
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


        mainActivity.optionsButtonCollecion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        errorLayout = rootView.findViewById(R.id.error_container);
        errorButton = rootView.findViewById(R.id.tv_error_retry_button);
        errorDetail = rootView.findViewById(R.id.tv_error_detail);
        errorLayout.setVisibility(View.GONE);
        errorButton.setText("RETRY");
        errorButton.setVisibility(View.VISIBLE);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                loadCollections(mSortMode);
            }
        });

        scrollListener = new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page, mSortMode);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        if (fab.getVisibility() == View.VISIBLE && mLoginHelper.isUserLogged()) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View messageView = getLayoutInflater().inflate(R.layout.new_collection_layout, null, false);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View messageRootView = messageView.getRootView();
                    builder.setTitle("New collection");
                    builder.setIcon(R.drawable.ic_plus);
                    final EditText titleET = messageRootView.findViewById(R.id.et_new_collection_title);
                    final EditText descET = messageRootView.findViewById(R.id.et_new_collection_desc);
                    final CheckBox isPrivateCB = messageRootView.findViewById(R.id.cb_new_collection_private);
                    builder.setCancelable(true);
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String cTitle = titleET.getText().toString();
                            String cDesc = descET.getText().toString();
                            boolean cPrivate = isPrivateCB.isChecked();

                            if (cTitle.isEmpty()) {
                                titleET.setError("Title is required");
                            } else {
                                if (mNetworkUtils.isNetworkAvailable()) {
                                    Uri.Builder builder = new Uri.Builder();
                                    builder.scheme("https")
                                            .authority(getString(R.string.base_url))
                                            .appendPath("collections")
                                            .appendQueryParameter(getActivity().getString(R.string.title), cTitle)
                                            .appendQueryParameter(getActivity().getString(R.string.description), cDesc)
                                            .appendQueryParameter(getActivity().getString(R.string.isprivate), String.valueOf(cPrivate))
                                            .appendQueryParameter(getActivity().getString(R.string.access_token), mSharedPreferences.getString(getString(R.string.access_token), ""));
                                    String url = builder.build().toString();

                                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                                    StringRequest request = new StringRequest(Request.Method.POST, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Toast.makeText(getActivity(), "Collection created", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(TAG, "onErrorResponse: ");
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    queue.add(request);
                                }

                            }
                        }
                    });
                    builder.setView(messageView);
                    builder.create();
                    builder.show();
                }
            });

        } else if (fab.getVisibility() == View.VISIBLE && !mLoginHelper.isUserLogged()) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Login first", Snackbar.LENGTH_SHORT).setAction("LOGIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                        }
                    }).show();
                }
            });
        }
    }

    void loadMore(int page, String sort_mode) {

        if (mNetworkUtils.isNetworkAvailable()) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath(getString(R.string.collections_path))
                    .appendPath(sort_mode)
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
            String url = builder.build().toString();
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            processData(response);
                            collectionAdapter.notifyDataSetChanged();
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

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    void loadCollections(String sort_mode) {
        if (mNetworkUtils.isNetworkAvailable()) {
            mSwipeRefreshLayout.setRefreshing(true);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath(getString(R.string.collections_path))
                    .appendPath(sort_mode)
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
            String url = builder.build().toString();
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            collectionArrayList.clear();
                            processData(response);
                            mSwipeRefreshLayout.setRefreshing(false);
                            collectionAdapter = new CollectionAdapter(getActivity(), collectionArrayList);
                            mRecyclerView.setAdapter(collectionAdapter);
                            errorLayout.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "onErrorResponse: " + error);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorDetail.setText("Some error occured");
                    errorButton.setText("RETRY");
                    errorButton.setVisibility(View.VISIBLE);
                    errorButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadCollections(mSortMode);
                        }
                    });
                    collectionContainer.setVisibility(GONE);
                }
            });
            queue.add(stringRequest);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            collectionContainer.setVisibility(GONE);
            errorDetail.setText("Network error occured.");
            errorButton.setText("RETRY");
            errorButton.setVisibility(View.VISIBLE);
            errorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadCollections(mSortMode);
                }
            });
        }

    }

    void processData(String data) {
        try {
            JSONArray jsonElements = new JSONArray(data);
            for (int i = 0; i < jsonElements.length(); i++) {
                Collection c = dataProcessor.processCollection(jsonElements.getJSONObject(i));
                if (!collectionArrayList.contains(c)) {
                    collectionArrayList.add(c);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
            errorLayout.setVisibility(View.VISIBLE);
            errorDetail.setText("Some error occured.");
            collectionContainer.setVisibility(GONE);
            errorButton.setVisibility(GONE);
        }
    }

}
