package io.awaken.ui.connections

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.awaken.R
import io.awaken.data.database.ConnectionDatabaseProvider
import io.awaken.data.model.Connection
import io.awaken.data.network.Wake
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.connection_card.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Tyler Wong
 */
class ConnectionViewHolder(
        view: View,
        private val refreshListener: ConnectionsRefreshListener
) : RecyclerView.ViewHolder(view) {

    internal val nickname = itemView.nicknameLabel
    internal val host = itemView.hostLabel
    internal val mac = itemView.macLabel
    internal val location = itemView.locationLabel
    internal val date = itemView.awokenDateLabel
    internal val status = itemView.statusMarker

    internal var connectionId: Int = 0
    private val databaseHelper = ConnectionDatabaseProvider.databaseHelper

    init {

        val editButton = itemView.editButton
        val deleteButton = itemView.deleteButton

        view.setOnClickListener { itemView ->
            Wake.sendPacket(host.text.toString(), mac.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
                        val date = Date()
                        val formatDate = dateFormat.format(date)
                        databaseHelper.updateDate(connectionId, formatDate)
                        this.date.text = formatDate
                        Snackbar.make(itemView, nickname.text.toString() + " "
                                + itemView.resources.getString(R.string.woken),
                                Snackbar.LENGTH_LONG).show()
                    }
        }

        // Potential use for multi-select
        view.setOnLongClickListener { true }

        editButton.setOnClickListener { itemView ->
            val mainIntent = Intent(itemView.context, ConnectionActivity::class.java)
            mainIntent.putExtra("connectionId", connectionId)
            itemView.context.startActivity(mainIntent)
        }

        deleteButton.setOnClickListener { itemView ->
            val builder = AlertDialog.Builder(itemView.context, R.style.AlertDialog)
            builder.setMessage("Are you sure you want to delete " + nickname.text.toString() + "?")
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                var message: String
                var connection: Connection? = null
                var wasDeletionSuccessful = false
                try {
                    connection = databaseHelper.getConnection(connectionId)
                    databaseHelper.deleteConnection(connectionId)
                    message = "Successfully deleted " + nickname.text.toString()
                    refreshListener.invoke()
                    wasDeletionSuccessful = true
                } catch (e: Exception) {
                    message = "Failed to delete " + nickname.text.toString()
                    Log.e("failure", e.message)
                }

                val snackbar = Snackbar.make(itemView, message, Snackbar.LENGTH_LONG)
                if (wasDeletionSuccessful && connection != null) {
                    snackbar.setAction("UNDO") {
                        databaseHelper.insertConnection(connection)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        refreshListener::invoke
                                )
                    }
                }
                snackbar.show()
            }
            builder.create()
            builder.show()
        }
    }

    companion object {
        private const val DATE_FORMAT = "MM/dd/yyyy HH:mm:ss"
    }
}
