package io.awaken.ui.connections

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.awaken.R
import kotlinx.android.synthetic.main.connections_fragment.*

/**
 * @author Tyler Wong
 */
class ConnectionsFragment : Fragment() {
    private lateinit var viewModel: ConnectionsViewModel
    private lateinit var connectionsAdapter: ConnectionsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.connections_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout.setOnRefreshListener { viewModel.updateStatuses() }

        fab.setOnClickListener {
            val intent = Intent(context, ConnectionActivity::class.java)
            startActivity(intent)
        }

        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        actionBar?.setTitle(R.string.connections)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        connectionList.layoutManager = layoutManager
        connectionsAdapter = ConnectionsAdapter {
            viewModel.updateStatuses()
        }
        connectionList.adapter = connectionsAdapter

        viewModel = ViewModelProvider(this).get(ConnectionsViewModel::class.java).also {
            it.connections.observe(this, Observer { connections ->
                connectionsAdapter.setConnections(connections)
                refreshLayout.isRefreshing = false
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateStatuses()
    }
}
