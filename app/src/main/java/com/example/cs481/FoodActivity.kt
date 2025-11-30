package com.example.cs481

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

class FoodActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_food)

        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)

        val hungerBar = findViewById<ProgressBar>(R.id.foodHungerBar)
        val tvMood = findViewById<TextView>(R.id.tvMoodStatusFood)
        val tvTokens = findViewById<TextView>(R.id.tvTokenCountFood)
        val popup = findViewById<TextView>(R.id.tvFoodPopup)

        val btnSnack = findViewById<Button>(R.id.btnSnack)
        val btnMeal = findViewById<Button>(R.id.btnMeal)
        val btnFeast = findViewById<Button>(R.id.btnFeast)

        fun updateUI() {
            val hunger = prefs.getInt("hunger", 80)
            val tokens = prefs.getInt("tokens", 0)

            hungerBar.progress = hunger
            tvTokens.text = "Tokens: $tokens"

            tvMood.text = when {
                hunger >= 80 -> "😄 Happy"
                hunger >= 40 -> "🙂 Okay"
                else -> "😢 Hungry"
            }
        }

        fun showFoodPopup(text: String) {
            popup.text = text
            popup.alpha = 0f
            popup.translationY = 0f
            popup.visibility = View.VISIBLE

            popup.animate()
                .alpha(1f)
                .translationYBy(-40f)
                .setDuration(250)
                .withEndAction {
                    popup.animate()
                        .alpha(0f)
                        .translationYBy(40f)
                        .setDuration(250)
                        .withEndAction {
                            popup.visibility = View.GONE
                        }
                }
        }

        fun feed(costTokens: Int, hungerGain: Int, label: String) {
            var hunger = prefs.getInt("hunger", 80)
            var tokens = prefs.getInt("tokens", 0)

            if (tokens < costTokens) {
                Toast.makeText(
                    this,
                    "Not enough tokens for $label!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            tokens -= costTokens
            hunger += hungerGain
            if (hunger > 100) hunger = 100

            prefs.edit()
                .putInt("tokens", tokens)
                .putInt("hunger", hunger)
                .apply()

            updateUI()
            showFoodPopup("+$hungerGain hunger (−$costTokens tokens)")
        }

        // Button listeners
        btnSnack.setOnClickListener {
            feed(costTokens = 5, hungerGain = 10, label = "Snack")
        }
        btnMeal.setOnClickListener {
            feed(costTokens = 10, hungerGain = 25, label = "Meal")
        }
        btnFeast.setOnClickListener {
            feed(costTokens = 20, hungerGain = 50, label = "Feast")
        }

        // Initial UI
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        // Refresh UI in case hunger/tokens changed from Home or games
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val hungerBar = findViewById<ProgressBar>(R.id.foodHungerBar)
        val tvMood = findViewById<TextView>(R.id.tvMoodStatusFood)
        val tvTokens = findViewById<TextView>(R.id.tvTokenCountFood)

        val hunger = prefs.getInt("hunger", 80)
        val tokens = prefs.getInt("tokens", 0)

        hungerBar.progress = hunger
        tvTokens.text = "Tokens: $tokens"

        tvMood.text = when {
            hunger >= 80 -> "😄 Happy"
            hunger >= 40 -> "🙂 Okay"
            else -> "😢 Hungry"
        }
    }
}
