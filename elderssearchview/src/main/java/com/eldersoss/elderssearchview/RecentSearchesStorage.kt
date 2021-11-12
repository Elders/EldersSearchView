/*
 * Copyright (c) 2018. Elders LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eldersoss.elderssearchview

import android.content.Context
import android.util.Log
import java.io.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Created by IvanVatov on 5/16/2018.
 */
internal class RecentSearchesStorage(private val context: Context, private val logfile: String) {

    private val newLineSeparator = System.getProperty("line.separator")

    private val lock = ReentrantLock()
    private var searches: ArrayList<String>? = null

    init {
        workerThread.run { readSearches() }
    }

    fun addSearch(word: String) {
        if (searches?.contains(word) == true) {
            searches?.remove(word)
            searches?.add(word)
            workerThread.run { rebuildSearches() }
        } else {
            searches?.add(word)
            workerThread.run { writeToSearches(word + newLineSeparator, true) }
        }
    }

    fun deleteSearch(key: String) {
        searches?.remove(key)
        workerThread.run { rebuildSearches() }
    }

    fun getSearches(): ArrayList<String> {
        if (searches != null) {
            return searches!!
        }
        return readSearches()
    }

    private fun readSearches(): ArrayList<String> {
        lock.withLock {
            val result = ArrayList<String>()
            var inputStream: InputStream? = null
            try {
                inputStream = context.openFileInput("$logfile.search")
                val input = BufferedReader(InputStreamReader(inputStream!!))
                input.forEachLine { result.add(it) }
            } catch (e: Exception) {
                e.message?.let {
                    Log.e(this.javaClass.name, it)
                }
            } finally {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.message?.let {
                        Log.e(this.javaClass.name, it)
                    }
                }
            }
            searches = result
            return result
        }
    }

    private fun writeToSearches(data: String, add: Boolean) {
        lock.withLock {
            try {
                val mode = if (add) Context.MODE_APPEND else Context.MODE_PRIVATE
                val outputStreamWriter = OutputStreamWriter(context.openFileOutput("$logfile.search", mode))
                outputStreamWriter.write(data)
                outputStreamWriter.close()
            } catch (e: IOException) {
                e.message?.let {
                    Log.e(this.javaClass.name, it)
                }
            }
        }
    }

    private fun rebuildSearches() {
        val sb = StringBuilder()
        searches?.forEach {
            sb.append(it + newLineSeparator)
        }
        writeToSearches(sb.toString(), false)
    }

    companion object {
        private val workerThread: Thread = Thread("EldersSearchView#0")
    }
}