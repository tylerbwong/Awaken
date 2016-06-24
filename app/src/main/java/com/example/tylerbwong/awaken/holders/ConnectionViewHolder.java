package com.example.tylerbwong.awaken.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.network.Wake;

/**
 * @author Tyler Wong
 */
public class ConnectionViewHolder extends RecyclerView.ViewHolder {
   public TextView mNickname;
   public TextView mHost;
   public TextView mMac;
   public TextView mLocation;
   public TextView mStatus;

   public ConnectionViewHolder(View view) {
      super(view);

      mNickname = (TextView) view.findViewById(R.id.nickname_label);
      mHost = (TextView) view.findViewById(R.id.host_label);
      mMac = (TextView) view.findViewById(R.id.mac_label);
      mLocation = (TextView) view.findViewById(R.id.location_label);
      mStatus = (TextView) view.findViewById(R.id.status_label);

      view.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Wake wake = new Wake(mHost.getText().toString(), mMac.getText().toString());
            wake.sendPacket();
         }
      });
   }
}
