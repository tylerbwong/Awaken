package io.awaken.ui.connections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.awaken.R
import io.awaken.data.model.Connection

typealias ConnectionsRefreshListener = () -> Unit

internal class ConnectionsAdapter(
        private val refreshListener: ConnectionsRefreshListener
) : RecyclerView.Adapter<ConnectionViewHolder>() {

    private var connections = listOf<Connection>()

    fun setConnections(connections: List<Connection>) {
        this.connections = connections
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.connection_card, parent, false)

        return ConnectionViewHolder(view, refreshListener)
    }

    override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
        val curConnection = connections[position]

        holder.nickname.text = curConnection.nickname
        holder.host.text = curConnection.host
        holder.mac.text = curConnection.mac
        holder.location.text = curConnection.city + ", " + curConnection.state
        holder.date.text = curConnection.date
        val status = curConnection.status.toBoolean()
        if (status) {
            holder.status.setImageResource(R.drawable.active_marker)
        } else {
            holder.status.setImageResource(R.drawable.inactive_marker)
        }
        holder.connectionId = curConnection.id
    }

    override fun getItemCount() = connections.size
}

