package com.eldersoss.elderssearchviewdemoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eldersoss.elderssearchviewdemoapp.databinding.ActivityFirstBinding
import java.util.*

class FirstActivity : AppCompatActivity() {

    private class SearchResult(val word: String, val result: String)

    private val searchResultsQueue = ArrayDeque<SearchResult>()
    private var currentSearchResult: SearchResult? = null

    var binding: ActivityFirstBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirstBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.buttonSetSearchedPhrase?.setOnClickListener { binding?.eldersSearchBar?.setSearchedPhrase(it.tag.toString()) }
        binding?.buttonSearchForPhrase?.setOnClickListener { binding?.eldersSearchBar?.searchForPhrase(it.tag.toString()) }

        binding?.eldersSearchBar?.filterButton?.setOnClickListener {
            Toast.makeText(this, "Show filter options", Toast.LENGTH_SHORT).show()
        }

        binding?.eldersSearchBar?.setOnSearchListener {
            //            val intent = Intent(this, SecondActivity::class.java)
//            this.startActivity(intent)
//            Handler().postDelayed(
//                    // clear search bar after 1,5 sec
//                    { eldersSearchView?.clearSearch() },
//                    1500)
            val searchResultFor = "Result for $it"
            if (currentSearchResult != null) {
                searchResultsQueue.add(currentSearchResult)
            }
            currentSearchResult = SearchResult(it, searchResultFor)
            Toast.makeText(this, "Search for: $it", Toast.LENGTH_SHORT).show()
            binding?.textViewResult?.text = searchResultFor
        }

        binding?.eldersSearchBar?.setOnBackListener {
            val searchResult = searchResultsQueue.pollLast() // searchResult here can be null
            currentSearchResult = searchResult
            if (searchResult != null) {
                binding?.textViewResult?.text = searchResult.result
            } else {
                binding?.textViewResult?.text = getString(R.string.default_result)
            }
            searchResult?.word
        }
    }

    override fun onBackPressed() {
        if (binding?.eldersSearchBar?.clickBackButton() == true) {
            return
        }
        super.onBackPressed()
    }

}
