package com.roommate.find.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.roommate.find.R;

public class SplashActivity extends BaseActivity {

    private LinearLayout llSplash;

    @Override
    public void initialise() {
        llSplash = (LinearLayout) inflater.inflate(R.layout.activity_splash, null);
        addBodyView(llSplash);
        lockMenu();
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        llToolbar.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        }, 500);
    }

    @Override
    public void getData() {

    }
}
