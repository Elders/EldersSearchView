package com.eldersoss.elderssearchviewdemoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.eldersoss.elderssearchviewdemoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.buttonFirstDemo?.setOnClickListener(click)
        binding?.buttonSecondDemo?.setOnClickListener(click)
        binding?.buttonThirdDemo?.setOnClickListener(click)
        binding?.buttonFourthDemo?.setOnClickListener(click)
        binding?.buttonFifthDemo?.setOnClickListener(click)
        binding?.buttonSixthDemo?.setOnClickListener(click)
    }

    private val click: View.OnClickListener = View.OnClickListener {
        val cls: Class<*> = when (it.id) {
            R.id.button_second_demo -> SecondActivity::class.java
            R.id.button_third_demo -> ThirdActivity::class.java
            R.id.button_fourth_demo -> FourthActivity::class.java
            R.id.button_fifth_demo -> FifthActivity::class.java
            R.id.button_sixth_demo -> SixthActivity::class.java
            else -> FirstActivity::class.java
        }
        val intent = Intent(this, cls)
        this.startActivity(intent)
    }
}
