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

import java.text.DateFormat
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
class ConnectionViewHolder internal constructor(view: View, recyclerView: AnimatedRecyclerView, private val mRefresher: ConnectionRefresher) : RecyclerView.ViewHolder(view) {

    internal var mNickname: TextView
    internal var mHost: TextView
    internal var mMac: TextView
    internal var mLocation: TextView
    internal var mDate: TextView
    internal var mStatus: ImageView

    private var mConnectionId: Int = 0
    private val mDatabaseHelper: ConnectionDatabaseHelper

    init {

        val editButton = ViewCompat.requireViewById<ImageButton>(itemView, R.id.edit_button)
        val deleteButton = ViewCompat.requireViewById<ImageButton>(itemView, R.id.delete_button)

        mNickname = ViewCompat.requireViewById(itemView, R.id.nickname_label)
        mHost = ViewCompat.requireViewById(itemView, R.id.host_label)
        mMac = ViewCompat.requireViewById(itemView, R.id.mac_label)
        mLocation = ViewCompat.requireViewById(itemView, R.id.location_label)
        mStatus = ViewCompat.requireViewById(itemView, R.id.status_marker)
        mDate = ViewCompat.requireViewById(itemView, R.id.awoken_date_label)
        mDatabaseHelper = ConnectionDatabaseHelper(view.context)

        view.setOnClickListener { itemView ->
            Wake.sendPacket(mHost.text.toString(), mMac.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
                        val date = Date()
                        val formatDate = dateFormat.format(date)
                        mDatabaseHelper.updateDate(mConnectionId, formatDate)
                        recyclerView.setIsAnimatable(false)
                        mDate.text = formatDate
                        Snackbar.make(itemView, mNickname.text.toString() + " "
                                + itemView.resources.getString(R.string.woken),
                                Snackbar.LENGTH_LONG).show()
                    }
        }

        view.setOnLongClickListener { itemView -> true }

        editButton.setOnClickListener { itemView ->
            val mainIntent = Intent(itemView.context, NewConnectionActivity::class.java)
            itemView.context.startActivity(mainIntent)
        }

        deleteButton.setOnClickListener { itemView ->
            val builder = AlertDialog.Builder(itemView.context, R.style.AlertDialog)
            builder.setMessage("Are you sure you want to delete " + mNickname.text.toString() + "?")
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                var message: String
                try {
                    mDatabaseHelper.deleteConnection(mConnectionId)
                    message = "Successfully deleted " + mNickname.text.toString()
                    mRefresher.refreshConnections()
                } catch (e: Exception) {
                    message = "Failed to delete " + mNickname.text.toString()
                    Log.e("failure", e.message)
                }

                Toast.makeText(itemView.context, message, Toast.LENGTH_LONG).show()
            }
            builder.create()
            builder.show()
        }
    }

    fun setId(id: Int) {
        this.mConnectionId = id
    }

    companion object {
        private val DATE_FORMAT = "MM/dd/yyyy HH:mm:ss"
    }
}
