package com.sagara.grapeandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sagara.grapeclient.GrapeClient
import com.sagara.grapeclient.GrapeConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = GrapeConfig("custom-id", "https://grape.suntuk.xyz", "token")
        val socket = object :GrapeClient(config){
            override fun onMessageReceived(payload: String) {
                Log.i("MainActivity", payload)
            }
        }
        socket.connect()
    }
}