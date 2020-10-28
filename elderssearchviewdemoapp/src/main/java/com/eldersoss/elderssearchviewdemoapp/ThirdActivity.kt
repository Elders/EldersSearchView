package com.eldersoss.elderssearchviewdemoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eldersoss.elderssearchview.EldersSearchView

class ThirdActivity : AppCompatActivity() {

    private var eldersSearchView: EldersSearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        eldersSearchView = findViewById(R.id.elders_search_bar)

        val phrase: String? = intent.getStringExtra(KEY_SEARCHED_PHRASE)

        phrase?.let {
            eldersSearchView?.setSearchedPhrase(it)
        }

    }

    companion object {
        const val KEY_SEARCHED_PHRASE = "KeySearchedPhrase"
    }
}
