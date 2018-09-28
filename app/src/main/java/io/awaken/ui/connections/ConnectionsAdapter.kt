package io.awaken.ui.connections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.awaken.R
import io.awaken.data.model.Connection
import io.awaken.ui.utils.AnimatedRecyclerView

/**
 * @author Tyler Wong
 */
internal class ConnectionsAdapter(private val recyclerView: AnimatedRecyclerView, private var connections: List<Connection>, private val refresher: ConnectionRefresher) : RecyclerView.Adapter<ConnectionViewHolder>() {

    fun getConnections(): List<Connection>? {
        return connections
    }

    fun setConnections(connections: List<Connection>) {
        this.connections = connections
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.connection_card, parent, false)

        return ConnectionViewHolder(view, recyclerView, refresher)
    }

    override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
        val curConnection = connections[position]

        holder.nickname.text = curConnection.nickname
        holder.host.text = curConnection.host
        holder.mac.text = curConnection.mac
        holder.location.text = curConnection.city + ", " + curConnection.state
        holder.date.text = curConnection.date
        val status = java.lang.Boolean.parseBoolean(curConnection.status)
        if (status) {
            holder.status.setImageResource(R.drawable.active_marker)
        } else {
            holder.status.setImageResource(R.drawable.inactive_marker)
        }
        holder.setId(curConnection.id)
    }

    override fun getItemCount(): Int {
        return connections.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }
}

