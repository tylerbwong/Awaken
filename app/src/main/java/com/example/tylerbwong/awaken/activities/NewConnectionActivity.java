package com.example.tylerbwong.awaken.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.tylerbwong.awaken.network.LocationServiceProvider;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.database.ConnectionDatabaseHelper;
import com.example.tylerbwong.awaken.network.Location;
import com.example.tylerbwong.awaken.network.StatusUpdate;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.Callable;
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

    private final CompositeDisposable disposables = new CompositeDisposable();

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

    private void enterAction() {
        mNickname = mNicknameInput.getText().toString();
        mHost = mHostInput.getText().toString();
        mPortWol = mWolPortInput.getText().toString();
        mDevicePort = mDevicePortInput.getText().toString();
        String message;
        if (macIsValid(mMac = mMacInput.getText().toString().toUpperCase())) {
            Disposable disposable = getIpAddress()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String ipAddress) {
                            makeLocationAndStatusRequest(ipAddress);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            Log.e("ERROR", throwable.getLocalizedMessage());
                        }
                    });
            disposables.add(disposable);
        } else {
            message = getResources().getString(R.string.mac_address_invalid);
            Toast.makeText(NewConnectionActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void makeLocationAndStatusRequest(String ipAddress) {
        Single<Location> locationRequest = LocationServiceProvider.locationService.getLocation(ipAddress);
        Single<Boolean> statusRequest = StatusUpdate.getStatus(ipAddress, Integer.parseInt(mDevicePort));

        Disposable disposable = Single.zip(locationRequest, statusRequest, new BiFunction<Location, Boolean, Pair<Location, Boolean>>() {
            @Override
            public Pair<Location, Boolean> apply(Location location, Boolean status) {
                return Pair.create(location, status);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<Location, Boolean>>() {
            @Override
            public void accept(Pair<Location, Boolean> locationBooleanPair) {
                onLocationStatusSuccess(locationBooleanPair);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Log.e("ERROR", throwable.getLocalizedMessage());
            }
        });
        disposables.add(disposable);
    }

    private Single<String> getIpAddress() {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                InetAddress[] ip = InetAddress.getAllByName(mHost);
                for (InetAddress address : ip) {
                    System.out.println(address.toString());
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        mHost = convertIpByteArray(address.getAddress());
                    }
                }
                return mHost;
            }
        });
    }

    private void onLocationStatusSuccess(Pair<Location, Boolean> result) {
        Location location = result.first;
        mCity = location.getCity();
        mState = location.getRegionCode();
        mCountry = location.getCountryName();

        Disposable disposable = mDatabaseHelper.insertConnection(mNickname, mHost, mMac, mPortWol,
                mDevicePort, mCity, mState, mCountry, String.valueOf(result.second), "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        onFinish(getResources().getString(R.string.new_connection_success));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        onFinish(getResources().getString(R.string.new_connection_fail));
                    }
                });
        disposables.add(disposable);
    }

    private void onFinish(String message) {
        Toast.makeText(NewConnectionActivity.this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private static String convertIpByteArray(byte[] ipAddress) {
        String result = "";

        for (int index = 0; index < ipAddress.length; index++) {
            result += ipAddress[index] & 0xFF;

            if (index < ipAddress.length - 1) {
                result += ".";
            }
        }

        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposables.clear();
    }

    private boolean macIsValid(String mac) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        Matcher m = p.matcher(mac);
        return m.matches();
    }
}
