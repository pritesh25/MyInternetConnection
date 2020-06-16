package com.vinrak.myinternetconnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val mTAG = "MainActivity"
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    }

    override fun onResume() {
        super.onResume()

        connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build(), networkCallback
        )

    }

    override fun onStop() {
        super.onStop()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Log.d(mTAG, "(onStop) catch error = ${e.message}")
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(mTAG, "internet available")
            /* network.getAllByName("google.com").apply {
                 if (this.isNotEmpty()) {
                     for (i in this.indices) {
                         Log.d(mTAG, "i = $i")
                     }
                 } else {
                     Log.d(mTAG, "it is empty")
                 }
             }*/
/*            val ipAddress = InetAddress.getByName("google.com")
            Log.d(mTAG, "ipAddress = $ipAddress.")*/
            Log.d(mTAG,"status = ${checkStatus()}")
        }

        override fun onLost(network: Network) {
            Log.d(mTAG, "internet not available")
            /*network.getAllByName("google.com").apply {
                if (this.isNotEmpty()) {
                    for (i in this.indices) {
                        Log.d(mTAG, "i = $i")
                    }
                } else {
                    Log.d(mTAG, "it is empty")
                }
            }*/
            /*val ipAddress = InetAddress.getByName("google.com")
            Log.d(mTAG, "ipAddress = $ipAddress")*/
            Log.d(mTAG,"status = ${checkStatus()}")
        }
    }

    fun checkStatus(): Boolean {
        val command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }

}