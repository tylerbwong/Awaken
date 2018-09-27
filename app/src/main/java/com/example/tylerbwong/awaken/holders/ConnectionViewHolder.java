package com.example.tylerbwong.awaken.holders;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.activities.NewConnectionActivity;
import com.example.tylerbwong.awaken.database.ConnectionDatabaseHelper;
import com.example.tylerbwong.awaken.fragments.ConnectionRefresher;
import com.example.tylerbwong.awaken.network.Wake;
import com.example.tylerbwong.awaken.utilities.AnimatedRecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
    public ImageButton mDeleteButton;

    private int mConnectionId;
    private final static String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    private ConnectionDatabaseHelper mDatabaseHelper;
    private ConnectionRefresher mRefresher;

    @SuppressWarnings("CheckResult")
    public ConnectionViewHolder(View view, final AnimatedRecyclerView recyclerView, ConnectionRefresher refresher) {
        super(view);

        mRefresher = refresher;

        mNickname = view.findViewById(R.id.nickname_label);
        mHost = view.findViewById(R.id.host_label);
        mMac = view.findViewById(R.id.mac_label);
        mLocation = view.findViewById(R.id.location_label);
        mStatus = view.findViewById(R.id.status_marker);
        mDate = view.findViewById(R.id.awoken_date_label);
        mEditButton = view.findViewById(R.id.edit_button);
        mDeleteButton = view.findViewById(R.id.delete_button);
        mDatabaseHelper = new ConnectionDatabaseHelper(view.getContext());

        view.setOnClickListener(itemView ->
                Wake.INSTANCE.sendPacket(mHost.getText().toString(), mMac.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                            Date date = new Date();
                            String formatDate = dateFormat.format(date);
                            mDatabaseHelper.updateDate(mConnectionId, formatDate);
                            recyclerView.setIsAnimatable(false);
                            mDate.setText(formatDate);
                            Snackbar.make(itemView, mNickname.getText().toString() + " "
                                            + itemView.getResources().getString(R.string.woken),
                                    Snackbar.LENGTH_LONG).show();
                        }));

        view.setOnLongClickListener(itemView -> true);

        mEditButton.setOnClickListener(itemView -> {
            Intent mainIntent = new Intent(itemView.getContext(), NewConnectionActivity.class);
            itemView.getContext().startActivity(mainIntent);
        });

        mDeleteButton.setOnClickListener(itemView -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(), R.style.AlertDialog);
            builder.setMessage("Are you sure you want to delete " + mNickname.getText().toString() + "?");
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                String message;
                try {
                    mDatabaseHelper.deleteConnection(mConnectionId);
                    message = "Successfully deleted " + mNickname.getText().toString();
                    mRefresher.refreshConnections();
                }
                catch (Exception e) {
                    message = "Failed to delete " + mNickname.getText().toString();
                    Log.e("failure", e.getMessage());
                }
                Toast.makeText(itemView.getContext(), message, Toast.LENGTH_LONG).show();
            });
            builder.create();
            builder.show();
        });
    }

    public void setId(int id) {
        this.mConnectionId = id;
    }
}
