package io.awaken.ui.connections

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.awaken.R
import io.awaken.data.database.ConnectionDatabaseHelper
import io.awaken.data.model.Connection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.connections_fragment.*

/**
 * @author Tyler Wong
 */
class ConnectionsFragment : Fragment(), ConnectionRefresher {

    private var connections = listOf<Connection>()

    private lateinit var databaseHelper: ConnectionDatabaseHelper
    private lateinit var connectionsAdapter: ConnectionsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.connections_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout.setOnRefreshListener { updateStatuses(connections) }

        fab.setOnClickListener { _ ->
            val intent = Intent(context, NewConnectionActivity::class.java)
            startActivity(intent)
        }

        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        actionBar?.setTitle(R.string.connections)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        connectionList.layoutManager = layoutManager
        connectionsAdapter = ConnectionsAdapter(connections, this)
        connectionList.adapter = connectionsAdapter

        databaseHelper = ConnectionDatabaseHelper(context)
    }

    override fun onResume() {
        super.onResume()
        refreshConnections()
    }

    @Suppress("CheckResult")
    override fun refreshConnections() {
        databaseHelper.allConnections
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            connections = it
                            connectionsAdapter.setConnections(it)
                            refreshLayout.isRefreshing = false
                        },
                        {
                            Log.e("ERROR", it.localizedMessage)
                            refreshLayout.isRefreshing = false
                        }
                )
    }

    @Suppress("CheckResult")
    private fun updateStatuses(connections: List<Connection>) {

        if (connections.isEmpty()) {
            refreshConnections()
        }

        connections.forEach { connection ->
            val connectionId = connection.id
            connection.isRunning()
                    .flatMap { databaseHelper.updateStatus(connectionId, it.toString()) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { _ -> refreshConnections() },
                            {
                                Log.e("ERROR", it.localizedMessage)
                                refreshLayout.isRefreshing = false
                            }
                    )
        }
    }
}
