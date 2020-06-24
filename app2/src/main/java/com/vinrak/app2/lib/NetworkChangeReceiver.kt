/*
 *Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vinrak.app2.lib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import java.lang.ref.WeakReference

/**
 * Created by aa on 29/04/17.
 */
internal class NetworkChangeReceiver : BroadcastReceiver() {
    private var mNetworkChangeListenerWeakReference: WeakReference<NetworkChangeListener>? =
        null

    override fun onReceive(context: Context, intent: Intent) {
        val networkChangeListener =
            mNetworkChangeListenerWeakReference!!.get()
        networkChangeListener?.onNetworkChange(isNetworkConnected(context))
    }

    fun setNetworkChangeListener(networkChangeListener: NetworkChangeListener) {
        mNetworkChangeListenerWeakReference =
            WeakReference(networkChangeListener)
    }

    fun removeNetworkChangeListener() {
        if (mNetworkChangeListenerWeakReference != null) {
            mNetworkChangeListenerWeakReference!!.clear()
        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo

        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isAvailable && netInfo.isConnected
    }

    //Interface to send opt to listeners
    internal interface NetworkChangeListener {
        fun onNetworkChange(isNetworkAvailable: Boolean)
    }
}