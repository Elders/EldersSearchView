package com.eldersoss.elderssearchviewdemoapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.eldersoss.elderssearchviewdemoapp.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    var binding: ActivitySecondBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.buttonSetSearchedPhrase?.setOnClickListener { binding?.eldersSearchBar?.setSearchedPhrase(it.tag.toString()) }
        binding?.buttonSearchForPhrase?.setOnClickListener { binding?.eldersSearchBar?.searchForPhrase(it.tag.toString()) }

        binding?.eldersSearchBar?.setOnSearchListener {

            val intent = Intent(this, ThirdActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ThirdActivity.KEY_SEARCHED_PHRASE, it)
            intent.putExtras(bundle)
            this.startActivity(intent)

            Handler(Looper.getMainLooper()).postDelayed(
                // clear text from the search bar after some time
                { binding?.eldersSearchBar?.clearSearch() },
                1500
            )
        }
    }

    override fun onBackPressed() {
        if (binding?.eldersSearchBar?.clickBackButton() == true) {
            return
        }
        super.onBackPressed()
    }

}