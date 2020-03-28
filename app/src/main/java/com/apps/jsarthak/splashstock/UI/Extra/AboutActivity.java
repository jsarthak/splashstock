package com.apps.jsarthak.splashstock.UI.Extra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.Utils.Utils;

public class AboutActivity extends AppCompatActivity {


    LinearLayout rateButton, feedbackButton, instagramButton, websiteButton;
    ImageView backButton;

    Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils.setTypeFace(this);
        Utils.setLightStatusBar(getWindow().getDecorView(), this);
        setContentView(R.layout.activity_about);

        backButton = findViewById(R.id.iv_toolbar_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rateButton = findViewById(R.id.rate_button);
        feedbackButton = findViewById(R.id.feedback_button);
        instagramButton = findViewById(R.id.instagram_developer_button);
        websiteButton = findViewById(R.id.website_developer_button);


    }
}
