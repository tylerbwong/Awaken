package com.example.tylerbwong.awaken.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.tylerbwong.awaken.R;

/**
 * VERSION 2.0 FEATURE
 * @author Tyler Wong
 */
public class LockActivity extends AppCompatActivity implements PinLockListener {
   private PinLockView mPinLockView;
   private IndicatorDots mIndicatorDots;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_lock);

      mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
      mPinLockView.setPinLockListener(this);
      mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
      mPinLockView.attachIndicatorDots(mIndicatorDots);
   }

   @Override
   public void onComplete(String pin) {

   }

   @Override
   public void onEmpty() {

   }

   @Override
   public void onPinChange(int pinLength, String intermediatePin) {

   }
}
