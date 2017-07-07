package com.example.tylerbwong.awaken.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.tylerbwong.awaken.R;
import com.example.tylerbwong.awaken.fragments.ConnectionsFragment;

/**
 * @author Tyler Wong
 */
public class MainActivity extends AppCompatActivity {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private Fragment mCurrentFragment;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mNavigationView.getMenu().getItem(0).setChecked(true);
        ConnectionsFragment connectionsFragment = new ConnectionsFragment();
        mCurrentFragment = connectionsFragment;
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.frame, connectionsFragment);
        mFragmentTransaction.commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.connections:
                        ConnectionsFragment connectionsFragment = new ConnectionsFragment();
                        mCurrentFragment = connectionsFragment;
                        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        mFragmentTransaction.replace(R.id.frame, connectionsFragment);
                        mFragmentTransaction.commit();
                        return true;
                    default:
                        return false;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                hideKeyboard();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
            }
        };

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
