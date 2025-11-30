package com.example.cs481

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.bumptech.glide.load.engine.DiskCacheStrategy


class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)

        // Make sure daily tasks get reset once per day
        checkDailyReset(prefs)

        // ---------- Background GIF ----------
        val bgImage = findViewById<ImageView>(R.id.homeBackgroundImage)

        Glide.with(this)
            .asGif()
            .load(R.drawable.sky_background)
            .into(bgImage)


        // ---------- Character GIF ----------
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

        // ---------- Mood + hunger + stats ----------
        val homeHungerBar = findViewById<ProgressBar>(R.id.homeHungerBar)
        val moodTextHome = findViewById<TextView>(R.id.tvMoodStatusHome)
        val tvTokenCountHome = findViewById<TextView>(R.id.tvTokenCountHome)
        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val tvTasksTracker = findViewById<TextView>(R.id.tvTasksTracker)

        val hunger = prefs.getInt("hunger", 80)
        homeHungerBar.progress = hunger

        moodTextHome.text = when {
            hunger >= 80 -> "😄 Happy"
            hunger >= 40 -> "🙂 Okay"
            else -> "😢 Sad"
        }

        val tokens = prefs.getInt("tokens", 0)
        val xp = prefs.getInt("xp", 0)
        val level = prefs.getInt("level", 1)
        tvTokenCountHome.text = "Tokens: $tokens"
        tvLevel.text = "Level $level (XP: $xp)"

        val done1 = prefs.getBoolean("task1_done", false)
        val done2 = prefs.getBoolean("task2_done", false)
        val done3 = prefs.getBoolean("task3_done", false)
        val completedCount = listOf(done1, done2, done3).count { it }
        tvTasksTracker.text = "Tasks completed today: $completedCount/3"

        // ---------- Choose character button ----------
        val chooseBtn = findViewById<Button>(R.id.chooseCharacterBtn)
        chooseBtn.setOnClickListener {
            startActivity(Intent(this, CharacterSelectActivity::class.java))
        }

        // ---------- Open Tasks button ----------
        val btnOpenTasks = findViewById<Button>(R.id.btnOpenTasks)
        btnOpenTasks.setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
        }

        // ---------- Petting animation on tap ----------
        selectedImage.setOnClickListener {
            // scale up a bit, then back down (boop)
            selectedImage.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(120)
                .withEndAction {
                    selectedImage.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start()
                }
                .start()

            // temporary "loved" mood text
            moodTextHome.text = "😻 Feeling loved"
        }
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)

        // Refresh hunger
        val hunger = prefs.getInt("hunger", 80)
        val homeHungerBar = findViewById<ProgressBar>(R.id.homeHungerBar)
        homeHungerBar.progress = hunger

        // Refresh mood
        val moodTextHome = findViewById<TextView>(R.id.tvMoodStatusHome)
        moodTextHome.text = when {
            hunger >= 80 -> "😄 Happy"
            hunger >= 40 -> "🙂 Okay"
            else -> "😢 Sad"
        }

        // Refresh tokens / level / tracker
        val tvTokenCountHome = findViewById<TextView>(R.id.tvTokenCountHome)
        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val tvTasksTracker = findViewById<TextView>(R.id.tvTasksTracker)

        val tokens = prefs.getInt("tokens", 0)
        val xp = prefs.getInt("xp", 0)
        val level = prefs.getInt("level", 1)
        tvTokenCountHome.text = "Tokens: $tokens"
        tvLevel.text = "Level $level (XP: $xp)"

        val done1 = prefs.getBoolean("task1_done", false)
        val done2 = prefs.getBoolean("task2_done", false)
        val done3 = prefs.getBoolean("task3_done", false)
        val completedCount = listOf(done1, done2, done3).count { it }
        tvTasksTracker.text = "Tasks completed today: $completedCount/3"

        // Reload character in case they changed it
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

    // Daily reset of task completion + slot flag
    private fun checkDailyReset(prefs: SharedPreferences) {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val today = sdf.format(Date())
        val last = prefs.getString("tasks_last_reset", null)

        if (last == null || last != today) {
            prefs.edit()
                .putBoolean("task1_done", false)
                .putBoolean("task2_done", false)
                .putBoolean("task3_done", false)
                .putBoolean("slot_played_today", false)
                .putString("tasks_last_reset", today)
                .apply()
        }
    }
}
