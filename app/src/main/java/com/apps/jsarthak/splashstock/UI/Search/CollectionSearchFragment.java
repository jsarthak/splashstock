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
import com.apps.jsarthak.splashstock.Data.Adapters.CollectionAdapter;
import com.apps.jsarthak.splashstock.Data.Collection;
import com.apps.jsarthak.splashstock.Data.DataProcessor;
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
public class CollectionSearchFragment extends Fragment {
    private static final String TAG = CollectionSearchFragment.class.getSimpleName();

    View rootView;
    int page = 1, per_page = 30;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager mLayoutManager;
    EndlessScrollListener scrollListener;
    FrameLayout searchContainer;

    LinearLayout errorLayout;
    int totalResults, totalPages;
    TextView errorDetail, errorButton;

    CollectionAdapter collectionAdapter;
    SearchActivity searchActivity;
    NetworkUtils mNetworkUtils;
    String s;

    DataProcessor dataProcessor;

    ArrayList<Collection> collectionArrayList = new ArrayList<>();

    public static CollectionSearchFragment newInstance(String query) {
        CollectionSearchFragment userFragment = new CollectionSearchFragment();

        Bundle args = new Bundle();
        args.putString("query", query);
        userFragment.setArguments(args);

        return userFragment;
    }


    public CollectionSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent().hasExtra("query")){
            s = getArguments().getString("query", getActivity().getIntent().getStringExtra("query"));
        } else{
            s = getArguments().getString("query", null);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search_collection, container, false);
        dataProcessor = new DataProcessor(getActivity());
        mNetworkUtils = new NetworkUtils(getActivity());
        initViews();

        loadCollections(s);
        return rootView;
    }
    void initViews(){
        searchContainer = rootView.findViewById(R.id.fl_search_collection_container);
        searchContainer.setVisibility(View.VISIBLE);
        searchActivity = (SearchActivity)getActivity();
        mSwipeRefreshLayout = rootView.findViewById(R.id.srl_common);
        mRecyclerView = rootView.findViewById(R.id.rv_common);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        scrollListener = new EndlessScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page, s);
            }
        };

        errorLayout = rootView.findViewById(R.id.error_container);
        errorButton = rootView.findViewById(R.id.tv_error_retry_button);
        errorDetail = rootView.findViewById(R.id.tv_error_detail);
        errorDetail.setText("No results found...");
        errorLayout.setVisibility(View.GONE);
        errorButton.setVisibility(GONE);

        //Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    void loadMore(int page, String search) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.base_url))
                .appendPath("search")
                .appendPath(getString(R.string.collections_path))
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


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void loadCollections(final String search) {
        if (search == null){
            return;
        }
        if (mNetworkUtils.isNetworkAvailable()){
            mSwipeRefreshLayout.setRefreshing(true);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath("search")
                    .appendPath(getString(R.string.collections_path))
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
                            collectionArrayList.clear();
                            processData(response);
                            mSwipeRefreshLayout.setRefreshing(false);
                            collectionAdapter = new CollectionAdapter(getActivity(), collectionArrayList);
                            mRecyclerView.setAdapter(collectionAdapter);
                            searchContainer.setVisibility(View.VISIBLE);
                            errorLayout.setVisibility(GONE);
                            searchActivity.tabLayout.getTabAt(1).setText(totalResults + " Collections");

                            if (collectionArrayList.isEmpty() || collectionArrayList.size() == 0) {
                                errorLayout.setVisibility(View.VISIBLE);
                                errorButton.setVisibility(GONE);
                                errorDetail.setText("Nothing here...");
                                mSwipeRefreshLayout.setVisibility(GONE);
                                mRecyclerView.setVisibility(GONE);


                        } else

                        {
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
        }else{
            errorLayout.setVisibility(View.VISIBLE);
            searchContainer.setVisibility(GONE);
            errorDetail.setText("Network error occured.");
            errorButton.setText("RETRY");
            errorButton.setVisibility(View.VISIBLE);
            errorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadCollections(search);
                }
            });
        }


    }

    void processData(String data) {
        try {
            JSONObject searchObject = new JSONObject(data);
             totalResults = searchObject.getInt("total");
             totalPages = searchObject.getInt("total_pages");
            if(totalResults>0) {
                JSONArray jsonElements = searchObject.getJSONArray("results");
                for (int i = 0; i < jsonElements.length(); i++) {
                    Collection c = dataProcessor.processCollection(jsonElements.getJSONObject(i));
                    if (!collectionArrayList.contains(c)) {
                        collectionArrayList.add(c);
                    }
                }
            }

            else if (totalResults == 0){
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
