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

import android.content.Context
import android.content.IntentFilter
import com.vinrak.app2.lib.NetworkChangeReceiver.NetworkChangeListener
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by aa on 29/04/17.
 */
class InternetAvailabilityChecker private constructor(context: Context) :
    NetworkChangeListener {
    private val mContextWeakReference: WeakReference<Context>
    private val mInternetConnectivityListenersWeakReferences: MutableList<WeakReference<InternetConnectivityListener>>?
    private var mNetworkChangeReceiver: NetworkChangeReceiver? = null
    private var mIsNetworkChangeRegistered = false
    var currentInternetAvailabilityStatus = false
        private set
    private var isInitialConnectivityStatusKnow =
        false // this variable is to track if initial connectivity status has been calculated or not
    private var mCheckConnectivityCallback: TaskFinished<Boolean>? = null

    /**
     * Add InternetConnectivityListener only if it's not added. It keeps a weak reference to the listener.
     * So user should have a strong reference to that listener otherwise that will be garbage collected
     */
    fun addInternetConnectivityListener(internetConnectivityListener: InternetConnectivityListener?) {
        if (internetConnectivityListener == null) {
            return
        }
        mInternetConnectivityListenersWeakReferences!!.add(
            WeakReference(
                internetConnectivityListener
            )
        )
        if (mInternetConnectivityListenersWeakReferences.size == 1) {
            registerNetworkChangeReceiver()
            isInitialConnectivityStatusKnow = false
            return
        }
        publishInternetAvailabilityStatus(currentInternetAvailabilityStatus)
    }

    /**
     * remove the weak reference to the listener
     */
    fun removeInternetConnectivityChangeListener(internetConnectivityListener: InternetConnectivityListener?) {
        if (internetConnectivityListener == null) {
            return
        }
        if (mInternetConnectivityListenersWeakReferences == null) {
            return
        }
        val iterator =
            mInternetConnectivityListenersWeakReferences.iterator()
        while (iterator.hasNext()) {

            //if weak reference is null then remove it from iterator
            val reference =
                iterator.next()
            if (reference == null) {
                iterator.remove()
                continue
            }

            //if listener referenced by this weak reference is garbage collected then remove it from iterator
            val listener = reference.get()
            if (listener == null) {
                reference.clear()
                iterator.remove()
                continue
            }

            //if listener to be removed is found then remove it
            if (listener === internetConnectivityListener) {
                reference.clear()
                iterator.remove()
                break
            }
        }

        //if all listeners are removed then unregister NetworkChangeReceiver
        if (mInternetConnectivityListenersWeakReferences.size == 0) {
            unregisterNetworkChangeReceiver()
        }
    }

    fun removeAllInternetConnectivityChangeListeners() {
        if (mInternetConnectivityListenersWeakReferences == null) {
            return
        }
        val iterator =
            mInternetConnectivityListenersWeakReferences.iterator()
        while (iterator.hasNext()) {
            val reference =
                iterator.next()
            reference?.clear()
            iterator.remove()
        }
        unregisterNetworkChangeReceiver()
    }

    /**
     * registers a NetworkChangeReceiver if not registered already
     */
    private fun registerNetworkChangeReceiver() {
        val context = mContextWeakReference.get()
        if (context != null && !mIsNetworkChangeRegistered) {
            mNetworkChangeReceiver = NetworkChangeReceiver()
            mNetworkChangeReceiver!!.setNetworkChangeListener(this)
            context.registerReceiver(
                mNetworkChangeReceiver,
                IntentFilter(CONNECTIVITY_CHANGE_INTENT_ACTION)
            )
            mIsNetworkChangeRegistered = true
        }
    }

    /**
     * unregisters the already registered NetworkChangeReceiver
     */
    private fun unregisterNetworkChangeReceiver() {
        val context = mContextWeakReference.get()
        if (context != null && mNetworkChangeReceiver != null && mIsNetworkChangeRegistered) {
            try {
                context.unregisterReceiver(mNetworkChangeReceiver)
            } catch (exception: IllegalArgumentException) {
                //consume this exception
            }
            mNetworkChangeReceiver!!.removeNetworkChangeListener()
        }
        mNetworkChangeReceiver = null
        mIsNetworkChangeRegistered = false
        mCheckConnectivityCallback = null
    }




    override fun onNetworkChange(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            mCheckConnectivityCallback = object : TaskFinished<Boolean> {
                override fun onTaskFinished(data: Boolean) {
                    mCheckConnectivityCallback = null
                    if (!isInitialConnectivityStatusKnow || currentInternetAvailabilityStatus != data) {
                        publishInternetAvailabilityStatus(data)
                        isInitialConnectivityStatusKnow = true
                    }
                }
            }
            CheckInternetTask(mCheckConnectivityCallback!!).execute()
        } else {
            if (!isInitialConnectivityStatusKnow || currentInternetAvailabilityStatus) {
                publishInternetAvailabilityStatus(false)
                isInitialConnectivityStatusKnow = true
            }
        }
    }

    private fun publishInternetAvailabilityStatus(isInternetAvailable: Boolean) {
        currentInternetAvailabilityStatus = isInternetAvailable
        if (mInternetConnectivityListenersWeakReferences == null) {
            return
        }
        val iterator =
            mInternetConnectivityListenersWeakReferences.iterator()
        while (iterator.hasNext()) {
            val reference =
                iterator.next()
            if (reference == null) {
                iterator.remove()
                continue
            }
            val listener = reference.get()
            if (listener == null) {
                iterator.remove()
                continue
            }
            listener.onInternetConnectivityChanged(isInternetAvailable)
        }
        if (mInternetConnectivityListenersWeakReferences.size == 0) {
            unregisterNetworkChangeReceiver()
        }
    }

    companion object {
        private val LOCK = Any()

        @Volatile
        private var sInstance: InternetAvailabilityChecker? = null
        private const val CONNECTIVITY_CHANGE_INTENT_ACTION =
            "android.net.conn.CONNECTIVITY_CHANGE"

        /**
         * Call this function in application class to do initial setup. it returns singleton instance.
         *
         * @param context need to register for Connectivity broadcast
         * @return instance of InternetConnectivityHelper
         */
        @JvmStatic
        fun init(context: Context?): InternetAvailabilityChecker? {
            if (context == null) {
                throw NullPointerException("context can not be null")
            }
            if (sInstance == null) {
                synchronized(LOCK) {
                    if (sInstance == null) {
                        sInstance =
                            InternetAvailabilityChecker(context)
                    }
                }
            }
            return sInstance
        }

        @JvmStatic
        val instance: InternetAvailabilityChecker?
            get() {
                checkNotNull(sInstance) { "call init(Context) in your application class before calling getInstance()" }
                return sInstance
            }
    }

    init {
        val appContext = context.applicationContext
        mContextWeakReference = WeakReference(appContext)
        mInternetConnectivityListenersWeakReferences =
            ArrayList()
    }
}