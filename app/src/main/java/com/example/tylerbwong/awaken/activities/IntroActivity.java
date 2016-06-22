package com.example.tylerbwong.awaken.activities;

import android.content.Intent;
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

   private void switchToLock() {
      Intent mainIntent = new Intent(IntroActivity.this, LockActivity.class);
      startActivity(mainIntent);
   }

   @Override
   public void onSkipPressed(Fragment currentFragment) {
      super.onSkipPressed(currentFragment);

      switchToLock();
   }

   @Override
   public void onDonePressed(Fragment currentFragment) {
      super.onDonePressed(currentFragment);

      switchToLock();
   }

   @Override
   public void onBackPressed() {

   }
}
