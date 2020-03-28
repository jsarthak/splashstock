package com.apps.jsarthak.splashstock.UI.Search;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.jsarthak.splashstock.Data.Adapters.PhotoAdapter;
import com.apps.jsarthak.splashstock.Data.DataProcessor;
import com.apps.jsarthak.splashstock.Data.Photo;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.Utils.EndlessScrollListener;
import com.apps.jsarthak.splashstock.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoSearchFragment extends Fragment {

    private static final String TAG = PhotoSearchFragment.class.getSimpleName();
    View rootView;
    String editorialResponse = null;
    NetworkUtils mNetworkUtils;
    LinearLayoutManager mLayoutManager;
    EndlessScrollListener scrollListener;
    PhotoAdapter photoAdapter;
    ArrayList<Photo> mPhotoList = new ArrayList<>();
    public RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page = 1;
    private int per_page = 30;
    SearchActivity searchActivity;
    String s;
    DataProcessor dataProcessor;
    int totalResults, totalPages;
    FrameLayout searchContainer;


    LinearLayout errorLayout;
    TextView errorDetail, errorButton;
    public PhotoSearchFragment() {
        // Required empty public constructor
    }

    public static PhotoSearchFragment newInstance(String query) {
        PhotoSearchFragment userFragment = new PhotoSearchFragment();

        Bundle args = new Bundle();
        args.putString("query", query);
        userFragment.setArguments(args);

        return userFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent().hasExtra("query")) {
            s = getArguments().getString("query", getActivity().getIntent().getStringExtra("query"));
        } else {
            s = getArguments().getString("query", null);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_photo_search, container, false);
        dataProcessor = new DataProcessor(getActivity());
        mNetworkUtils = new NetworkUtils(getActivity());
        initViews();
        loadPhotos(s);
        return rootView;
    }


    void initViews() {
        searchContainer = rootView.findViewById(R.id.fl_photo_search_container);
        searchContainer.setVisibility(View.VISIBLE);
        searchActivity = (SearchActivity) getActivity();
        mSwipeRefreshLayout = rootView.findViewById(R.id.srl_common);
        mRecyclerView = rootView.findViewById(R.id.rv_common);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        errorLayout = rootView.findViewById(R.id.error_container);
        errorButton = rootView.findViewById(R.id.tv_error_retry_button);
        errorDetail = rootView.findViewById(R.id.tv_error_detail);
        errorLayout.setVisibility(GONE);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(GONE);
                loadPhotos(s);
            }
        });
        scrollListener = new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page, s);
            }
        };
        //Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    void loadMore(int page, String search) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("search")
                .appendPath("photos")
                .appendQueryParameter("query", search)
                .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
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
                        errorLayout.setVisibility(GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });
        queue.add(stringRequest);
    }

    void loadPhotos(final String search) {
        if (search == null){
            return;
        }
        if (mNetworkUtils.isNetworkAvailable()){
            mSwipeRefreshLayout.setRefreshing(true);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath("search")
                    .appendPath("photos")
                    .appendQueryParameter("query", search)
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
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
                            searchContainer.setVisibility(View.VISIBLE);
                            searchActivity.tabLayout.getTabAt(0).setText(totalResults+" Photos");
                            if (mPhotoList.isEmpty() || mPhotoList.size() ==0){
                                errorLayout.setVisibility(View.VISIBLE);
                                errorButton.setVisibility(GONE);
                                errorDetail.setText("Nothing here...");
                                mSwipeRefreshLayout.setVisibility(GONE);
                                mRecyclerView.setVisibility(GONE);
                            } else {
                                errorLayout.setVisibility(GONE);
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.VISIBLE);

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "onErrorResponse: " + error);
                    errorLayout.setVisibility(View.VISIBLE);
                    searchContainer.setVisibility(GONE);
                    errorButton.setVisibility(View.VISIBLE);
                    errorDetail.setText("Some error occured.");
                    errorButton.setVisibility(GONE);
                }
            });
            queue.add(stringRequest);
        } else{
            errorLayout.setVisibility(View.VISIBLE);
            searchContainer.setVisibility(GONE);
            errorDetail.setText("Network error occured.");
            errorButton.setText("RETRY");
            errorButton.setVisibility(View.VISIBLE);
            errorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadPhotos(search);
                }
            });
        }

    }

    void processData(String data) {
        try {
            JSONObject searchObject = new JSONObject(data);
            totalResults = searchObject.getInt("total");
            totalPages = searchObject.getInt("total_pages");
            Log.d(TAG, "processData: " + totalResults);

            if(totalResults != 0){
                JSONArray jsonElements = searchObject.getJSONArray("results");
                for (int i = 0; i < jsonElements.length(); i++) {
                    Photo p = dataProcessor.processPhoto(jsonElements.getJSONObject(i));
                    if (!mPhotoList.contains(p)){
                        mPhotoList.add(p);
                    }
                }
            } else{
                errorLayout.setVisibility(View.VISIBLE);
                searchContainer.setVisibility(GONE);
                errorButton.setVisibility(GONE);
                errorDetail.setText("No results found!!!");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
