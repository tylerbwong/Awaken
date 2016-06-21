package com.example.tylerbwong.awaken.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.utilities.Typefaces;

/**
 * @author Tyler Wong
 */
public class SplashActivity extends AppCompatActivity {
   private TextView mTitleLabel;
   private Typeface robotoLight;

   private final static int SPLASH_DISPLAY_LENGTH = 1000;
   final static String ROBOTO_PATH = "fonts/roboto-light.ttf";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_splash);
      robotoLight = Typefaces.get(this, ROBOTO_PATH);

      mTitleLabel = (TextView) findViewById(R.id.title_label);

      if (mTitleLabel != null && robotoLight != null) {
         mTitleLabel.setTypeface(robotoLight);
      }
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
