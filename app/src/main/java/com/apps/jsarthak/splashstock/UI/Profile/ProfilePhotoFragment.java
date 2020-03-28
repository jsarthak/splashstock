package com.apps.jsarthak.splashstock.UI.Profile;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.Utils.EndlessScrollListener;

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
public class ProfilePhotoFragment extends Fragment {


    private static final String TAG = ProfilePhotoFragment.class.getSimpleName();

    int page = 1, per_page = 30;
    PhotoAdapter photoAdapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    DataProcessor dataProcessor;
    View rootView;

    EndlessScrollListener scrollListener;

    SharedPreferences mSharedPreferences;
    String mUserName;
    ArrayList<Photo> mPhotoList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;

    LoginHelper mLoginHelper;


    LinearLayout errorLayout;
    TextView errorDetail, errorButton;
    public ProfilePhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_profile_photo, container, false);
        dataProcessor = new DataProcessor(getActivity());
        initViews();
        mLoginHelper = new LoginHelper(getActivity());
        if (getActivity().getIntent().hasExtra(getString(R.string.username))){
            mUserName = getActivity().getIntent().getStringExtra(getActivity().getString(R.string.username));
            loadPhotos(mUserName);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadPhotos(mUserName);
                }
            });
        }
        return rootView;
    }

    void initViews(){
        mRecyclerView = rootView.findViewById(R.id.rv_common);
        mSwipeRefreshLayout = rootView.findViewById(R.id.srl_common);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        scrollListener = new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page, mUserName);
            }
        };
        errorLayout = rootView.findViewById(R.id.error_container);
        errorButton = rootView.findViewById(R.id.tv_error_retry_button);
        errorDetail = rootView.findViewById(R.id.tv_error_detail);
        errorLayout.setVisibility(GONE);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(GONE);
                loadPhotos(mUserName);
            }
        });
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    void loadMore(int page, String username) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("users").appendPath(username)
                .appendPath(getString(R.string.editorial_path))
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
                        photoAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });
        queue.add(stringRequest);
    }

    void loadPhotos(String username) {
        mSwipeRefreshLayout.setRefreshing(true);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("users").appendPath(username)
                .appendPath(getString(R.string.editorial_path))
                .appendQueryParameter(getString(R.string.page), String.valueOf(page))
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
                        photoAdapter = new PhotoAdapter(getActivity(), mPhotoList);
                        mRecyclerView.setAdapter(photoAdapter);
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
            }
        });
        queue.add(stringRequest);
    }

    void processData(String data) {
        try {

            JSONArray jsonElements = new JSONArray(data);
            for (int i = 0; i < jsonElements.length(); i++) {
                Photo p = dataProcessor.processPhoto(jsonElements.getJSONObject(i));

                if (!mPhotoList.contains(p)){
                    mPhotoList.add(p);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }

}
