package com.adnanansari.webcam

/**
 * WebSocket server implementation for camera streaming
 * Developed by Adnan Ansari (github.com/26Adnanansari)
 */

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class SimpleWebSocketServer(port: Int) : WebSocketServer(InetSocketAddress(port)) {
    private val connections = mutableSetOf<WebSocket>()
    
    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        connections.add(conn)
        Log.d("WebSocket", "New connection: ${conn.remoteSocketAddress}")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        connections.remove(conn)
        Log.d("WebSocket", "Closed connection: $reason")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        // Handle incoming messages if needed
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        Log.e("WebSocket", "Error: ${ex.message}")
    }

    override fun onStart() {
        Log.d("WebSocket", "Server started on port: $port")
    }

    fun broadcast(message: String) {
        connections.forEach { conn ->
            try {
                conn.send(message)
            } catch (e: Exception) {
                Log.e("WebSocket", "Send error: ${e.message}")
                connections.remove(conn)
            }
        }
    }
}
