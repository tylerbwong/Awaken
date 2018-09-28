package io.awaken.ui.connections;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.fabtransitionactivity.SheetLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.awaken.R;
import io.awaken.data.database.ConnectionDatabaseHelper;
import io.awaken.data.model.Connection;
import io.awaken.ui.utils.AnimatedRecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Tyler Wong
 */
public class ConnectionsFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener, ConnectionRefresher {

    private SheetLayout mSheetLayout;
    private SwipeRefreshLayout mRefreshLayout;
    private List<Connection> mConnections;

    private ConnectionsAdapter mConnectionsAdapter;

    private ConnectionDatabaseHelper mDatabaseHelper;

    private final static int REQUEST_CODE = 1;
    private final static int DURATION = 1000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connections_fragment, container, false);
        AnimatedRecyclerView connectionsList = ViewCompat.requireViewById(view, R.id.connection_list);
        FloatingActionButton fab = ViewCompat.requireViewById(view, R.id.fab);

        mSheetLayout = ViewCompat.requireViewById(view, R.id.bottom_sheet);
        mRefreshLayout = ViewCompat.requireViewById(view, R.id.refresh_layout);

        mDatabaseHelper = new ConnectionDatabaseHelper(getContext());

        mRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            refreshConnections();
            mRefreshLayout.setRefreshing(false);
        }, DURATION));

        fab.setOnClickListener(itemView -> onFabClick());

        mSheetLayout.setFab(fab);
        mSheetLayout.setFabAnimationEndListener(this);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.connections);
        }

        mConnections = mDatabaseHelper.getAllConnections();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        connectionsList.setLayoutManager(layoutManager);
        mConnectionsAdapter = new ConnectionsAdapter(connectionsList, mConnections, this);
        connectionsList.setAdapter(mConnectionsAdapter);

        return view;
    }

    private void onFabClick() {
        mSheetLayout.expandFab();
    }

    @SuppressWarnings("CheckResult")
    @Override
    public void refreshConnections() {
        for (int index = 0; index < mConnections.size(); index++) {
            final Connection connection = mConnections.get(index);
            final int connectionId = connection.getId();
            connection.isRunning()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            status -> mDatabaseHelper.updateStatus(connectionId, String.valueOf(status)),
                            throwable -> Log.e("ERROR", "Could not update status")
                    );
        }
        mConnections = mDatabaseHelper.getAllConnections();
        mConnectionsAdapter.setConnections(mConnections);
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(getContext(), NewConnectionActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mSheetLayout.contractFab();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshConnections();
    }
}
