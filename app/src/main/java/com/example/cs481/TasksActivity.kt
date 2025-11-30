package com.example.cs481

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TasksActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // load the tasks page
        setContentView(R.layout.tasks_screen)

        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)

        // Daily reset system
        checkDailyReset(prefs)

        // UI references
        val tvToken = findViewById<TextView>(R.id.tvTokenCountTasks)
        val tvLevel = findViewById<TextView>(R.id.tvLevelTasks)
        val tvTracker = findViewById<TextView>(R.id.tvTasksTrackerTasks)

        val etTask1 = findViewById<EditText>(R.id.etTask1)
        val etTask2 = findViewById<EditText>(R.id.etTask2)
        val etTask3 = findViewById<EditText>(R.id.etTask3)

        val cbTask1 = findViewById<CheckBox>(R.id.cbTask1)
        val cbTask2 = findViewById<CheckBox>(R.id.cbTask2)
        val cbTask3 = findViewById<CheckBox>(R.id.cbTask3)

        val btnReset = findViewById<Button>(R.id.btnResetTasks)

        // Load saved text
        etTask1.setText(prefs.getString("task1_text", ""))
        etTask2.setText(prefs.getString("task2_text", ""))
        etTask3.setText(prefs.getString("task3_text", ""))

        // Load currency / level
        var tokens = prefs.getInt("tokens", 0)
        var xp = prefs.getInt("xp", 0)
        var level = prefs.getInt("level", 1)
        tvToken.text = "Tokens: $tokens"
        tvLevel.text = "Level $level (XP: $xp)"

        // Load completion
        val done1 = prefs.getBoolean("task1_done", false)
        val done2 = prefs.getBoolean("task2_done", false)
        val done3 = prefs.getBoolean("task3_done", false)
        val completedCount = listOf(done1, done2, done3).count { it }
        tvTracker.text = "Tasks completed today: $completedCount/3"

        cbTask1.isChecked = done1
        cbTask2.isChecked = done2
        cbTask3.isChecked = done3

        if (done1) cbTask1.isEnabled = false
        if (done2) cbTask2.isEnabled = false
        if (done3) cbTask3.isEnabled = false

        // Checkbox listeners → reward system
        cbTask1.setOnCheckedChangeListener { _, checked ->
            handleTaskCompletion(
                prefs, 1, checked, cbTask1, tvToken, tvTracker, tvLevel
            )
        }
        cbTask2.setOnCheckedChangeListener { _, checked ->
            handleTaskCompletion(
                prefs, 2, checked, cbTask2, tvToken, tvTracker, tvLevel
            )
        }
        cbTask3.setOnCheckedChangeListener { _, checked ->
            handleTaskCompletion(
                prefs, 3, checked, cbTask3, tvToken, tvTracker, tvLevel
            )
        }

        // Slot game auto-complete
        autoCompleteSlotTaskIfNeeded(prefs, cbTask2, tvToken, tvTracker, tvLevel)

        // RESET BUTTON
        btnReset.setOnClickListener {
            resetTasksForToday(
                prefs, cbTask1, cbTask2, cbTask3, tvTracker
            )
        }
    }

    override fun onPause() {
        super.onPause()
        // Save task text
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        prefs.edit()
            .putString("task1_text", findViewById<EditText>(R.id.etTask1).text.toString())
            .putString("task2_text", findViewById<EditText>(R.id.etTask2).text.toString())
            .putString("task3_text", findViewById<EditText>(R.id.etTask3).text.toString())
            .apply()
    }

    // Handle task completion logic
    private fun handleTaskCompletion(
        prefs: android.content.SharedPreferences,
        taskNumber: Int,
        checked: Boolean,
        cb: CheckBox,
        tvTokens: TextView,
        tvTracker: TextView,
        tvLevel: TextView
    ) {
        if (!checked) return

        val doneKey = "task${taskNumber}_done"

        // Already completed?
        if (prefs.getBoolean(doneKey, false)) {
            cb.isChecked = true
            cb.isEnabled = false
            return
        }

        // Reward by difficulty
        val reward = when (taskNumber) {
            1 -> 5
            2 -> 10
            3 -> 15
            else -> 5
        }

        // Update tokens/xp/level
        var tokens = prefs.getInt("tokens", 0) + reward
        var xp = prefs.getInt("xp", 0) + reward
        var level = prefs.getInt("level", 1)

        var leveledUp = false
        while (xp >= 50) {
            xp -= 50
            level++
            leveledUp = true
        }

        prefs.edit()
            .putBoolean(doneKey, true)
            .putInt("tokens", tokens)
            .putInt("xp", xp)
            .putInt("level", level)
            .apply()

        // Update UI
        cb.isEnabled = false
        tvTokens.text = "Tokens: $tokens"
        tvLevel.text = "Level $level (XP: $xp)"

        val done1 = prefs.getBoolean("task1_done", false)
        val done2 = prefs.getBoolean("task2_done", false)
        val done3 = prefs.getBoolean("task3_done", false)
        val completedCount = listOf(done1, done2, done3).count { it }
        tvTracker.text = "Tasks completed today: $completedCount/3"

        // Popup animation
        showTokenPopup("+$reward tokens")
        if (leveledUp) showTokenPopup("Level up! 🎉")

        Toast.makeText(
            this,
            "Task $taskNumber completed! +$reward tokens",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Daily reset logic
    private fun checkDailyReset(prefs: android.content.SharedPreferences) {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
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

    // Auto-complete Task 2 if the Slot Game was played
    private fun autoCompleteSlotTaskIfNeeded(
        prefs: android.content.SharedPreferences,
        cbTask2: CheckBox,
        tvToken: TextView,
        tvTracker: TextView,
        tvLevel: TextView
    ) {
        val slotPlayed = prefs.getBoolean("slot_played_today", false)
        val alreadyDone = prefs.getBoolean("task2_done", false)
        if (!slotPlayed || alreadyDone) return

        val text = prefs.getString("task2_text", "") ?: ""
        if (text.contains("slot", ignoreCase = true)) {
            cbTask2.isChecked = true
            handleTaskCompletion(
                prefs, 2, true, cbTask2, tvToken, tvTracker, tvLevel
            )
            prefs.edit().putBoolean("slot_played_today", false).apply()
        }
    }

    // RESET TODAY'S TASKS
    private fun resetTasksForToday(
        prefs: android.content.SharedPreferences,
        cb1: CheckBox,
        cb2: CheckBox,
        cb3: CheckBox,
        tvTracker: TextView
    ) {
        prefs.edit()
            .putBoolean("task1_done", false)
            .putBoolean("task2_done", false)
            .putBoolean("task3_done", false)
            .putBoolean("slot_played_today", false)
            .apply()

        cb1.isChecked = false
        cb2.isChecked = false
        cb3.isChecked = false

        cb1.isEnabled = true
        cb2.isEnabled = true
        cb3.isEnabled = true

        tvTracker.text = "Tasks completed today: 0/3"

        Toast.makeText(this, "Today's tasks reset!", Toast.LENGTH_SHORT).show()
    }

    // Floating popup animation
    private fun showTokenPopup(text: String) {
        val popup = findViewById<TextView>(R.id.tvTokenPopupTasks)
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
}
