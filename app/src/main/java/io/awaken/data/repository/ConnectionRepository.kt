package io.awaken.data.repository

import io.awaken.data.ConnectionDao

class ConnectionRepository(connectionDao: ConnectionDao) {
    val connections = connectionDao.getAllConnections()
}
