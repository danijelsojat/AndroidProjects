package com.example.vjezba_15_05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class ItemActivity : AppCompatActivity() {

    private lateinit var newTv1: TextView
    private lateinit var newTv2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        newTv1 = findViewById(R.id.newTv1)
        newTv2 = findViewById(R.id.newTv2)

        val extra1 = intent?.getStringExtra(EXTRA1) ?: ""
        val extra2 = intent?.getStringExtra(EXTRA2) ?: ""

        newTv1.text = extra1
        newTv2.text = extra2
    }
}