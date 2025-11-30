package com.example.cs481

import android.os.Bundle

class SlotGameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slot_game)

        // Mark that the slot game has been played today
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("slot_played_today", true).apply()
    }
}
