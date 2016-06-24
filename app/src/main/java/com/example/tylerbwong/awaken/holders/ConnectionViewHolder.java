package com.example.tylerbwong.awaken.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
   public ImageView mStatus;
   public ImageButton mEditButton;

   public ConnectionViewHolder(View view) {
      super(view);

      mNickname = (TextView) view.findViewById(R.id.nickname_label);
      mHost = (TextView) view.findViewById(R.id.host_label);
      mMac = (TextView) view.findViewById(R.id.mac_label);
      mLocation = (TextView) view.findViewById(R.id.location_label);
      mStatus = (ImageView) view.findViewById(R.id.status_marker);
      mEditButton = (ImageButton) view.findViewById(R.id.edit_button);

      view.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Wake wake = new Wake(mHost.getText().toString(), mMac.getText().toString());
            wake.sendPacket();
         }
      });
   }
}
