package com.example.tylerbwong.awaken.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.components.Connection;
import com.example.tylerbwong.awaken.holders.ConnectionViewHolder;
import com.example.tylerbwong.awaken.network.StatusUpdate;

import java.util.List;

/**
 * @author Tyler Wong
 */
public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionViewHolder> {

   private List<Connection> connections;
   private Context mContext;

   public ConnectionsAdapter(Context context, List<Connection> connections) {
      this.mContext = context;
      this.connections = connections;
   }

   public List<Connection> getConnections() {
      return connections;
   }

   @Override
   public ConnectionViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
      View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.connection_card, parent, false);

      ConnectionViewHolder viewHolder = new ConnectionViewHolder(view);
      return viewHolder;
   }

   @Override
   public void onBindViewHolder(final ConnectionViewHolder holder, int position) {
      Connection curConnection = connections.get(position);

      holder.mNickname.setText(curConnection.getNickname());
      holder.mHost.setText(curConnection.getHost());
      holder.mMac.setText(curConnection.getMac());
      holder.mLocation.setText(curConnection.getCity() + ", " + curConnection.getState() + ", " +
            curConnection.getCountry());
      holder.mStatus.setText(StatusUpdate.getStatus(curConnection.getHost(),
            Integer.parseInt(curConnection.getPortDev())));
   }

   @Override
   public int getItemCount() {
      return connections.size();
   }

   @Override
   public void onAttachedToRecyclerView(RecyclerView recyclerView) {
      super.onAttachedToRecyclerView(recyclerView);
   }
}

