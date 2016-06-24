package com.example.tylerbwong.awaken.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.github.fabtransitionactivity.SheetLayout;

import java.util.List;

/**
 * @author Tyler Wong
 */
public class ConnectionsFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener {

   private SheetLayout mSheetLayout;
   private FloatingActionButton mFab;
   private RecyclerView mConnectionsList;
   private SwipeRefreshLayout mRefreshLayout;
   LinearLayoutManager layoutManager;
   private LinearLayout mEmptyView;
   private List<Connection> connections;

   private ConnectionsAdapter connectionsAdapter;

   private ConnectionDatabaseHelper databaseHelper;

   private final static int REQUEST_CODE = 1;
   private final static int DURATION = 5000;

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.connections_fragment, container, false);

      mSheetLayout = (SheetLayout) view.findViewById(R.id.bottom_sheet);
      mConnectionsList = (RecyclerView) view.findViewById(R.id.connection_list);
      mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
      mFab = (FloatingActionButton) view.findViewById(R.id.fab);
      mEmptyView = (LinearLayout) view.findViewById(R.id.empty_layout);

      databaseHelper = new ConnectionDatabaseHelper(getContext());

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

      connections = databaseHelper.getAllConnections();

      layoutManager = new LinearLayoutManager(getContext());
      layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
      mConnectionsList.setLayoutManager(layoutManager);
      connectionsAdapter = new ConnectionsAdapter(getContext(), connections);
      mConnectionsList.setAdapter(connectionsAdapter);

      refreshConnections();

      return view;
   }

   private void onFabClick() {
      mSheetLayout.expandFab();
   }

   private void refreshConnections() {
      for (Connection connection : connections) {
         String status = String.valueOf(StatusUpdate.refreshStatus(connection.getHost(),
               Integer.valueOf(connection.getPortDev())));
         databaseHelper.updateStatus(connection.getMac(), status);
      }
      connections = databaseHelper.getAllConnections();
      connectionsAdapter = new ConnectionsAdapter(getContext(), connections);
      mConnectionsList.setAdapter(connectionsAdapter);
   }

   @Override
   public void onFabAnimationEnd() {
      Intent intent = new Intent(getContext(), NewConnectionActivity.class);
      startActivityForResult(intent, REQUEST_CODE);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if(requestCode == REQUEST_CODE){
         mSheetLayout.contractFab();
      }
   }
}
