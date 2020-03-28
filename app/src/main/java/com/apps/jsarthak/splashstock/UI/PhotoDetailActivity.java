package com.apps.jsarthak.splashstock.UI;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.jsarthak.splashstock.Data.DataProcessor;
import com.apps.jsarthak.splashstock.Data.Photo;
import com.apps.jsarthak.splashstock.Helper.LoginHelper;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.Account.LoginActivity;
import com.apps.jsarthak.splashstock.UI.Profile.ProfileActivity;
import com.apps.jsarthak.splashstock.Utils.Utils;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = PhotoDetailActivity.class.getSimpleName();

    SharedPreferences mSharedPreferences;

    Photo mPhoto;

    Intent intent;

    ImageView backButton;

    Toolbar toolbar;

    DataProcessor dataProcessor;

    LoginHelper mLoginHelper;

    LinearLayout container;

    ProgressBar progressBar;

    String photoResponse, statResponse;

    int totalDownload, changeDownloads, totalLikes, changeLikes, totalViews, changeViews;

    ImageView likeButtonImage;
    TextView likeButtonText;

    Utils mUtils;

    ImageView wallpaperImage;

    String id;

    FloatingActionButton fab;

    LinearLayout infoButton;

    ProgressDialog progressDialog;


    TextView descriptionTextView, userNameTextView, fullNameTextView, locationTextView, dateTextView, likesTextView, viewsTextView, downloadsTextView;
    LinearLayout likeButton, downloadButton, collectButton, wallpaperButton, userContainer, shareButton, linkButton;
    ImageView profileImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            Utils.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            Utils.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
