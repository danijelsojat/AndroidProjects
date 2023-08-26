package com.example.danijelsojat.stepcounter.ui

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.danijelsojat.stepcounter.DAILY_GOAL_FROM_PREFS
import com.example.danijelsojat.stepcounter.DAILY_GOAL_PREFS
import com.example.danijelsojat.stepcounter.NEW_SENSOR_PREFS
import com.example.danijelsojat.stepcounter.SENSOR_PREFS
import com.example.danijelsojat.stepcounter.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var dailyGoal: String? = null
    private var totalSteps: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = requireContext().getSharedPreferences(SENSOR_PREFS, MODE_PRIVATE)
        totalSteps = sharedPreferences.getInt(NEW_SENSOR_PREFS, 0)
        updateUI()
    }

    private fun getDailyGoal() {
        dailyGoal = requireContext().getSharedPreferences(
            DAILY_GOAL_PREFS,
            AppCompatActivity.MODE_PRIVATE
        ).getString(DAILY_GOAL_FROM_PREFS, "")
    }

    private fun updateUI() {
        getDailyGoal()
        binding.tvStepsMade.text = "$totalSteps"
        binding.circularProgressBar.apply {
            setProgressWithAnimation(totalSteps.toFloat())
        }
        binding.circularProgressBar.progressMax = try {
            dailyGoal?.toFloat() ?: 0f
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0f
        }
    }

    fun updateSteps(newCount: Int){
        totalSteps = newCount
        updateUI()
    }
}