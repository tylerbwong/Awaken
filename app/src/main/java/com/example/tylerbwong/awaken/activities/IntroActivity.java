package com.example.tylerbwong.awaken.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.tylerbwong.awaken.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * @author Tyler Wong
 */
public class IntroActivity extends AppIntro {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.intro_one_title),
            getResources().getString(R.string.intro_one_description), R.drawable.server,
            Color.parseColor("#1D5189")));

      addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.intro_two_title),
            getResources().getString(R.string.intro_two_description), R.drawable.computer_device,
            Color.parseColor("#1D5189")));
   }

   private void switchToMain() {
      SharedPreferences preferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = preferences.edit();
      editor.putBoolean("appIntroFinished", true);
      editor.apply();
      Intent mainIntent = new Intent(IntroActivity.this, MainActivity.class);
      startActivity(mainIntent);
   }

   @Override
   public void onSkipPressed(Fragment currentFragment) {
      super.onSkipPressed(currentFragment);
      switchToMain();
   }

   @Override
   public void onDonePressed(Fragment currentFragment) {
      super.onDonePressed(currentFragment);
      switchToMain();
   }

   @Override
   public void onBackPressed() {

   }
}
