package com.example.cs481

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun setContentView(layoutResID: Int) {
        // Inflate the base layout first
        val baseLayout = layoutInflater.inflate(R.layout.activity_base, null)

        // Find the placeholder where child content goes
        val contentContainer = baseLayout.findViewById<FrameLayout>(R.id.contentContainer)

        // Inflate the child layout into the placeholder
        LayoutInflater.from(this).inflate(layoutResID, contentContainer, true)

        // Now set final combined layout
        super.setContentView(baseLayout)

        // Set up bottom nav buttons
        setupNavigation()
    }

    private fun setupNavigation() {
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnFood = findViewById<Button>(R.id.btnFood)

        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btnPlay.setOnClickListener {
            startActivity(Intent(this, PlayMenuActivity::class.java))
        }

        btnFood.setOnClickListener {
            startActivity(Intent(this, FoodActivity::class.java))
        }
    }
}
