package com.example.cs481

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

class FoodActivity : BaseActivity() {

    private lateinit var tvTokens: TextView
    private lateinit var tvFoodCount: TextView
    private lateinit var hungerBar: ProgressBar
    private lateinit var moodBar: ProgressBar
    private lateinit var tvMoodStatus: TextView
    private lateinit var btnBuy: Button
    private lateinit var btnFeed: Button

    // SharedPreferences for storing pet stats
    private val prefs by lazy {
        getSharedPreferences("game_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        tvTokens = findViewById(R.id.tvTokens)
        tvFoodCount = findViewById(R.id.tvFoodCount)
        hungerBar = findViewById(R.id.hungerBar)
        moodBar = findViewById(R.id.moodBar)
        tvMoodStatus = findViewById(R.id.tvMoodStatus)
        btnBuy = findViewById(R.id.btnBuyFood)
        btnFeed = findViewById(R.id.btnFeed)

        updateTimedHunger()   // drop hunger with time
        loadStats()           // load everything into UI

        btnBuy.setOnClickListener { buyFood() }
        btnFeed.setOnClickListener { feedPet() }
    }

    // Hunger decreases over time
    private fun updateTimedHunger() {
        val lastTime = prefs.getLong("last_hunger_update", System.currentTimeMillis())
        val currentTime = System.currentTimeMillis()

        val minutesPassed = ((currentTime - lastTime) / 60000).toInt()

        if (minutesPassed > 0) {
            var hunger = prefs.getInt("hunger", 80)   // default 80
            hunger -= minutesPassed
            hunger = hunger.coerceAtLeast(0)

            prefs.edit()
                .putInt("hunger", hunger)
                .putLong("last_hunger_update", currentTime)
                .apply()
        }
    }

    private fun loadStats() {
        val tokens = prefs.getInt("tokens", 0)
        val food = prefs.getInt("food_count", 0)
        val hunger = prefs.getInt("hunger", 80)

        val mood = hunger  // mood equals hunger level

        // Update UI
        tvTokens.text = "Tokens: $tokens"
        tvFoodCount.text = "Food Owned: $food"
        hungerBar.progress = hunger
        moodBar.progress = mood

        tvMoodStatus.text = when {
            mood >= 80 -> "Mood: Happy 😄"
            mood >= 40 -> "Mood: Okay 🙂"
            else -> "Mood: Sad 😢"
        }
    }

    private fun buyFood() {
        var tokens = prefs.getInt("tokens", 0)
        var food = prefs.getInt("food_count", 0)

        if (tokens < 10) {
            Toast.makeText(this, "Not enough tokens!", Toast.LENGTH_SHORT).show()
            return
        }

        tokens -= 10
        food += 1

        prefs.edit()
            .putInt("tokens", tokens)
            .putInt("food_count", food)
            .apply()

        loadStats()
        Toast.makeText(this, "Food purchased!", Toast.LENGTH_SHORT).show()
    }

    private fun feedPet() {
        var food = prefs.getInt("food_count", 0)
        var hunger = prefs.getInt("hunger", 80)

        if (food <= 0) {
            Toast.makeText(this, "No food available!", Toast.LENGTH_SHORT).show()
            return
        }

        if (hunger >= 100) {
            Toast.makeText(this, "Your pet is already full!", Toast.LENGTH_SHORT).show()
            return
        }

        food -= 1
        hunger = (hunger + 20).coerceAtMost(100)

        prefs.edit()
            .putInt("food_count", food)
            .putInt("hunger", hunger)
            .apply()

        loadStats()
        Toast.makeText(this, "Your pet has been fed!", Toast.LENGTH_SHORT).show()
    }
}
