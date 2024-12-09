package com.example.scoreapp

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var scoreTextView: TextView
    private lateinit var scoreButton: Button
    private lateinit var stealButton: Button
    private lateinit var resetButton: Button
    private var mediaPlayer: MediaPlayer? = null

    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        scoreTextView = findViewById(R.id.tv_score)
        scoreButton = findViewById(R.id.score)
        stealButton = findViewById(R.id.steal)
        resetButton = findViewById(R.id.reset)

        // Restore score if activity is recreated
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("SCORE", 0)
            updateScoreDisplay()
        }

        // Ensure UI Stabilization
        stabilizeUI()

        // Button Listeners
        setupListeners()
    }

    private fun stabilizeUI() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                ensureButtonFocus()
                Log.d("MainActivity", "UI stabilized. Score button is ready.")
            }
        })
    }

    private fun ensureButtonFocus() {
        scoreButton.isClickable = true
        scoreButton.requestFocus()
        Log.d("MainActivity", "Score button is clickable and focused.")
    }

    private fun setupListeners() {
        scoreButton.setOnClickListener { incrementScore() }
        stealButton.setOnClickListener { decrementScore() }
        resetButton.setOnClickListener { resetScore() }
    }

    private fun incrementScore() {
        if (score < 15) {
            score++
            updateScoreDisplay()
            if (score == 15) playWinSound()
        }
    }

    private fun decrementScore() {
        if (score > 0) {
            score--
            updateScoreDisplay()
        }
    }

    private fun resetScore() {
        score = 0
        updateScoreDisplay()
    }

    private fun updateScoreDisplay() {
        scoreTextView.text = score.toString()
        Log.d("MainActivity", "Score updated: $score")
    }

    private fun playWinSound() {
        mediaPlayer?.release() // Release any previous MediaPlayer instance
        mediaPlayer = MediaPlayer.create(this, R.raw.win_sound)
        mediaPlayer?.setOnCompletionListener {
            it.release() // Release resources after playback
        }
        mediaPlayer?.start()
        Log.d("MainActivity", "Win sound played. Score is now 15.")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current score
        outState.putInt("SCORE", score)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("MainActivity", "Configuration changed: ${newConfig.orientation}")
        stabilizeUI() // Re-stabilize the UI after rotation
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
