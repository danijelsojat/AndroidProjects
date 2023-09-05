package com.example.danijelsojat.stepcounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.danijelsojat.stepcounter.databinding.ActivityStartingScreenBinding

class StartingScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartingScreenBinding

    // početni zaslon, koristi se kao loading screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivAppIcon.alpha = 0f
        binding.tvAppName.alpha = 0f

        binding.ivAppIcon.animate().setDuration(1500).alpha(1f)
        binding.tvAppName.animate().setDuration(1500).alpha(1f).withEndAction {
            startActivity(Intent(this@StartingScreenActivity, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}