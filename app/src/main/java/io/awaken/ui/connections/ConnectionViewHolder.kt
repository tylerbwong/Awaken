package io.awaken.ui.connections

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.google.android.material.snackbar.Snackbar

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import io.awaken.R
import io.awaken.data.database.ConnectionDatabaseHelper
import io.awaken.data.network.Wake
import io.awaken.ui.utils.AnimatedRecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author Tyler Wong
 */
class ConnectionViewHolder(view: View, recyclerView: AnimatedRecyclerView, private val refresher: ConnectionRefresher) : RecyclerView.ViewHolder(view) {

    internal val nickname = ViewCompat.requireViewById<TextView>(itemView, R.id.nickname_label)
    internal val host = ViewCompat.requireViewById<TextView>(itemView, R.id.host_label)
    internal val mac = ViewCompat.requireViewById<TextView>(itemView, R.id.mac_label)
    internal val location = ViewCompat.requireViewById<TextView>(itemView, R.id.location_label)
    internal val date = ViewCompat.requireViewById<TextView>(itemView, R.id.awoken_date_label)
    internal val status = ViewCompat.requireViewById<ImageView>(itemView, R.id.status_marker)

    internal var connectionId: Int = 0
    private val databaseHelper = ConnectionDatabaseHelper(view.context)

    init {

        val editButton = ViewCompat.requireViewById<ImageButton>(itemView, R.id.edit_button)
        val deleteButton = ViewCompat.requireViewById<ImageButton>(itemView, R.id.delete_button)

        view.setOnClickListener { itemView ->
            Wake.sendPacket(host.text.toString(), mac.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
                        val date = Date()
                        val formatDate = dateFormat.format(date)
                        databaseHelper.updateDate(connectionId, formatDate)
                        recyclerView.setIsAnimatable(false)
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
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                var message: String
                try {
                    databaseHelper.deleteConnection(connectionId)
                    message = "Successfully deleted " + nickname.text.toString()
                    refresher.refreshConnections()
                } catch (e: Exception) {
                    message = "Failed to delete " + nickname.text.toString()
                    Log.e("failure", e.message)
                }

                Toast.makeText(itemView.context, message, Toast.LENGTH_LONG).show()
            }
            builder.create()
            builder.show()
        }
    }

    companion object {
        private const val DATE_FORMAT = "MM/dd/yyyy HH:mm:ss"
    }
}
