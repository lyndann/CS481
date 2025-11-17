package com.example.cs481

import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.widget.ImageView
import android.net.Uri
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.widget.ProgressBar
import android.widget.TextView



class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Character button
        val chooseBtn = findViewById<Button>(R.id.chooseCharacterBtn)
        chooseBtn.setOnClickListener {
            startActivity(Intent(this, CharacterSelectActivity::class.java))
        }

        // Load saved GIF character
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val selected = prefs.getString("selected_character", null)
        val selectedImage = findViewById<ImageView>(R.id.selectedCharacterImage)
        val hunger = prefs.getInt("hunger", 80)

        val homeHungerBar = findViewById<ProgressBar>(R.id.homeHungerBar)
        homeHungerBar.progress = hunger

        if (selected != null) {
            val gifRes = when (selected) {
                "bugcat" -> R.drawable.bugcat
                "purplecat" -> R.drawable.purplecat
                else -> null
            }
            if (gifRes != null) {
                Glide.with(this).asGif().load(gifRes).into(selectedImage)
            }
        }

        // Button listeners
        findViewById<Button>(R.id.btnTask1).setOnClickListener {
            Toast.makeText(this, "Task 1", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnTask2).setOnClickListener {
            Toast.makeText(this, "Task 2", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnTask3).setOnClickListener {
            Toast.makeText(this, "Task 3", Toast.LENGTH_SHORT).show()
        }


        // Load mood based on hunger
        val moodTextHome = findViewById<TextView>(R.id.tvMoodStatusHome)
        val hungerValue = prefs.getInt("hunger", 80)

        moodTextHome.text = when {
            hungerValue >= 80 -> "😄 Happy"
            hungerValue >= 40 -> "🙂 Okay"
            else -> "😢 Sad"
        }

    }
    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)

        // Reload hunger
        val hunger = prefs.getInt("hunger", 80)
        val homeHungerBar = findViewById<ProgressBar>(R.id.homeHungerBar)
        homeHungerBar.progress = hunger

        // Reload mood
        val moodTextHome = findViewById<TextView>(R.id.tvMoodStatusHome)
        moodTextHome.text = when {
            hunger >= 80 -> "😄 Happy"
            hunger >= 40 -> "🙂 Okay"
            else -> "😢 Sad"
        }

        // ✅ Reload character GIF (the missing piece!)
        val selectedImage = findViewById<ImageView>(R.id.selectedCharacterImage)
        val selected = prefs.getString("selected_character", "bugcat")

        val gifRes = when (selected) {
            "bugcat" -> R.drawable.bugcat
            "purplecat" -> R.drawable.purplecat
            else -> null
        }

        if (gifRes != null) {
            Glide.with(this).asGif().load(gifRes).into(selectedImage)
        }
    }



}
