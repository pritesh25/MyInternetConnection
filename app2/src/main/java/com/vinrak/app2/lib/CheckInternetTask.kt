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

import android.os.AsyncTask
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by aa on 29/04/17.
 */
//this async task tries to create a socket connection with google.com. If succeeds then return true otherwise false
internal open class CheckInternetTask(callback: TaskFinished<Boolean>) :
    AsyncTask<Void?, Void?, Boolean>() {
    private val mCallbackWeakReference: WeakReference<TaskFinished<Boolean>> = WeakReference(callback)
    override fun doInBackground(vararg params: Void?): Boolean? {
        return try {
            //parse url. if url is not parsed properly then return
            val url: URL
            url = try {
                URL("https://clients3.google.com/generate_204")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return false
            }

            //open connection. If fails return false
            val urlConnection: HttpURLConnection
            urlConnection = try {
                url.openConnection() as HttpURLConnection
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
            urlConnection.setRequestProperty("User-Agent", "Android")
            urlConnection.setRequestProperty("Connection", "close")
            urlConnection.connectTimeout = 1500
            urlConnection.readTimeout = 1500
            urlConnection.connect()
            urlConnection.responseCode == 204 && urlConnection.contentLength == 0
        } catch (e: IOException) {
            false
        }
    }

    override fun onPostExecute(isInternetAvailable: Boolean) {
        val callback = mCallbackWeakReference.get()
        callback?.onTaskFinished(isInternetAvailable)
    }

}