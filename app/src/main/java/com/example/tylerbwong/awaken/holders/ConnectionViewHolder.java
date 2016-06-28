package com.example.tylerbwong.awaken.holders;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.database.ConnectionDatabaseHelper;
import com.example.tylerbwong.awaken.network.Wake;
import com.example.tylerbwong.awaken.utilities.AnimatedRecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Tyler Wong
 */
public class ConnectionViewHolder extends RecyclerView.ViewHolder {
   public TextView mNickname;
   public TextView mHost;
   public TextView mMac;
   public TextView mLocation;
   public TextView mDate;
   public ImageView mStatus;
   public ImageButton mEditButton;

   private int mConnectionId;
   private final static String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
   private ConnectionDatabaseHelper mDatabaseHelper;

   public ConnectionViewHolder(View view, final AnimatedRecyclerView recyclerView) {
      super(view);

      mNickname = (TextView) view.findViewById(R.id.nickname_label);
      mHost = (TextView) view.findViewById(R.id.host_label);
      mMac = (TextView) view.findViewById(R.id.mac_label);
      mLocation = (TextView) view.findViewById(R.id.location_label);
      mStatus = (ImageView) view.findViewById(R.id.status_marker);
      mDate = (TextView) view.findViewById(R.id.awoken_date_label);
      mEditButton = (ImageButton) view.findViewById(R.id.edit_button);

      view.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mDatabaseHelper = new ConnectionDatabaseHelper(view.getContext());
            Wake wake = new Wake(mHost.getText().toString(), mMac.getText().toString());
            wake.sendPacket();
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            Date date = new Date();
            String formatDate = dateFormat.format(date);
            mDatabaseHelper.updateDate(mConnectionId, formatDate);
            recyclerView.setIsAnimatable(false);
            mDate.setText(formatDate);
            Snackbar snackbar = Snackbar
                  .make(view, mNickname.getText().toString() + " " + view.getResources().getString(R.string.woken),
                        Snackbar.LENGTH_LONG);
            snackbar.show();
         }
      });

      view.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View view) {
            return true;
         }
      });
   }

   public void setId(int id) {
      this.mConnectionId = id;
   }
}
