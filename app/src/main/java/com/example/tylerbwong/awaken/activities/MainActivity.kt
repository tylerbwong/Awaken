package com.example.tylerbwong.awaken.activities

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager

import com.example.tylerbwong.awaken.R
import com.example.tylerbwong.awaken.fragments.ConnectionsFragment

import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * @author Tyler Wong
 */
class MainActivity : AppCompatActivity() {

    private var currentFragment: Fragment? = null
    private var fragmentTransaction: FragmentTransaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        navigationView.menu.getItem(0).isChecked = true
        val connectionsFragment = ConnectionsFragment()
        currentFragment = connectionsFragment
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction?.replace(R.id.frame, connectionsFragment)
        fragmentTransaction?.commit()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.connections -> {
                    val connectionsFragment = ConnectionsFragment()
                    currentFragment = connectionsFragment
                    fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction?.replace(R.id.frame, connectionsFragment)
                    fragmentTransaction?.commit()
                    true
                }
                else -> false
            }
        }

        val actionBarDrawerToggle = object : ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                hideKeyboard()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                hideKeyboard()
            }
        }

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun hideKeyboard() {
        val view = currentFocus

        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onBackPressed() {

    }
}
