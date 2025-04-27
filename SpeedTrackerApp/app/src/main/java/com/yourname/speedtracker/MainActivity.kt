package com.yourname.speedtracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yourname.speedtracker.databinding.ActivityMainBinding
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isMph = true
    private var speed = 0.0
    private var distance = 0.0
    private var topSpeedOverall = 0.0
    private var topSpeed1320 = 0.0
    private var runStarted = false
    private var distanceAtRunStart = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.toggleButton.setOnClickListener {
            isMph = !isMph
            updateUnit()
        }

        binding.startRunButton.setOnClickListener {
            startRun()
        }

        binding.resetButton.setOnClickListener {
            resetData()
        }
    }

    private fun updateUnit() {
        binding.unitText.text = if (isMph) getString(R.string.mph) else getString(R.string.kph)
        binding.toggleButton.text = if (isMph) getString(R.string.switch_to_kph) else getString(R.string.switch_to_mph)
    }

    private fun startRun() {
        runStarted = true
        distanceAtRunStart = distance
        binding.topSpeed1320Text.text = ""
        Toast.makeText(this, getString(R.string.quarter_mile_started), Toast.LENGTH_SHORT).show()
    }

    private fun resetData() {
        speed = 0.0
        distance = 0.0
        topSpeedOverall = 0.0
        topSpeed1320 = 0.0
        runStarted = false
        updateUI()
    }

    private fun updateUI() {
        binding.speedValue.text = String.format(getString(R.string.speed_label), speed)
        binding.odometerText.text = String.format(getString(R.string.distance_label), distance, if (isMph) "mi" else "km")
        binding.topSpeedOverallText.text = String.format(getString(R.string.top_speed_overall), topSpeedOverall)
    }

    // Example: Call this when GPS updates
    private fun onLocationUpdate(newSpeedMps: Double) {
        speed = if (isMph) newSpeedMps * 2.23694 else newSpeedMps * 3.6
        distance += newSpeedMps * (1.0 / 60.0)
        topSpeedOverall = max(topSpeedOverall, speed)

        if (runStarted) {
            val traveled = distance - distanceAtRunStart
            if (traveled >= 0.25) {
                topSpeed1320 = max(topSpeed1320, speed)
                binding.topSpeed1320Text.text = String.format(getString(R.string.top_speed_1320), topSpeed1320)
                runStarted = false
                Toast.makeText(this, getString(R.string.quarter_mile_finished), Toast.LENGTH_SHORT).show()
            }
        }

        updateUI()
    }
}