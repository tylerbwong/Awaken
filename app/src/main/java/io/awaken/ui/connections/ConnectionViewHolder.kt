package io.awaken.ui.connections

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.awaken.R
import io.awaken.data.database.ConnectionDatabaseProvider
import io.awaken.data.network.Wake
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
    private val disposables = CompositeDisposable()

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

        view.setOnLongClickListener { _ -> true }

        editButton.setOnClickListener { itemView ->
            val mainIntent = Intent(itemView.context, NewConnectionActivity::class.java)
            itemView.context.startActivity(mainIntent)
        }

        deleteButton.setOnClickListener { itemView ->
            val builder = AlertDialog.Builder(itemView.context, R.style.AlertDialog)
            builder.setMessage("Are you sure you want to delete " + nickname.text.toString() + "?")
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                var message: String
                var connection: List<String>? = null
                try {
                    connection = databaseHelper.getConnection(connectionId)
                    databaseHelper.deleteConnection(connectionId)
                    message = "Successfully deleted " + nickname.text.toString()
                    refreshListener.invoke()
                } catch (e: Exception) {
                    message = "Failed to delete " + nickname.text.toString()
                    Log.e("failure", e.message)
                }

                Snackbar.make(itemView, message, Snackbar.LENGTH_LONG)
                        .setAction("UNDO") { _ ->
                            val disposable = databaseHelper.insertConnection(connection?.get(0),
                                    connection?.get(1),
                                    connection?.get(2),
                                    connection?.get(3),
                                    connection?.get(4),
                                    connection?.get(5),
                                    connection?.get(6),
                                    connection?.get(7),
                                    connection?.get(8),
                                    connection?.get(9))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe()
                            disposables.add(disposable)
                            refreshListener.invoke()
                        }.show()
            }
            builder.create()
            builder.show()
        }
    }

    companion object {
        private const val DATE_FORMAT = "MM/dd/yyyy HH:mm:ss"
    }
}
