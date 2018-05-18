package com.eldersoss.elderssearchviewdemoapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.eldersoss.elderssearchview.EldersSearchView
import java.util.*

class MainActivity : AppCompatActivity() {

    private class SearchResult(val word: String, val result: String)

    private val searchResultsQueue = ArrayDeque<SearchResult>()
    private var currentSearchResult: SearchResult? = null

    private var eldersSearchView: EldersSearchView? = null

    private var textViewResult: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eldersSearchView = findViewById(R.id.elders_search_bar)
        textViewResult = findViewById(R.id.text_view_result)

        eldersSearchView?.filterButton?.setOnClickListener {
            Toast.makeText(this, "Show filter options", Toast.LENGTH_SHORT).show()
        }

        eldersSearchView?.setOnSearchListener {
            val searchResultFor = "Result for $it"
            if (currentSearchResult != null) {
                searchResultsQueue.add(currentSearchResult)
            }
            currentSearchResult = SearchResult(it, searchResultFor)
            Toast.makeText(this, "Search for: $it", Toast.LENGTH_SHORT).show()
            textViewResult?.text = searchResultFor
        }

        eldersSearchView?.setOnBackListener {
            val searchResult = searchResultsQueue.pollLast() // searchResult here can be null
            currentSearchResult = searchResult
            if (searchResult != null) {
                textViewResult?.text = searchResult.result
            } else {
                textViewResult?.text = getString(R.string.default_result)
            }
            searchResult?.word
        }
    }

}
