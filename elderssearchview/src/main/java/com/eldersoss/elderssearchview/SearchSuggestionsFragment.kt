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

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment

/**
 * Created by IvanVatov on 5/28/2018.
 */
class SearchSuggestionsFragment : Fragment() {

    private var adapter: SearchSuggestionsAdapter? = null
    private var esvBackground: Int = 0
    private var esvElevation: Float = 0f
    private var esvMargin: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.esv_fragment_search_suggestions, container, false)

        val suggestionsListView = view.findViewById(R.id.suggestions_list_view) as ListView
        val suggestionsListViewLayoutParams = LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        )

        suggestionsListViewLayoutParams.setMargins(
                esvMargin,
                esvMargin,
                esvMargin,
                esvMargin
        )
        suggestionsListView.setBackgroundResource(esvBackground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            suggestionsListView.elevation = esvElevation
            suggestionsListView.z = esvElevation - 1f
        }

        suggestionsListView.layoutParams = suggestionsListViewLayoutParams
        suggestionsListView.adapter = adapter

        return view
    }

    companion object {
        fun create(adapter: SearchSuggestionsAdapter, esvBackground: Int, esvElevation: Float, esvMargin: Int): SearchSuggestionsFragment {
            val fragment = SearchSuggestionsFragment()
            fragment.adapter = adapter
            fragment.esvBackground = esvBackground
            fragment.esvElevation = esvElevation
            fragment.esvMargin = esvMargin

            return fragment
        }
    }
}