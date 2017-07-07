package com.example.tylerbwong.awaken.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.example.tylerbwong.awaken.R;

/**
 * @author Tyler Wong
 */
public class SplashActivity extends AppCompatActivity {
    private final static int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

        if (preferences.getBoolean("appIntroFinished", false)) {
            skipToMain();
        }
    }

    private void skipToMain() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, StartActivity.class);
                mainIntent.putExtra("logo_transition", true);
                LinearLayout logo = (LinearLayout) findViewById(R.id.logo);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashActivity.this, logo, getString(R.string.logo_transition));
                SplashActivity.this.startActivity(mainIntent, options.toBundle());
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
