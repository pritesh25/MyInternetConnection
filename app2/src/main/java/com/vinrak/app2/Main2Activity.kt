package com.vinrak.app2

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vinrak.app2.lib.InternetAvailabilityChecker
import com.vinrak.app2.lib.InternetConnectivityListener


class Main2Activity : AppCompatActivity(), InternetConnectivityListener {

    private var mTvStatus: TextView? = null
    private var mInternetAvailabilityChecker: InternetAvailabilityChecker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mTvStatus = findViewById(R.id.tv_status);

        mInternetAvailabilityChecker = InternetAvailabilityChecker.instance
        mInternetAvailabilityChecker?.let {
            it.addInternetConnectivityListener(this)
        }?: kotlin.run {
            Log.d("Main2Activity","mInternetAvailabilityChecker is null")
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mInternetAvailabilityChecker!!.removeInternetConnectivityChangeListener(this)
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (isConnected) {
            mTvStatus!!.text = "connected"
            Log.d("Main2Activity","connected")
        } else {
            mTvStatus!!.text = "not connected"
            Log.d("Main2Activity","connected")
        }
    }

}
