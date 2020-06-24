package com.vinrak.app2


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vinrak.app2.lib.InternetAvailabilityChecker
import com.vinrak.app2.lib.InternetConnectivityListener


class MainActivity : AppCompatActivity(), InternetConnectivityListener {

    private var mTvStatus: TextView? = null
    private var mInternetAvailabilityChecker: InternetAvailabilityChecker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTvStatus = findViewById(R.id.tv_status)
        findViewById<Button>(R.id.btb_open_next_activity).setOnClickListener {
            startActivity(
                Intent(this, Main2Activity::class.java)
            )
        }

        mInternetAvailabilityChecker = InternetAvailabilityChecker.instance;
        mInternetAvailabilityChecker?.let {
            it.addInternetConnectivityListener(this)
        }?: kotlin.run {
            Log.d("MainActivity","mInternetAvailabilityChecker is null")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mInternetAvailabilityChecker!!.removeInternetConnectivityChangeListener(this)
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (isConnected) {
            mTvStatus!!.setText("connected");
            Log.d("MainActivity","connected")
        } else {
            mTvStatus!!.setText("not connected");
            Log.d("MainActivity","not connected")
        }
    }


}
