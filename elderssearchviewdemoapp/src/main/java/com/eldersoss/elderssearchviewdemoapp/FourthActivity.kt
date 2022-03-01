package com.eldersoss.elderssearchviewdemoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eldersoss.elderssearchviewdemoapp.databinding.ActivityFourthBinding

class FourthActivity : AppCompatActivity() {

    var binding: ActivityFourthBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFourthBinding.inflate(layoutInflater)

        setContentView(binding?.root)

    }
}