//        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_photo_detail);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        dataProcessor = new DataProcessor(this);
        initViews();

        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);

        intent = getIntent();

        id = intent.getStringExtra(getString(R.string.id));
        int height = intent.getIntExtra(getString(R.string.height), 0);
        int width = intent.getIntExtra(getString(R.string.width), 0);


        DisplayMetrics displaymetrics = this.getResources().getDisplayMetrics();
        float finalHeight = displaymetrics.widthPixels / ((float) width / (float) height);
        wallpaperImage.setMinimumHeight((int) finalHeight);
        Picasso.get().load(intent.getStringExtra(getString(R.string.urls))).into(wallpaperImage);
        loadImage(id);


    }

    void initViews() {
        mLoginHelper = new LoginHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backButton = toolbar.getRootView().findViewById(R.id.toolbar_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        wallpaperImage = toolbar.getRootView().findViewById(R.id.phot_detail_toolbar_image_preview);
        descriptionTextView = findViewById(R.id.tv_photo_detail_description);
        userNameTextView = findViewById(R.id.tv_photo_detail_username);
        userContainer = findViewById(R.id.ll_photo_detail_user_container);
        fullNameTextView = findViewById(R.id.tv_photo_detail_user_fullname);
        profileImageView = findViewById(R.id.iv_photo_detail_userimage);
        locationTextView = findViewById(R.id.tv_photo_detail_location);
        dateTextView = findViewById(R.id.tv_photo_detail_date);
        likesTextView = findViewById(R.id.tv_photo_detail_likes_count);
        downloadsTextView = findViewById(R.id.tv_photo_detail_downloads_count);
        viewsTextView = findViewById(R.id.tv_photo_detail_views_count);
        infoButton = findViewById(R.id.ll_photo_info_button);
        likeButtonImage = findViewById(R.id.iv_photo_detail_like_button_image);
        likeButtonText = findViewById(R.id.tv_photo_detail_like_button_text);
        container = findViewById(R.id.photo_detail_container);
        downloadButton = findViewById(R.id.ll_photo_detail_download_button);
        wallpaperButton = findViewById(R.id.ll_photo_detail_set_button);
        collectButton = findViewById(R.id.ll_photo_detail_add_button);
        likeButton = findViewById(R.id.ll_photo_detail_like_button);
        progressBar = findViewById(R.id.pb_photo_detail);
        shareButton = findViewById(R.id.share_button);
        linkButton = findViewById(R.id.link_button);
        progressBar.setVisibility(GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void loadImage(final String id) {
        container.setVisibility(GONE);
        Uri.Builder builder = new Uri.Builder();
        String url;
        if (mLoginHelper.isUserLogged()) {
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath(getString(R.string.editorial_path))
                    .appendPath(id)
                    .appendQueryParameter(getString(R.string.access_token), mSharedPreferences.getString(getString(R.string.access_token), ""))
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
             url = builder.build().toString();
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(PhotoDetailActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            photoResponse = response;
                            loadImageStats(id);
                            progressBar.setVisibility(GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    progressBar.setVisibility(GONE);
                }
            });
            queue.add(stringRequest);
        }else {
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath(getString(R.string.editorial_path))
                    .appendPath(id)
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
            url = builder.build().toString();
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(PhotoDetailActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            photoResponse = response;
                            loadImageStats(id);
                            progressBar.setVisibility(GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    progressBar.setVisibility(GONE);
                }
            });
            queue.add(stringRequest);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(GONE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        progressBar.setVisibility(GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(GONE);
    }

    void loadImageStats(String id) {
        Uri.Builder builder = new Uri.Builder();
        String url;
            builder.scheme("https")
                    .authority(getString(R.string.base_url))
                    .appendPath(getString(R.string.editorial_path))
                    .appendPath(id)
                    .appendPath("statistics")
                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id));
            url = builder.build().toString();
        RequestQueue queue = Volley.newRequestQueue(PhotoDetailActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        statResponse = response;
                        processData(photoResponse, statResponse);
                        container.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
                progressBar.setVisibility(GONE);
            }
        });
        queue.add(stringRequest);

    }

    void processData(String response, String stat) {

        try {
            Map<String, Object> photoMap = new HashMap<>();
            JSONObject photo = new JSONObject(response);
            JSONObject user = photo.getJSONObject(getString(R.string.user));
            JSONObject urls = photo.getJSONObject(getString(R.string.urls));
            JSONObject links = photo.getJSONObject(getString(R.string.links));
            JSONObject user_links = user.getJSONObject(getString(R.string.links));
            JSONObject profileImage = user.getJSONObject(getString(R.string.profile_image));
            mPhoto = dataProcessor.processPhoto(photo);

            if (photo.has(getString(R.string.exif))) {
                JSONObject exif = photo.getJSONObject(getString(R.string.exif));
                photoMap.put(getString(R.string.make), exif.get(getString(R.string.make)));
                photoMap.put(getString(R.string.model), exif.get(getString(R.string.model)));
                photoMap.put(getString(R.string.exposure_time), exif.get(getString(R.string.exposure_time)));
                photoMap.put(getString(R.string.aperture), exif.get(getString(R.string.aperture)));
                photoMap.put(getString(R.string.focal_length), exif.get(getString(R.string.focal_length)));
                photoMap.put(getString(R.string.iso), exif.get(getString(R.string.iso)));
            } else {
                photoMap.put(getString(R.string.make), "");
                photoMap.put(getString(R.string.model), "");
                photoMap.put(getString(R.string.exposure_time), "");
                photoMap.put(getString(R.string.aperture), "");
                photoMap.put(getString(R.string.focal_length), "");
                photoMap.put(getString(R.string.iso), "");
            }

            if (photo.has((getString(R.string.location)))) {
                JSONObject location = photo.getJSONObject(getString(R.string.location));
                photoMap.put(getString(R.string.location_title), location.get(getString(R.string.title)));
                photoMap.put(getString(R.string.photo_location), location.get(getString(R.string.name)));
                photoMap.put(getString(R.string.city), location.get(getString(R.string.city)));
                photoMap.put(getString(R.string.country), location.get(getString(R.string.country)));
            } else {
                photoMap.put(getString(R.string.location_title), "");
                photoMap.put(getString(R.string.photo_location), "");
                photoMap.put(getString(R.string.city), "");
                photoMap.put(getString(R.string.country), "");
            }

            JSONObject stats = new JSONObject(stat);
            JSONObject downloads = stats.getJSONObject(getString(R.string.downloads));
            JSONObject views = stats.getJSONObject(getString(R.string.views));
            JSONObject likes = stats.getJSONObject(getString(R.string.likes));
            JSONObject historicalDownload = downloads.getJSONObject("historical");
            JSONObject historicalLikes = likes.getJSONObject("historical");
            JSONObject historicalViews = views.getJSONObject("historical");

            totalDownload = downloads.getInt("total");
            totalLikes = likes.getInt("total");
            totalViews = views.getInt("total");
            changeDownloads = historicalDownload.getInt("change");
            changeLikes = historicalLikes.getInt("change");
            changeViews = historicalViews.getInt("change");

            photoMap.put(getString(R.string.id), photo.get(getString(R.string.id)));
            photoMap.put(getString(R.string.created_at), photo.get(getString(R.string.created_at)));
            photoMap.put(getString(R.string.updated_at), photo.get(getString(R.string.updated_at)));
            photoMap.put(getString(R.string.width), photo.get(getString(R.string.width)));
            photoMap.put(getString(R.string.height), photo.get(getString(R.string.height)));
            photoMap.put(getString(R.string.color), photo.get(getString(R.string.color)));
            photoMap.put(getString(R.string.description), photo.get(getString(R.string.description)));
            photoMap.put(getString(R.string.raw), urls.get(getString(R.string.raw)));
            photoMap.put(getString(R.string.full), urls.get(getString(R.string.full)));
            photoMap.put(getString(R.string.regular), urls.get(getString(R.string.regular)));
            photoMap.put(getString(R.string.small), urls.get(getString(R.string.small)));
            photoMap.put(getString(R.string.thumb), urls.get(getString(R.string.thumb)));
            photoMap.put(getString(R.string.self), links.get(getString(R.string.self)));
            photoMap.put(getString(R.string.html), links.get(getString(R.string.html)));
            photoMap.put(getString(R.string.download), links.get(getString(R.string.download)));
            photoMap.put(getString(R.string.download_location), links.get(getString(R.string.download_location)));
            photoMap.put(getString(R.string.categories), photo.get(getString(R.string.categories)));
            photoMap.put(getString(R.string.sponsored), photo.get(getString(R.string.sponsored)));
            photoMap.put(getString(R.string.likes), photo.get(getString(R.string.likes)));
            photoMap.put(getString(R.string.liked_by_user), photo.get(getString(R.string.liked_by_user)));
            photoMap.put(getString(R.string.current_user_collections), photo.get(getString(R.string.current_user_collections)));
            photoMap.put(getString(R.string.slug), photo.get(getString(R.string.slug)));
            photoMap.put(getString(R.string.user_id), user.get(getString(R.string.id)));
            photoMap.put(getString(R.string.user_updated_at), user.get(getString(R.string.updated_at)));
            photoMap.put(getString(R.string.username), user.get(getString(R.string.username)));
            photoMap.put(getString(R.string.name), user.get(getString(R.string.name)));
            photoMap.put(getString(R.string.first_name), user.get(getString(R.string.first_name)));
            photoMap.put(getString(R.string.last_name), user.get(getString(R.string.last_name)));
            photoMap.put(getString(R.string.twitter_username), user.get(getString(R.string.twitter_username)));
            photoMap.put(getString(R.string.portfolio_url), user.get(getString(R.string.portfolio_url)));
            photoMap.put(getString(R.string.bio), user.get(getString(R.string.bio)));
            photoMap.put(getString(R.string.location), user.get(getString(R.string.location)));
            photoMap.put(getString(R.string.user_self), user_links.get(getString(R.string.self)));
            photoMap.put(getString(R.string.user_html), user_links.get(getString(R.string.html)));
            photoMap.put(getString(R.string.user_photos), user_links.get(getString(R.string.user_photos)));
            photoMap.put(getString(R.string.user_followers), user_links.get(getString(R.string.user_followers)));
            photoMap.put(getString(R.string.user_following), user_links.get(getString(R.string.user_following)));
            photoMap.put(getString(R.string.user_likes), user_links.get(getString(R.string.likes)));
            photoMap.put(getString(R.string.user_portfolio), user_links.get(getString(R.string.user_portfolio)));
            photoMap.put(getString(R.string.user_small), profileImage.get(getString(R.string.small)));
            photoMap.put(getString(R.string.user_medium), profileImage.get(getString(R.string.user_medium)));
            photoMap.put(getString(R.string.user_large), profileImage.get(getString(R.string.user_large)));
            photoMap.put(getString(R.string.instagram_username), user.get(getString(R.string.instagram_username)));
            photoMap.put(getString(R.string.total_photos), user.get(getString(R.string.total_photos)));
            photoMap.put(getString(R.string.total_likes), user.get(getString(R.string.total_likes)));
            photoMap.put(getString(R.string.total_collections), user.get(getString(R.string.total_collections)));
            photoMap.put(getString(R.string.views), photo.get(getString(R.string.views)));
            photoMap.put(getString(R.string.downloads), photo.get(getString(R.string.downloads)));
            final Photo p = new Photo(PhotoDetailActivity.this, photoMap, true);
            if (p.description.equals("null")) {
                descriptionTextView.setVisibility(GONE);
            } else {
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(p.description);
            }

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isStoragePermissionGranted()){
                        downloadFile(p.id, p.links.download);
                    } else {
                        requestPermission();
                    }
                }
            });
            wallpaperButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SetWallpaperTask().execute();
                }
            });

            collectButton.setVisibility(GONE);

            collectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLoginHelper.isUserLogged()){
                        Toast.makeText(PhotoDetailActivity.this, "Add to collection", Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(v, "Please Login first", Snackbar.LENGTH_SHORT).setAction("LOGIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(PhotoDetailActivity.this, LoginActivity.class));
                            }
                        }).show();
                    }
                }
            });

            userNameTextView.setText("@" + p.user.userName);
            fullNameTextView.setText(p.user.name);
            Picasso.get().load(p.user.profileImage.medium).placeholder(R.drawable.ic_user).fit().centerCrop().transform(new CropCircleTransformation()).into(profileImageView);

            if (p.location.city.equals("null") && p.location.country.equals("null")) {
                locationTextView.setText("N/A");
            } else if (p.location.city.equals("null")) {
                locationTextView.setText(p.location.country);
            } else {
                locationTextView.setText(p.location.city + ", " + p.location.country);
            }
            int f = p.createdAt.indexOf("T");
            final String dateTaken = p.createdAt.substring(0, f);
            dateTextView.setText(dateTaken);
            if (p.likedByUser) {
                likeButtonText.setText("Liked");
                likeButtonImage.setImageResource(R.drawable.ic_heart_red);
            } else {
                likeButtonText.setText("Like");
                likeButtonImage.setImageResource(R.drawable.ic_heart);
            }

            linkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriBrowser = Uri.parse("https://unsplash.com/photos/"+p.id);
                    Intent feedback = new Intent(Intent.ACTION_VIEW, uriBrowser);
                    feedback.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try{
                        startActivity(feedback);
                    } catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/photos/"+p.id)));
                    }
                }
            });

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "https://unsplash.com/photos/"+p.id);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLoginHelper.isUserLogged()) {
                        if (p.likedByUser == true) {
                            // unlike image
                            Uri.Builder builder = new Uri.Builder();
                            String url;
                            builder.scheme("https")
                                    .authority(getString(R.string.base_url))
                                    .appendPath(getString(R.string.editorial_path))
                                    .appendPath(id)
                                    .appendPath("like")
                                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id))
                                    .appendQueryParameter(getString(R.string.access_token), mSharedPreferences.getString(getString(R.string.access_token), ""));
                            url = builder.build().toString();
                            RequestQueue queue = Volley.newRequestQueue(PhotoDetailActivity.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            likeButtonImage.setImageResource(R.drawable.ic_heart);
                                            likeButtonText.setText("LIKE");
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "onErrorResponse: " + error);
                                }
                            });
                            queue.add(stringRequest);
                        } else {
                            //Like Image
                            Uri.Builder builder = new Uri.Builder();
                            String url;
                            builder.scheme("https")
                                    .authority(getString(R.string.base_url))
                                    .appendPath(getString(R.string.editorial_path))
                                    .appendPath(id)
                                    .appendPath("like")
                                    .appendQueryParameter(getString(R.string.client_id_tag), getString(R.string.client_id))
                                    .appendQueryParameter(getString(R.string.access_token), mSharedPreferences.getString(getString(R.string.access_token), ""));
                            url = builder.build().toString();
                            RequestQueue queue = Volley.newRequestQueue(PhotoDetailActivity.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            likeButtonImage.setImageResource(R.drawable.ic_heart_red);
                                            likeButtonText.setText("LIKED");
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "onErrorResponse: " + error);
                                }
                            });
                            queue.add(stringRequest);
                        }
                    } else {
                        Snackbar.make(v, "Please login first", Snackbar.LENGTH_SHORT).setAction("LOGIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PhotoDetailActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
                    }
                }
            });

            likesTextView.setText(totalLikes + "");
            downloadsTextView.setText(totalDownload + "");
            viewsTextView.setText(totalViews + "");
            userContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startProfileActivity = new Intent(PhotoDetailActivity.this, ProfileActivity.class);
                    startProfileActivity.putExtra(getString(R.string.username), p.user.userName);
                    startActivity(startProfileActivity);
                }
            });
            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View messageView = getLayoutInflater().inflate(R.layout.photo_more_info_dialog, null, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhotoDetailActivity.this);
                    View messageRootView = messageView.getRootView();
                    TextView infoPicDate = messageRootView.findViewById(R.id.tv_info_dialog_date);
                    infoPicDate.setText("Published on " + dateTaken);
                    TextView infoLikesCount = messageRootView.findViewById(R.id.tv_info_dialog_like_count);
                    infoLikesCount.setText(totalLikes + "");
                    TextView likesChange = messageRootView.findViewById(R.id.tv_info_dialog_like_change);
                    likesChange.setText("+" + changeLikes + " since last month");
                    TextView infoViewCount = messageRootView.findViewById(R.id.tv_info_dialog_views_count);
                    infoViewCount.setText(totalViews + "");
                    TextView viewChange = messageRootView.findViewById(R.id.tv_info_dialog_view_change);
                    viewChange.setText("+" + changeViews + " since last month");
                    TextView infoDownloadCount = messageRootView.findViewById(R.id.tv_info_dialog_download_count);
                    infoDownloadCount.setText(totalDownload + "");
                    TextView downloadChange = messageRootView.findViewById(R.id.tv_info_dialog_download_change);
                    downloadChange.setText("+" + changeDownloads + " since last month");
                    TextView cameraMake = messageRootView.findViewById(R.id.tv_info_dialog_camera_make);
                    cameraMake.setText(p.exif.make);
                    TextView cameraModel = messageRootView.findViewById(R.id.tv_info_dialog_camera_model);
                    cameraModel.setText(p.exif.model);
                    TextView cameraAperture = messageRootView.findViewById(R.id.tv_info_dialog_camera_aperture);
                    cameraAperture.setText(p.exif.aperture);
                    TextView cameraISO = messageRootView.findViewById(R.id.tv_info_dialog_camera_iso);
                    cameraISO.setText(p.exif.iso);
                    TextView cameraExposure = messageRootView.findViewById(R.id.tv_info_dialog_camera_shutter);
                    cameraExposure.setText(p.exif.exposureTime);
                    TextView cameraFocalLength = messageRootView.findViewById(R.id.tv_info_dialog_camera_focal_length);
                    cameraFocalLength.setText(p.exif.focalLength);
                    TextView dimensionsTextview = messageRootView.findViewById(R.id.tv_info_dialog_dimensions);
                    dimensionsTextview.setText(p.width + "x" + p.height);
                    builder.setView(messageView);
                    builder.create();
                    builder.show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(PhotoDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            downloadFile(mPhoto.id, mPhoto.links.download);
        }
    }

    void  requestPermission(){
            ActivityCompat.requestPermissions(PhotoDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    void downloadFile(String id, String link){

        String photoDisplayUrl;
        switch (mSharedPreferences.getString("load_quality", "Regular")){
            case "Raw":
                photoDisplayUrl = mPhoto.urls.raw;
                break;
            case "Full":
                photoDisplayUrl = mPhoto.urls.full;
                break;
            case "Regular":
                photoDisplayUrl = mPhoto.urls.regular;
                break;
            case "Small":
                photoDisplayUrl = mPhoto.urls.small;
                break;
            case "Thumb":
                photoDisplayUrl = mPhoto.urls.thumb;
                break;
            default:
                photoDisplayUrl = mPhoto.urls.regular;
        }
        Toast.makeText(this, "Downloading image", Toast.LENGTH_SHORT).show();
        String filename = id+".jpg";
        String downloadUrl =photoDisplayUrl;
        File direct = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/splashstock" + "/");
        if (!direct.exists()){
            direct.mkdir();
        }
        DownloadManager downloadManager = (DownloadManager) PhotoDetailActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(true)
                .setAllowedOverMetered(true)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setVisibleInDownloadsUi(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+"splashstock"+File.separator+filename);
        downloadManager.enqueue(request);
    }

    public class SetWallpaperTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try{
                String photoDisplayUrl;
                switch (mSharedPreferences.getString("load_quality", "Regular")){
                    case "Raw":
                        photoDisplayUrl = mPhoto.urls.raw;
                        break;
                    case "Full":
                        photoDisplayUrl = mPhoto.urls.full;
                        break;
                    case "Regular":
                        photoDisplayUrl = mPhoto.urls.regular;
                        break;
                    case "Small":
                        photoDisplayUrl = mPhoto.urls.small;
                        break;
                    case "Thumb":
                        photoDisplayUrl = mPhoto.urls.thumb;
                        break;
                    default:
                        photoDisplayUrl = mPhoto.urls.regular;
                }

                Bitmap bitmap = Picasso.get().load(photoDisplayUrl).get();
                wallpaperManager.setBitmap(bitmap);
                return bitmap;
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            if (bitmap != null) {
                Toast.makeText(PhotoDetailActivity.this, "Sucessfully set wallpaper", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Setting wallpaper...");
            progressDialog.show();


        }
    }
}