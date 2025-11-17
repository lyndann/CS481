package com.example.cs481

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PlayMenuActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_menu)

        val basketBtn = findViewById<Button>(R.id.btnBasketGame)
        val slotBtn = findViewById<Button>(R.id.btnSlotGame)

        basketBtn.setOnClickListener {
            // TODO: replace with your Basket Game Activity
            startActivity(Intent(this, BasketGameActivity::class.java))
        }

        slotBtn.setOnClickListener {
            // TODO: replace with your Slot Game Activity
            startActivity(Intent(this, SlotGameActivity::class.java))
        }
    }
}
