package com.eldersoss.elderssearchviewdemoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_first_demo.setOnClickListener(click)
        button_second_demo.setOnClickListener(click)
        button_third_demo.setOnClickListener(click)
        button_fourth_demo?.setOnClickListener(click)
        button_fifth_demo.setOnClickListener(click)
        button_sixth_demo.setOnClickListener(click)
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
