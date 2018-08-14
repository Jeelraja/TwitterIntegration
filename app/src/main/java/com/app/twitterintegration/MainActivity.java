package com.app.twitterintegration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MainActivity extends AppCompatActivity {

    private TextView mTvProfileName;
    private ImageView mIvProfilePic;
    private Button mBtnLogOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
    }

    private void findViews() {
        mTvProfileName = findViewById(R.id.tvProfileName);
        mIvProfilePic = findViewById(R.id.ivProfilePic);
        mBtnLogOut = findViewById(R.id.btnLogout);
        mBtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSharedPref.getInstance(MainActivity.this).setDataString(Constants.TWITTER_ACCESS_TOCKEN, "");
                AppSharedPref.getInstance(MainActivity.this).setDataString(Constants.TWITTER_ACCESS_TOCKEN_SECRET, "");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        setData();
    }

    private void setData() {

        mTvProfileName.setText(AppSharedPref.getInstance(MainActivity.this).getDataString(Constants.TWITTER_PROFILE_NAME));
        Glide.with(MainActivity.this)
                .load(AppSharedPref.getInstance(MainActivity.this).getDataString(Constants.TWITTER_PROFILE_IMAGE))
                .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher_round))
                .into(mIvProfilePic);
    }
}
