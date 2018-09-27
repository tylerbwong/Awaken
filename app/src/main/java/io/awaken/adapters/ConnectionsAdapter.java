package io.awaken.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.awaken.R;
import io.awaken.components.Connection;
import io.awaken.fragments.ConnectionRefresher;
import io.awaken.holders.ConnectionViewHolder;
import io.awaken.utilities.AnimatedRecyclerView;

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

