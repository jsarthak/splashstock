package com.apps.jsarthak.splashstock.UI.Main.Me;


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
import com.apps.jsarthak.splashstock.Data.Adapters.CollectionAdapter;
import com.apps.jsarthak.splashstock.Data.Collection;
import com.apps.jsarthak.splashstock.Data.DataProcessor;
import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.MainActivity;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.Utils.EndlessScrollListener;
import com.apps.jsarthak.splashstock.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeCollectionFragment extends Fragment {

    private static final String TAG = MeCollectionFragment.class.getSimpleName();

    int page = 1, per_page = 30;
    CollectionAdapter collectionAdapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    DataProcessor dataProcessor;
    View rootView;

    MainActivity mainActivity;
    EndlessScrollListener scrollListener;

    SharedPreferences mSharedPreferences;
    String mUserName;
    ArrayList<Collection> collectionArrayList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;

    LoginHelper mLoginHelper;


    NetworkUtils mNetworkUtils;
    LinearLayout errorLayout;
    TextView errorDetail, errorButton;
    public MeCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
// Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_me_likes, container, false);
        dataProcessor = new DataProcessor(getActivity());
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mNetworkUtils = new NetworkUtils(getActivity());
        initViews();
        mLoginHelper = new LoginHelper(getActivity());
        if (mLoginHelper.isUserLogged()){
            mUserName =mSharedPreferences.getString(getString(R.string.username), "");
            loadCollections(mUserName);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadCollections(mUserName);
                }
            });
        }
        return rootView;    }


    void initViews(){
        mainActivity = (MainActivity)getActivity();
        mRecyclerView = rootView.findViewById(R.id.rv_common);
        mSwipeRefreshLayout = rootView.findViewById(R.id.srl_common);
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
                loadCollections(mUserName);
            }
        });

        scrollListener = new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page, mUserName);
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    void loadMore(int page, String username) {
        if (mNetworkUtils.isNetworkAvailable()){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath("users").appendPath(username)
                    .appendPath(getString(R.string.collections_path))
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                    .appendQueryParameter(getString(R.string.access_token), mSharedPreferences.getString(getString(R.string.access_token), null))
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
        }

    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    void loadCollections(String username) {

        if (mNetworkUtils.isNetworkAvailable()){
            mSwipeRefreshLayout.setRefreshing(true);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath("users").appendPath(username)
                    .appendPath(getString(R.string.collections_path))
                    .appendQueryParameter(getString(R.string.page), String.valueOf(page))
                    .appendQueryParameter(getString(R.string.per_page), String.valueOf(per_page))
                    .appendQueryParameter(getString(R.string.access_token), mSharedPreferences.getString(getString(R.string.access_token), null))
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
                            if (collectionArrayList.isEmpty() || collectionArrayList.size() ==0){
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
                    errorLayout.setVisibility(GONE);
                    Log.d(TAG, "onErrorResponse: " + error);
                }
            });
            queue.add(stringRequest);
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
        }
    }

}
