package com.example.cs481

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge

class PlayMenuActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_play_menu)

        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)

        val tvTokenCountPlay = findViewById<TextView>(R.id.tvTokenCountPlay)
        val btnBasketGame = findViewById<Button>(R.id.btnBasketGame)
        val btnSlotGame = findViewById<Button>(R.id.btnSlotGame)

        // Show current tokens
        val tokens = prefs.getInt("tokens", 0)
        tvTokenCountPlay.text = "Tokens: $tokens"

        // Go to Basket Game
        btnBasketGame.setOnClickListener {
            startActivity(Intent(this, BasketGameActivity::class.java))
        }

        // Go to Slot Game
        btnSlotGame.setOnClickListener {
            startActivity(Intent(this, SlotGameActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // In case tokens changed after a game
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val tvTokenCountPlay = findViewById<TextView>(R.id.tvTokenCountPlay)
        val tokens = prefs.getInt("tokens", 0)
        tvTokenCountPlay.text = "Tokens: $tokens"
    }
}
