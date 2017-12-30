package com.example.tylerbwong.awaken.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.activities.NewConnectionActivity;
import com.example.tylerbwong.awaken.adapters.ConnectionsAdapter;
import com.example.tylerbwong.awaken.components.Connection;
import com.example.tylerbwong.awaken.database.ConnectionDatabaseHelper;
import com.example.tylerbwong.awaken.network.StatusUpdate;
import com.example.tylerbwong.awaken.utilities.AnimatedRecyclerView;
import com.github.fabtransitionactivity.SheetLayout;

import java.util.List;

/**
 * @author Tyler Wong
 */
public class ConnectionsFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener, ConnectionRefresher {

    private SheetLayout mSheetLayout;
    private FloatingActionButton mFab;
    private AnimatedRecyclerView mConnectionsList;
    private SwipeRefreshLayout mRefreshLayout;
    LinearLayoutManager mLayoutManager;
    private LinearLayout mEmptyView;
    private List<Connection> mConnections;

    private ConnectionsAdapter mConnectionsAdapter;

    private ConnectionDatabaseHelper mDatabaseHelper;

    private final static int REQUEST_CODE = 1;
    private final static int DURATION = 1000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connections_fragment, container, false);

        mSheetLayout = view.findViewById(R.id.bottom_sheet);
        mConnectionsList = view.findViewById(R.id.connection_list);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mFab = view.findViewById(R.id.fab);
        mEmptyView = view.findViewById(R.id.empty_layout);

        mDatabaseHelper = new ConnectionDatabaseHelper(getContext());

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshConnections();
                        mRefreshLayout.setRefreshing(false);
                    }
                }, DURATION);
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick();
            }
        });

        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.connections);
        }

        mConnections = mDatabaseHelper.getAllConnections();

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mConnectionsList.setLayoutManager(mLayoutManager);
        mConnectionsAdapter = new ConnectionsAdapter(mConnectionsList, mConnections, this);
        mConnectionsList.setAdapter(mConnectionsAdapter);

        return view;
    }

    private void onFabClick() {
        mSheetLayout.expandFab();
    }

    @Override
    public void refreshConnections() {
        for (int index = 0; index < mConnections.size(); index++) {
            String status = String.valueOf(StatusUpdate.getStatus(mConnections.get(index).getHost(),
                    Integer.valueOf(mConnections.get(index).getmPortDev())));
            mDatabaseHelper.updateStatus(mConnections.get(index).getId(), status);
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
