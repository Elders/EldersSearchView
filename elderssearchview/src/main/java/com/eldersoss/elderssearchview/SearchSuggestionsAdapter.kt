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

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

/**
 * Created by IvanVatov on 5/16/2018.
 */
class SearchSuggestionsAdapter(private val context: Context, private val callback: (String) -> Unit, logFile: String, private val iconsColor: Int, private val iconsWidth: Int) : BaseAdapter(), ListAdapter {

    private val storage = RecentSearchesStorage(context, logFile)
    private val filteredListItems = ArrayList<String>()
    private var filterChars: CharSequence = ""

    init {
        filterItems(filterChars)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var returnView = convertView

        if (returnView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            returnView = inflater.inflate(R.layout.esv_search_suggestions_row, null)
        }

        val historyIcon = returnView?.findViewById<ImageView>(R.id.recent_search_history_icon)
        historyIcon?.setColorFilter(iconsColor)
        historyIcon?.layoutParams?.width = iconsWidth

        val rowTextView = returnView?.findViewById<TextView>(R.id.recent_search_text)
        rowTextView?.text = getFilteredItems()[count - position - 1]
        rowTextView?.setTextColor(iconsColor)

        val removeButton = returnView?.findViewById<ImageView>(R.id.recent_search_remove_button)
        removeButton?.layoutParams?.width = iconsWidth

        removeButton?.setColorFilter(iconsColor)
        removeButton?.setOnClickListener {
            storage.deleteSearch(getFilteredItems()[count - position - 1])
            filterItems(filterChars)
        }

        returnView?.setOnClickListener { callback.invoke(getFilteredItems()[count - position - 1]) }

        return returnView!!
    }

    override fun getItem(position: Int): Any {
        return getFilteredItems()[count - position - 1]
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun getCount(): Int {
        return getFilteredItems().size
    }

    fun filterItems(chars: CharSequence) {
        filterChars = chars
        filteredListItems.clear()
        filteredListItems.addAll(storage.getSearches().filter { s -> s.contains(chars, true) })
        notifyDataSetChanged()
    }

    private fun getFilteredItems(): ArrayList<String> {
        return filteredListItems
    }

    fun addSearch(text: String) {
        storage.addSearch(text)
    }
}