package com.example.tylerbwong.awaken.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.utilities.Typefaces;

/**
 * @author Tyler Wong
 */
public class StartActivity extends AppCompatActivity {
   private TextView mTitleLabel;
   private Button mEnterButton;

   private Typeface robotoLight;

   private final static String ROBOTO_PATH = "fonts/roboto-light.ttf";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_start);
      robotoLight = Typefaces.get(this, ROBOTO_PATH);

      mTitleLabel = (TextView) findViewById(R.id.title_label);
      mEnterButton = (Button) findViewById(R.id.enter_button);

      if (robotoLight != null) {
         if (mTitleLabel != null) {
            mTitleLabel.setTypeface(robotoLight);
         }
         if (mEnterButton != null) {
            mEnterButton.setTypeface(robotoLight);
         }
      }

      mEnterButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent introIntent = new Intent(StartActivity.this, IntroActivity.class);
            startActivity(introIntent);
         }
      });

      boolean logoTransition = getIntent().getBooleanExtra("logo_transition", false);
      if (logoTransition) {
         overridePendingTransition(R.anim.slow_transition, R.anim.slow_transition);
      }
   }

   @Override
   public void onBackPressed() {

   }
}
