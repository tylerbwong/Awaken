package io.awaken.ui.connections;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import io.awaken.R;
import io.awaken.data.database.ConnectionDatabaseHelper;
import io.awaken.data.network.Wake;
import io.awaken.ui.utils.AnimatedRecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Tyler Wong
 */
public class ConnectionViewHolder extends RecyclerView.ViewHolder {

    TextView mNickname;
    TextView mHost;
    TextView mMac;
    TextView mLocation;
    TextView mDate;
    ImageView mStatus;

    private int mConnectionId;
    private final static String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    private ConnectionDatabaseHelper mDatabaseHelper;
    private ConnectionRefresher mRefresher;

    @SuppressWarnings("CheckResult")
    ConnectionViewHolder(View view, final AnimatedRecyclerView recyclerView, ConnectionRefresher refresher) {
        super(view);

        mRefresher = refresher;

        Button editButton = ViewCompat.requireViewById(itemView, R.id.edit_button);
        Button deleteButton = ViewCompat.requireViewById(itemView, R.id.delete_button);

        mNickname = ViewCompat.requireViewById(itemView, R.id.nickname_label);
        mHost = ViewCompat.requireViewById(itemView, R.id.host_label);
        mMac = ViewCompat.requireViewById(itemView, R.id.mac_label);
        mLocation = ViewCompat.requireViewById(itemView, R.id.location_label);
        mStatus = ViewCompat.requireViewById(itemView, R.id.status_marker);
        mDate = ViewCompat.requireViewById(itemView, R.id.awoken_date_label);
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

        editButton.setOnClickListener(itemView -> {
            Intent mainIntent = new Intent(itemView.getContext(), NewConnectionActivity.class);
            itemView.getContext().startActivity(mainIntent);
        });

        deleteButton.setOnClickListener(itemView -> {
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
