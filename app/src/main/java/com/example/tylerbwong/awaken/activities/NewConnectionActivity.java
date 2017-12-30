package com.example.tylerbwong.awaken.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.database.ConnectionDatabaseHelper;
import com.example.tylerbwong.awaken.network.Location;
import com.example.tylerbwong.awaken.network.StatusUpdate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tyler Wong
 */
public class NewConnectionActivity extends AppCompatActivity {

    private TextInputEditText mNicknameInput;
    private TextInputEditText mHostInput;
    private TextInputEditText mMacInput;
    private TextInputEditText mWolPortInput;
    private TextInputEditText mDevicePortInput;
    private Button mEnterButton;

    private ConnectionDatabaseHelper mDatabaseHelper;
    private String mNickname;
    private String mHost;
    private String mMac;
    private String mPortWol;
    private String mDevicePort;
    private String mCity;
    private String mState;
    private String mCountry;
    private boolean mHasTextHost = false;
    private boolean mHasTextPortWol = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connection);
        mDatabaseHelper = new ConnectionDatabaseHelper(this);

        mNicknameInput = findViewById(R.id.nickname_input);
        mHostInput = findViewById(R.id.host_input);
        mMacInput = findViewById(R.id.mac_input);
        mWolPortInput = findViewById(R.id.wol_input);
        mDevicePortInput = findViewById(R.id.port_input);
        mEnterButton = findViewById(R.id.enter_button);

        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterAction();
            }
        });

        mHostInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mHasTextHost = !(charSequence.toString().trim().length() == 0);
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mWolPortInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                mHasTextPortWol = !(sequence.toString().trim().length() == 0);
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mMacInput.addTextChangedListener(new TextWatcher() {
            String noColons;

            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                noColons = sequence.toString().replace(":", "");
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                final int noColLength = noColons.length();
                final int length = sequence.toString().length();
                if ((noColLength % 2 != 0 && noColLength != 0 && noColLength < 17
                        && (length > 0)) && sequence.toString().charAt(length - 1) != ':') {
                    mMacInput.setText(sequence.toString() + ":");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mMacInput.length() > 0) {
                    mMacInput.setSelection(mMacInput.length());
                }
            }
        });

        mEnterButton.setEnabled(false);
    }

    private void checkFields() {
        mEnterButton.setEnabled(mHasTextHost && mHasTextPortWol);
    }

    private void switchToMain() {
        Intent mainIntent = new Intent(NewConnectionActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void enterAction() {
        String message;
        if (macIsValid(mMac = mMacInput.getText().toString().toUpperCase())) {
            new AsyncTaskCaller().execute();
        } else {
            message = getResources().getString(R.string.mac_address_invalid);
            Toast.makeText(NewConnectionActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private class AsyncTaskCaller extends AsyncTask<Void, Void, Pair<String, Location>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNickname = mNicknameInput.getText().toString();
            mHost = mHostInput.getText().toString();
            mPortWol = mWolPortInput.getText().toString();
            mDevicePort = mDevicePortInput.getText().toString();
        }

        @Override
        protected Pair<String, Location> doInBackground(Void... pars) {
            return new Pair<>(String.valueOf(StatusUpdate.getStatus(mHost,
                    Integer.parseInt(mDevicePort))), new Location(mHost));
        }

        @Override
        protected void onPostExecute(Pair<String, Location> statLoc) {
            super.onPostExecute(statLoc);
            Location newLocation = statLoc.second;
            mCity = newLocation.getCity();
            mState = newLocation.getStateProv();
            mCountry = newLocation.getCountryName();
            String message;
            try {
                mDatabaseHelper.insertConnection(mNickname, mHost, mMac, mPortWol,
                        mDevicePort, mCity, mState, mCountry, statLoc.first, "");
                message = getResources().getString(R.string.new_connection_success);

            } catch (Exception e) {
                message = getResources().getString(R.string.new_connection_fail);
            }
            Toast.makeText(NewConnectionActivity.this, message, Toast.LENGTH_LONG).show();
            switchToMain();
        }
    }

    private boolean macIsValid(String mac) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        Matcher m = p.matcher(mac);
        return m.matches();
    }
}
