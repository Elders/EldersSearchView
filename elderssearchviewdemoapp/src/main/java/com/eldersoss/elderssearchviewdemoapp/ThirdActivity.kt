package com.eldersoss.elderssearchviewdemoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eldersoss.elderssearchviewdemoapp.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {

    var binding: ActivityThirdBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityThirdBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        val phrase: String? = intent.getStringExtra(KEY_SEARCHED_PHRASE)

        phrase?.let {
            binding?.eldersSearchBar?.setSearchedPhrase(it)
        }
    }

    companion object {
        const val KEY_SEARCHED_PHRASE = "KeySearchedPhrase"
    }
}
