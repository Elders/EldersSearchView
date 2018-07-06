package com.eldersoss.elderssearchviewdemoapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.eldersoss.elderssearchview.EldersSearchView
import java.util.*

class SecondActivity : AppCompatActivity() {

    private class SearchResult(val word: String, val result: String)

    private val searchResultsQueue = ArrayDeque<SearchResult>()

    private var eldersSearchView: EldersSearchView? = null

    private var buttonSearchForText: Button? = null
    private var buttonSetText: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        eldersSearchView = findViewById(R.id.elders_search_bar)
        buttonSearchForText = findViewById(R.id.button_search_for_phrase)
        buttonSetText = findViewById(R.id.button_set_searched_phrase)

        buttonSetText?.setOnClickListener { eldersSearchView?.setSearchedPhrase(it.tag.toString()) }
        buttonSearchForText?.setOnClickListener { eldersSearchView?.searchForPhrase(it.tag.toString()) }

        eldersSearchView?.setOnSearchListener {

            val intent = Intent(this, ThirdActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ThirdActivity.KEY_SEARCHED_PHRASE, it)
            intent.putExtras(bundle)
            this.startActivity(intent)


            Handler().postDelayed(
                    // clear text from the search bar after some time
                    { eldersSearchView?.clearSearch() },
                    1500)

        }
    }

    override fun onBackPressed() {
        if (eldersSearchView?.clickBackButton() == true) {
            return
        }
        super.onBackPressed()
    }

}