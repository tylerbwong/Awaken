package io.awaken.ui.connections;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import io.awaken.R;
import io.awaken.data.database.ConnectionDatabaseHelper;
import io.awaken.data.database.ConnectionDatabaseProvider;
import io.awaken.data.model.Location;
import io.awaken.data.network.LocationServiceProvider;
import io.awaken.data.network.StatusUpdate;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Tyler Wong
 */
public class NewConnectionActivity extends AppCompatActivity {

    private TextInputEditText nicknameInput;
    private TextInputEditText hostInput;
    private TextInputEditText macInput;
    private TextInputEditText wolPortInput;
    private TextInputEditText devicePortInput;
    private Button enterButton;

    private ConnectionDatabaseHelper databaseHelper;
    private String nickname;
    private String host;
    private String mac;
    private String portWol;
    private String devicePort;
    private boolean hasTextHost = false;
    private boolean hasTextPortWol = false;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connection);
        databaseHelper = ConnectionDatabaseProvider.databaseHelper;

        nicknameInput = findViewById(R.id.nickname_input);
        hostInput = findViewById(R.id.host_input);
        macInput = findViewById(R.id.mac_input);
        wolPortInput = findViewById(R.id.wol_input);
        devicePortInput = findViewById(R.id.port_input);
        enterButton = findViewById(R.id.enter_button);

        enterButton.setOnClickListener(view -> enterAction());

        hostInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hasTextHost = !(charSequence.toString().trim().length() == 0);
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        wolPortInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                hasTextPortWol = !(sequence.toString().trim().length() == 0);
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        macInput.addTextChangedListener(new TextWatcher() {
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
                    macInput.setText(sequence.toString() + ":");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (macInput.length() > 0) {
                    macInput.setSelection(macInput.length());
                }
            }
        });

        enterButton.setEnabled(false);
    }

    private void checkFields() {
        enterButton.setEnabled(hasTextHost && hasTextPortWol);
    }

    private void enterAction() {
        nickname = nicknameInput.getText().toString();
        host = hostInput.getText().toString();
        portWol = wolPortInput.getText().toString();
        devicePort = devicePortInput.getText().toString();
        String message;
        if (macIsValid(mac = macInput.getText().toString().toUpperCase())) {
            Disposable disposable = getIpAddress()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::makeLocationAndStatusRequest,
                            throwable -> Log.e("ERROR", throwable.getLocalizedMessage())
                    );
            disposables.add(disposable);
        }
        else {
            message = getResources().getString(R.string.mac_address_invalid);
            Toast.makeText(NewConnectionActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void makeLocationAndStatusRequest(String ipAddress) {
        Single<Location> locationRequest = LocationServiceProvider.locationService.getLocation(ipAddress);
        Single<Boolean> statusRequest = StatusUpdate.isRunning(ipAddress, Integer.parseInt(devicePort));

        Disposable disposable = Single.zip(locationRequest, statusRequest, Pair::create)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onLocationStatusSuccess,
                        throwable -> Log.e("ERROR", throwable.getLocalizedMessage())
                );
        disposables.add(disposable);
    }

    private Single<String> getIpAddress() {
        return Single.fromCallable(() -> {
            InetAddress[] ip = InetAddress.getAllByName(host);
            for (InetAddress address : ip) {
                System.out.println(address.toString());
                if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                    host = convertIpByteArray(address.getAddress());
                }
            }
            return host;
        });
    }

    private void onLocationStatusSuccess(Pair<Location, Boolean> result) {
        Location location = result.first;
        String city = location.getCity();
        String state = location.getRegionCode();
        String country = location.getCountryName();

        Disposable disposable = databaseHelper.insertConnection(nickname, host, mac, portWol,
                devicePort, city, state, country, String.valueOf(result.second), "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> onFinish(getResources().getString(R.string.new_connection_success)),
                        throwable -> onFinish(getResources().getString(R.string.new_connection_fail))
                );
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
