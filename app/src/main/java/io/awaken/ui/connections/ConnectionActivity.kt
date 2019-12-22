package io.awaken.ui.connections

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import io.awaken.R
import io.awaken.data.database.ConnectionDatabaseProvider
import io.awaken.data.model.Connection
import io.awaken.data.model.Location
import io.awaken.data.network.LocationServiceProvider
import io.awaken.data.network.isRunning
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_connection.*
import java.net.Inet4Address
import java.net.InetAddress
import java.util.*
import java.util.regex.Pattern

/**
 * @author Tyler Wong
 */
class ConnectionActivity : AppCompatActivity() {

    private var databaseHelper = ConnectionDatabaseProvider.databaseHelper
    private var connectionId: Int? = null
    private var nickname: String = ""
    private var host: String = ""
    private var mac: String = ""
    private var portWol: String = ""
    private var devicePort: String = ""
    private var hasTextHost = false
    private var hasTextPortWol = false

    private val disposables = CompositeDisposable()

    private val ipAddress: Single<String>
        get() = Single.fromCallable {
            val ip = InetAddress.getAllByName(host)
            for (address in ip) {
                println(address.toString())
                if (!address.isLoopbackAddress && address is Inet4Address) {
                    host = address.hostAddress
                }
            }
            host
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        connectionId = this.intent.extras?.getInt("connectionId")
        connectionId?.let {
            val connection = databaseHelper.getConnection(it)
            titleLabel.text = getString(R.string.edit_connection)
            nicknameInput.setText(connection?.nickname)
            hostInput.setText(connection?.host)
            macInput.setText(connection?.mac)
            wolInput.setText(connection?.portWol)
            portInput.setText(connection?.portDev)
            enterButton.isEnabled = true
        }

        enterButton.setOnClickListener { enterAction() }

        hostInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                hasTextHost = charSequence.isNotEmpty()
                checkFields()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        wolInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) {
                hasTextPortWol = sequence.isNotEmpty()
                checkFields()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        macInput.addTextChangedListener(object : TextWatcher {
            var noColons: String = ""

            override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) {
                noColons = sequence.toString().replace(":", "")
            }

            override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) {
                val noColLength = noColons.length
                val length = sequence.toString().length
                if ((noColLength % 2 != 0 && noColLength != 0 && noColLength < 17
                                && length > 0) && sequence.toString()[length - 1] != ':') {
                    macInput.setText("$sequence:")
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (macInput.length() > 0) {
                    macInput.setSelection(macInput.length())
                }
            }
        })
    }

    private fun checkFields() {
        enterButton.isEnabled = hasTextHost && hasTextPortWol
    }

    private fun enterAction() {
        enterButton.isEnabled = false
        nickname = nicknameInput.text.toString()
        host = hostInput.text.toString()
        portWol = wolInput.text.toString()
        devicePort = portInput.text.toString()
        val message: String
        val tempMac = macInput.text.toString().toUpperCase(Locale.ROOT).also {
            mac = it
        }
        if (macIsValid(tempMac)) {
            val disposable = ipAddress
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { this.makeLocationAndStatusRequest(it) },
                            { throwable -> Log.e("ERROR", throwable.localizedMessage) }
                    )
            disposables.add(disposable)
        } else {
            message = resources.getString(R.string.mac_address_invalid)
            Toast.makeText(this@ConnectionActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun makeLocationAndStatusRequest(ipAddress: String) {
        val locationRequest = LocationServiceProvider.locationService.getLocation(ipAddress)
        val statusRequest = isRunning(ipAddress, Integer.parseInt(devicePort))

        val disposable = Single.zip<Location, Boolean, Pair<Location, Boolean>>(
                locationRequest,
                statusRequest,
                BiFunction<Location, Boolean, Pair<Location, Boolean>> { a, b -> Pair.create(a, b) }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { this.onLocationStatusSuccess(it) },
                        { throwable -> Log.e("ERROR", throwable.localizedMessage) }
                )
        disposables.add(disposable)
    }

    private fun onLocationStatusSuccess(result: Pair<Location, Boolean>) {
        val location = result.first
        val city = location?.city
        val state = location?.regionCode
        val country = location?.countryName

        val connection = Connection(
                nickname,
                host,
                mac,
                portWol,
                devicePort,
                city,
                state,
                country,
                result.second.toString(), "",
                connectionId ?: -1)

        val disposable = if (connectionId != null) {
            databaseHelper.updateConnection(connection)
        } else {
            databaseHelper.insertConnection(connection)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            onFinish(resources.getString(if (connectionId != null) R.string.edit_connection_success else R.string.new_connection_success))
                        },
                        {
                            onFinish(resources.getString(if (connectionId != null) R.string.edit_connection_fail else R.string.new_connection_fail))
                        }
                )
        disposables.add(disposable)
    }

    private fun onFinish(message: String) {
        Toast.makeText(this@ConnectionActivity, message, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    private fun macIsValid(mac: String): Boolean {
        val p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
        val m = p.matcher(mac)
        return m.matches()
    }
}
