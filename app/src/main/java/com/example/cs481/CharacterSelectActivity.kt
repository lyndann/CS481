package com.example.cs481

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class CharacterSelectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_select)

        val img1 = findViewById<ImageView>(R.id.charImage1)
        val img2 = findViewById<ImageView>(R.id.charImage2)
        val select1 = findViewById<Button>(R.id.selectChar1)
        val select2 = findViewById<Button>(R.id.selectChar2)

        // Load GIFs
        Glide.with(this).asGif().load(R.drawable.bugcat).into(img1)
        Glide.with(this).asGif().load(R.drawable.purplecat).into(img2)

        select1.setOnClickListener { saveCharacter("bugcat") }
        select2.setOnClickListener { saveCharacter("purplecat") }
    }


    private fun saveCharacter(selected: String) {
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        prefs.edit().putString("selected_character", selected).apply()
        finish()
    }
}
