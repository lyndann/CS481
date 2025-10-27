package com.example.cs481

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnTask1: Button = findViewById(R.id.btnTask1)
        val btnTask2: Button = findViewById(R.id.btnTask2)
        val btnTask3: Button = findViewById(R.id.btnTask3)
        val btnHome: Button = findViewById(R.id.btnHome)
        val btnPlay: Button = findViewById(R.id.btnPlay)
        val btnFood: Button = findViewById(R.id.btnFood)

        btnTask1.setOnClickListener{
            Toast.makeText(this, "Task 1", Toast.LENGTH_SHORT).show()
        }
        btnTask2.setOnClickListener{
            Toast.makeText(this, "Task 2", Toast.LENGTH_SHORT).show()
        }
        btnTask3.setOnClickListener{
            Toast.makeText(this, "Task 3", Toast.LENGTH_SHORT).show()
        }
        btnHome.setOnClickListener{
            Toast.makeText(this, "Add Home", Toast.LENGTH_SHORT).show()
        }
        btnPlay.setOnClickListener{
            Toast.makeText(this, "Add Play", Toast.LENGTH_SHORT).show()
        }
        btnFood.setOnClickListener{
            Toast.makeText(this, "Add Food", Toast.LENGTH_SHORT).show()
        }


    }
}