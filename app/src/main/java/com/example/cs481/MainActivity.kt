package com.example.cs481

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

enum class Symbol(val drawableRes: Int, val weight: Int, val payout: Int) {
    // cherry = rare, high payout
    CHERRY(R.drawable.cherry, weight = 10, payout = 50),

    // orange = medium chance, medium payout
    ORANGE(R.drawable.orange, weight = 20, payout = 20),

    // grapes = common, low payout
    GRAPES(R.drawable.grapes, weight = 50, payout = 5)
}

class MainActivity : AppCompatActivity() {

    private var tokens = 0
    private lateinit var reel11: ImageView
    private lateinit var reel12: ImageView
    private lateinit var reel13: ImageView
    private lateinit var reel21: ImageView
    private lateinit var reel22: ImageView
    private lateinit var reel23: ImageView
    private lateinit var reel31: ImageView
    private lateinit var reel32: ImageView
    private lateinit var reel33: ImageView
    private lateinit var handleImageView: LinearLayout
    private lateinit var addCoinButton: Button
    private lateinit var tokenCount: TextView
    private lateinit var reelsGrid: Array<Array<ImageView>>
    private lateinit var symbolGrid: Array<Array<Symbol>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gambling)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reelsLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        reel11 = findViewById(R.id.reel11)
        reel12 = findViewById(R.id.reel12)
        reel13 = findViewById(R.id.reel13)
        reel21 = findViewById(R.id.reel21)
        reel22 = findViewById(R.id.reel22)
        reel23 = findViewById(R.id.reel23)
        reel31 = findViewById(R.id.reel31)
        reel32 = findViewById(R.id.reel32)
        reel33 = findViewById(R.id.reel33)
        handleImageView = findViewById(R.id.lever)
        addCoinButton = findViewById(R.id.addCoinButton)
        tokenCount = findViewById(R.id.tokenCount)

        reelsGrid = arrayOf(
            arrayOf(reel11, reel12, reel13),
            arrayOf(reel21, reel22, reel23),
            arrayOf(reel31, reel32, reel33)
        )

        //initialize with something; gets overwritten on first spin
        symbolGrid = Array(3) { Array(3) { Symbol.GRAPES } }

        //reels = listOf(reel11, reel12, reel13, reel21, reel22, reel23, reel31, reel32, reel33)

        updateTokenCount()

        addCoinButton.setOnClickListener {
            tokens++
            updateTokenCount()
        }

        handleImageView.doOnLayout {
            handleImageView.pivotX = handleImageView.width / 2f
            handleImageView.pivotY = 172f               // hinge at the top
        }

        handleImageView.setOnClickListener {
            spinReels()
            handleImageView.animate().rotation(28f).setDuration(300)
                .withEndAction { handleImageView.animate().rotation(-172f).setDuration(200)
                    .withEndAction { handleImageView.isClickable = true }
                    .start() }
                .start()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener {
            item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    setContentView(R.layout.activity_main)
                    true
                }
                else -> false
            }
        }
    }

    private fun getRandomSymbol(): Symbol {
        val symbols = Symbol.entries.toTypedArray()
        val totalWeight = symbols.sumOf { it.weight }  // 1 + 3 + 6 = 10

        var r = (1..totalWeight).random()
        for (symbol in symbols) {
            r -= symbol.weight
            if (r <= 0) {
                return symbol
            }
        }
        return symbols.last() // fallback, shouldn't happen
    }

    private fun spinReels() {
        if (tokens > 0) {
            tokens--
            updateTokenCount()

            // Fill the 3x3 grid with random symbols based on weights
            for (row in 0..2) {
                for (col in 0..2) {
                    val symbol = getRandomSymbol()
                    symbolGrid[row][col] = symbol
                    reelsGrid[row][col].setImageResource(symbol.drawableRes)
                }
            }

            checkWin()
        } else {
            Toast.makeText(this, "Not enough tokens!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkWin() {

        // 8 lines: 3 rows, 3 columns, 2 diagonals
        val lines = listOf(
            // Rows
            listOf(0 to 0, 0 to 1, 0 to 2),
            listOf(1 to 0, 1 to 1, 1 to 2),
            listOf(2 to 0, 2 to 1, 2 to 2),
            // Columns
            listOf(0 to 0, 1 to 0, 2 to 0),
            listOf(0 to 1, 1 to 1, 2 to 1),
            listOf(0 to 2, 1 to 2, 2 to 2),
            // Diagonals
            listOf(0 to 0, 1 to 1, 2 to 2),
            listOf(0 to 2, 1 to 1, 2 to 0)
        )

        var totalWin = 0

        for (line in lines) {
            val (r0, c0) = line[0]
            val firstSymbol = symbolGrid[r0][c0]

            val allMatch = line.all { (r, c) ->
                symbolGrid[r][c] == firstSymbol
            }

            if (allMatch) {
                // Add payout for this line (you can hit multiple lines)
                totalWin += firstSymbol.payout
            }
        }

        if (totalWin > 0) {
            tokens += totalWin
            updateTokenCount()
            Toast.makeText(this, "You won $totalWin tokens!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "You lose!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTokenCount() {
        tokenCount.text = tokens.toString()
    }
}