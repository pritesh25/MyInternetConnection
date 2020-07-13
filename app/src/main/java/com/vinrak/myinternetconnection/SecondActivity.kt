package com.vinrak.myinternetconnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.log


class SecondActivity : AppCompatActivity() {

    private val mTag = "SecondActivity"

    private var connectivityManager: ConnectivityManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        Log.d(mTag, "Second launched")
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) /* api 24 or above */ {
            connectivityManager?.registerDefaultNetworkCallback(networkCallback)
        } else {
            val networkInfo: NetworkInfo = connectivityManager?.activeNetworkInfo!!
            if (networkInfo.isConnected) {
                Log.d(mTag, "connected to internet")
            } else {
                Log.d(mTag, "connected to internet")
            }
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d(mTag, "Default -> Network Available")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d(mTag, "Default -> Connection lost")
        }
    }

    override fun onPause() {
        super.onPause()
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }
}