package com.example.tylerbwong.awaken.activities;

import android.graphics.Color;
import android.os.Bundle;

import com.example.tylerbwong.awaken.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

/**
 * @author Tyler Wong
 */
public class IntroActivity extends AppIntro2 {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      addSlide(AppIntro2Fragment.newInstance(getResources().getString(R.string.intro_one_title),
            getResources().getString(R.string.intro_one_description), R.drawable.server,
            Color.parseColor("#1D5189")));

      addSlide(AppIntro2Fragment.newInstance(getResources().getString(R.string.intro_two_title),
            getResources().getString(R.string.intro_two_description), R.drawable.computer_device,
            Color.parseColor("#1D5189")));
   }

   @Override
   public void onBackPressed() {

   }
}
