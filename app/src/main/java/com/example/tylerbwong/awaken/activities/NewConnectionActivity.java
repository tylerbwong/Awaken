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
   private TextInputEditText mWolPort;
   private TextInputEditText mDevicePort;
   private Button mEnterButton;

   private ConnectionDatabaseHelper databaseHelper;
   private StatusUpdate statusUpdate;
   private String nickname;
   private String host;
   private String mac;
   private String portWol;
   private String devicePort;
   private String city;
   private String state;
   private String country;
   private String status;
   private boolean hasTextHost = false;
   private boolean hasTextPortWol = false;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_new_connection);
      databaseHelper = new ConnectionDatabaseHelper(this);
      statusUpdate = new StatusUpdate();
      statusUpdate.delegate = this;

      mNicknameInput = (TextInputEditText) findViewById(R.id.nickname_input);
      mHostInput = (TextInputEditText) findViewById(R.id.host_input);
      mMacInput = (TextInputEditText) findViewById(R.id.mac_input);
      mWolPort = (TextInputEditText) findViewById(R.id.wol_input);
      mDevicePort = (TextInputEditText) findViewById(R.id.port_input);
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
               hasTextHost = false;
            }
            else {
               hasTextHost = true;
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

      mWolPort.addTextChangedListener(new TextWatcher() {

         @Override
         public void onTextChanged(CharSequence sequence, int start, int before, int count) {
            if (sequence.toString().trim().length() == 0) {
               hasTextPortWol = false;
            }
            else {
               hasTextPortWol = true;
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
      if (hasTextHost && hasTextPortWol) {
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
      nickname = mNicknameInput.getText().toString();
      host = mHostInput.getText().toString();
      mac = mMacInput.getText().toString();
      portWol = mWolPort.getText().toString();
      devicePort = mDevicePort.getText().toString();
      Location newLocation = new Location(host);
      city = newLocation.getCity();
      state = newLocation.getStateProv();
      country = newLocation.getCountryName();
      statusUpdate.execute(new Pair<>(host, Integer.parseInt(devicePort)));
   }

   @Override
   public void onTaskResult(Boolean result) {
      status = String.valueOf(result);

      String message;
      try {
         databaseHelper.insertConnection(nickname, host, mac, portWol,
               devicePort, city, state, country, status, "");
         message = getResources().getString(R.string.new_connection_success);
         switchToMain();
      }
      catch (Exception e) {
         message = getResources().getString(R.string.new_connection_fail);
      }
      Toast.makeText(NewConnectionActivity.this, message, Toast.LENGTH_LONG).show();
   }
}
