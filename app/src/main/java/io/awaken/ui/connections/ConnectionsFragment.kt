package io.awaken.ui.connections

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.github.fabtransitionactivity.SheetLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.awaken.R
import io.awaken.data.database.ConnectionDatabaseHelper
import io.awaken.data.model.Connection
import io.awaken.fragments.ConnectionRefresher
import io.awaken.ui.utils.AnimatedRecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author Tyler Wong
 */
class ConnectionsFragment : Fragment(), SheetLayout.OnFabAnimationEndListener, ConnectionRefresher {

    private var sheetLayout: SheetLayout? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    private var connections: List<Connection>? = null

    private var connectionsAdapter: ConnectionsAdapter? = null

    private var databaseHelper: ConnectionDatabaseHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.connections_fragment, container, false)
        val connectionsList = ViewCompat.requireViewById<AnimatedRecyclerView>(view, R.id.connection_list)
        val fab = ViewCompat.requireViewById<FloatingActionButton>(view, R.id.fab)

        sheetLayout = ViewCompat.requireViewById(view, R.id.bottom_sheet)
        refreshLayout = ViewCompat.requireViewById(view, R.id.refresh_layout)

        databaseHelper = ConnectionDatabaseHelper(context)

        refreshLayout!!.setOnRefreshListener {
            Handler().postDelayed({
                refreshConnections()
                refreshLayout!!.isRefreshing = false
            }, DURATION.toLong())
        }

        fab.setOnClickListener { itemView -> onFabClick() }

        sheetLayout!!.setFab(fab)
        sheetLayout!!.setFabAnimationEndListener(this)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.connections)

        connections = databaseHelper!!.allConnections

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        connectionsList.layoutManager = layoutManager
        connectionsAdapter = ConnectionsAdapter(connectionsList, connections!!, this)
        connectionsList.adapter = connectionsAdapter

        return view
    }

    private fun onFabClick() {
        sheetLayout!!.expandFab()
    }

    override fun refreshConnections() {
        for (index in connections!!.indices) {
            val connection = connections!![index]
            val connectionId = connection.id
            connection.isRunning()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { status -> databaseHelper!!.updateStatus(connectionId, status.toString()) },
                            { throwable -> Log.e("ERROR", "Could not update status") }
                    )
        }
        connections = databaseHelper!!.allConnections
        connectionsAdapter!!.setConnections(connections!!)
    }

    override fun onFabAnimationEnd() {
        val intent = Intent(context, NewConnectionActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            sheetLayout!!.contractFab()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshConnections()
    }

    companion object {

        private val REQUEST_CODE = 1
        private val DURATION = 1000
    }
}
