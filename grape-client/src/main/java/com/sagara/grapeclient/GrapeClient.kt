package com.sagara.grapeclient

import android.util.Log
import com.github.mervick.aes_everywhere.legacy.Aes256
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

abstract class GrapeClient(private val config: GrapeConfig) {

    private val tag = "grape-client"
    private val socket: Socket

    init {
        val options = IO.Options()
        options.forceNew = true
        options.reconnection = true
        socket = IO.socket(config.hostname, options)
        socket.on(Socket.EVENT_CONNECT) {
            socket.emit("register", encrypt(config.deviceID))
        }.on("onMessage") {
            if (it.isNotEmpty()) {
                val content = it[0].toString()
                val raw = decrypt(content)
                val jsonObject = JSONObject(raw)
                if (!jsonObject.isNull("id") || !jsonObject.isNull("message")) {
                    onMessageReceived(jsonObject.getString("message"))
                    socket.emit("report", encrypt(jsonObject.getString("id")))
                }
            }
            Log.i(tag, "onMessage event")
        }.on(Socket.EVENT_DISCONNECT) {
            Log.i(tag, "socket disconnected")
        }.on(Socket.EVENT_ERROR) {
            Log.e(tag, "socket error cause something!")
        }
    }

    abstract fun onMessageReceived(payload: String)

    private fun decrypt(content: String): String {
        return Aes256.decrypt(content, config.token)
    }

    private fun encrypt(content: String): String {
        return Aes256.encrypt(content, config.token)
    }

    fun connect() {
        socket.connect()
    }
}