package com.example.tylerbwong.awaken.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.database.ConnectionDatabaseHelper;
import com.example.tylerbwong.awaken.network.Location;

/**
 * @author Tyler Wong
 */
public class NewConnectionActivity extends AppCompatActivity {

   private TextInputEditText mNicknameInput;
   private TextInputEditText mHostInput;
   private TextInputEditText mMacInput;
   private TextInputEditText mWolPort;
   private TextInputEditText mDevicePort;
   private Button mEnterButton;

   private ConnectionDatabaseHelper databaseHelper;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_new_connection);
      databaseHelper = new ConnectionDatabaseHelper(this);

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
   }

   private void switchToMain() {
      Intent mainIntent = new Intent(NewConnectionActivity.this, MainActivity.class);
      startActivity(mainIntent);
      finish();
   }

   private void enterAction() {
      String nickname = mNicknameInput.getText().toString();
      String host = mHostInput.getText().toString();
      String mac = mMacInput.getText().toString();
      String portWol = mWolPort.getText().toString();
      String devicePort = mDevicePort.getText().toString();
      Location newLocation = new Location(host);
      String city = newLocation.getCity();
      String state = newLocation.getStateProv();
      String country = newLocation.getCountryName();
      String message;
      try {
         databaseHelper.insertConnection(nickname, host, mac, portWol,
               devicePort, city, state, country, "", "");
         message = getResources().getString(R.string.new_connection_success);
         switchToMain();
      }
      catch(Exception e) {
         message = getResources().getString(R.string.new_connection_fail);
      }
      Toast.makeText(NewConnectionActivity.this, message, Toast.LENGTH_LONG).show();
   }
}
