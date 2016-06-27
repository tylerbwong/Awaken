package com.example.tylerbwong.awaken.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tylerbwong.awaken.interfaces.AsyncResponse;
import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.database.ConnectionDatabaseHelper;
import com.example.tylerbwong.awaken.network.Location;
import com.example.tylerbwong.awaken.network.StatusUpdate;

/**
 * @author Tyler Wong
 */
public class NewConnectionActivity extends AppCompatActivity implements AsyncResponse {

   private TextInputEditText mNicknameInput;
   private TextInputEditText mHostInput;
   private TextInputEditText mMacInput;
   private TextInputEditText mWolPortInput;
   private TextInputEditText mDevicePortInput;
   private Button mEnterButton;

   private ConnectionDatabaseHelper mDatabaseHelper;
   private StatusUpdate mStatusUpdate;
   private String mNickname;
   private String mHost;
   private String mMac;
   private String mPortWol;
   private String mDevicePort;
   private String mCity;
   private String mState;
   private String mCountry;
   private String mStatus;
   private boolean mHasTextHost = false;
   private boolean mHasTextPortWol = false;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_new_connection);
      mDatabaseHelper = new ConnectionDatabaseHelper(this);
      mStatusUpdate = new StatusUpdate();
      mStatusUpdate.mDelegate = this;

      mNicknameInput = (TextInputEditText) findViewById(R.id.nickname_input);
      mHostInput = (TextInputEditText) findViewById(R.id.host_input);
      mMacInput = (TextInputEditText) findViewById(R.id.mac_input);
      mWolPortInput = (TextInputEditText) findViewById(R.id.wol_input);
      mDevicePortInput = (TextInputEditText) findViewById(R.id.port_input);
      mEnterButton = (Button) findViewById(R.id.enter_button);

      mEnterButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            enterAction();
         }
      });

      mHostInput.addTextChangedListener(new TextWatcher() {

         @Override
         public void onTextChanged(CharSequence sequence, int start, int before, int count) {
            if (sequence.toString().trim().length() == 0) {
               mHasTextHost = false;
            }
            else {
               mHasTextHost = true;
            }
            checkFields();
         }

         @Override
         public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

         }

         @Override
         public void afterTextChanged(Editable editable) {
         }
      });

      mWolPortInput.addTextChangedListener(new TextWatcher() {

         @Override
         public void onTextChanged(CharSequence sequence, int start, int before, int count) {
            if (sequence.toString().trim().length() == 0) {
               mHasTextPortWol = false;
            }
            else {
               mHasTextPortWol = true;
            }
            checkFields();
         }

         @Override
         public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

         }

         @Override
         public void afterTextChanged(Editable editable) {
         }
      });

      mEnterButton.setEnabled(false);
   }

   private void checkFields() {
      if (mHasTextHost && mHasTextPortWol) {
         mEnterButton.setEnabled(true);
      }
      else {
         mEnterButton.setEnabled(false);
      }
   }

   private void switchToMain() {
      Intent mainIntent = new Intent(NewConnectionActivity.this, MainActivity.class);
      startActivity(mainIntent);
      finish();
   }

   private void enterAction() {
      mNickname = mNicknameInput.getText().toString();
      mHost = mHostInput.getText().toString();
      mMac = formatMac(mMacInput.getText().toString());
      mPortWol = mWolPortInput.getText().toString();
      mDevicePort = mDevicePortInput.getText().toString();
      Location newLocation = new Location(mHost);
      mCity = newLocation.getCity();
      mState = newLocation.getStateProv();
      mCountry = newLocation.getCountryName();
      mStatusUpdate.execute(new Pair<>(mHost, Integer.parseInt(mDevicePort)));
   }

   @Override
   public void onTaskResult(Boolean result) {
      mStatus = String.valueOf(result);

      String message;
      try {
         mDatabaseHelper.insertConnection(mNickname, mHost, mMac, mPortWol,
               mDevicePort, mCity, mState, mCountry, mStatus, "");
         message = getResources().getString(R.string.new_connection_success);
         switchToMain();
      }
      catch (Exception e) {
         message = getResources().getString(R.string.new_connection_fail);
      }
      Toast.makeText(NewConnectionActivity.this, message, Toast.LENGTH_LONG).show();
   }

   private static String formatMac(String mac) {
      String formattedMac = "";
      mac = mac.replace("-", "");
      mac = mac.replace(":", "");
      for (int i = 0; i < mac.length(); i += 2) {
         formattedMac += mac.substring(i, i + 2);
         if (i < mac.length() - 2) {
            formattedMac += ":";
         }
      }
      formattedMac = formattedMac.toUpperCase();
      return formattedMac;
   }
}
