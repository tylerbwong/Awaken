package com.example.tylerbwong.awaken.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.components.Connection;
import com.example.tylerbwong.awaken.fragments.ConnectionRefresher;
import com.example.tylerbwong.awaken.holders.ConnectionViewHolder;
import com.example.tylerbwong.awaken.utilities.AnimatedRecyclerView;

import java.util.List;

/**
 * @author Tyler Wong
 */
public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionViewHolder> {

    private List<Connection> mConnections;
    private AnimatedRecyclerView mRecyclerView;
    private ConnectionRefresher mRefresher;

    public ConnectionsAdapter(AnimatedRecyclerView recyclerView, List<Connection> connections, ConnectionRefresher refresher) {
        this.mRecyclerView = recyclerView;
        this.mConnections = connections;
        this.mRefresher = refresher;
    }

    public List<Connection> getConnections() {
        return mConnections;
    }

    public void setConnections(List<Connection> connections) {
        mConnections = connections;
        notifyDataSetChanged();
    }

    @Override
    public ConnectionViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.connection_card, parent, false);

        ConnectionViewHolder viewHolder = new ConnectionViewHolder(view, mRecyclerView, mRefresher);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ConnectionViewHolder holder, int position) {
        Connection curConnection = mConnections.get(position);

        holder.mNickname.setText(curConnection.getNickname());
        holder.mHost.setText(curConnection.getHost());
        holder.mMac.setText(curConnection.getMac());
        holder.mLocation.setText(curConnection.getCity() + ", " + curConnection.getState());
        holder.mDate.setText(curConnection.getDate());
        boolean status = Boolean.parseBoolean(curConnection.getStatus());
        if (status) {
            holder.mStatus.setImageResource(R.drawable.active_marker);
        } else {
            holder.mStatus.setImageResource(R.drawable.inactive_marker);
        }
        holder.setId(curConnection.getId());
    }

    @Override
    public int getItemCount() {
        return mConnections.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

