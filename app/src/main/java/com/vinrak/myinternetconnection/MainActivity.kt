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
import java.io.IOException
import java.net.*


class MainActivity : AppCompatActivity() {

    private val mTAG = "MainActivity"
    private var connectivityManager: ConnectivityManager? = null
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        Log.d(mTAG, "isInternet = ${isInternet()}")

    }

    override fun onResume() {
        super.onResume()

        connectivityManager?.registerNetworkCallback(
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

            connectivityManager?.unregisterNetworkCallback(networkCallback)

        } catch (e: Exception) {
            Log.d(mTAG, "(onStop) catch error = ${e.message}")
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {

            Log.d(mTAG, "(onAvailable) isInternet = ${isInternet()}")

        }

        override fun onLost(network: Network) {

            Log.d(mTAG, "(onLost) isInternet = ${isInternet()}")
        }
    }

    fun isInternet(): Boolean {
        return try {
            val urlc: HttpURLConnection =
                URL("http://52.66.211.191:3000/").openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Test")
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = 1500
            try {
                urlc.connect()
            } catch (e: SocketTimeoutException) {
                Log.d(
                    mTAG,
                    "(SocketTimeoutException) Error checking internet connection = ${e.message}"
                )
            } catch (e: IOException) {
                Log.d(mTAG, "(IOException) Error checking internet connection = ${e.message}")
            } catch (e: URISyntaxException) {
                Log.d(
                    mTAG,
                    "(URISyntaxException) Error checking internet connection = ${e.message}"
                )
            } catch (e: UnknownHostException) {
                Log.d(
                    mTAG,
                    "(UnknownHostException) Error checking internet connection = ${e.message}"
                )
            }
            urlc.responseCode == 200
        } catch (e: Exception) {
            Log.d(mTAG, "(Exception) Error checking internet connection = ${e.message}")
            false
        } catch (e: ConnectException) {
            Log.d(mTAG, "(ConnectException2) Error checking internet connection = ${e.message}")
            false
        }
    }
}