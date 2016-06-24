package com.example.tylerbwong.awaken.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.tylerbwong.awaken.database.ConnectionDatabaseHelper;
import com.github.fabtransitionactivity.SheetLayout;

/**
 * @author Tyler Wong
 */
public class ConnectionsFragment extends Fragment implements SheetLayout.OnFabAnimationEndListener {

   private SheetLayout mSheetLayout;
   private FloatingActionButton mFab;
   private RecyclerView mConnectionsList;
   private SwipeRefreshLayout mRefreshLayout;
   private LinearLayout mEmptyView;

   private ConnectionsAdapter connectionsAdapter;

   private ConnectionDatabaseHelper databaseHelper;

   private final static int REQUEST_CODE = 1;

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.connections_fragment, container, false);

      mSheetLayout = (SheetLayout) view.findViewById(R.id.bottom_sheet);
      mConnectionsList = (RecyclerView) view.findViewById(R.id.connection_list);
      mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
      mFab = (FloatingActionButton) view.findViewById(R.id.fab);
      mEmptyView = (LinearLayout) view.findViewById(R.id.empty_layout);

      mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
            // TODO refresh statuses
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

      databaseHelper = new ConnectionDatabaseHelper(getContext());

      LinearLayoutManager llm = new LinearLayoutManager(getContext());
      llm.setOrientation(LinearLayoutManager.VERTICAL);
      mConnectionsList.setLayoutManager(llm);
      connectionsAdapter = new ConnectionsAdapter(getContext(), databaseHelper.getAllConnections());
      mConnectionsList.setAdapter(connectionsAdapter);

      return view;
   }

   private void onFabClick() {
      mSheetLayout.expandFab();
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
