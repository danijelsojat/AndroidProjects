package com.example.danijelsojat.stepcounter.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.danijelsojat.stepcounter.DAILY_GOAL_FROM_PREFS
import com.example.danijelsojat.stepcounter.DAILY_GOAL_PREFS
import com.example.danijelsojat.stepcounter.R
import com.example.danijelsojat.stepcounter.StartingScreenActivity
import com.example.danijelsojat.stepcounter.databinding.FragmentProfileBinding
import com.example.danijelsojat.stepcounter.model.GoogleSignInSingleton
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var signedInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private var signedInFirebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGoogleSignIn()
        checkFirebaseUser()
        buttonLogic()
    }

    private fun buttonLogic() {
        binding.bSignOut.setOnClickListener {
            requireContext().getSharedPreferences(DAILY_GOAL_PREFS, AppCompatActivity.MODE_PRIVATE)
                .edit().putString(DAILY_GOAL_FROM_PREFS, "").apply()
            signOut()
        }
        binding.bEdit.setOnClickListener {
            editDailyGoal()
        }
    }

    private fun editDailyGoal() {
        val sharedPreferences = requireContext()
            .getSharedPreferences(DAILY_GOAL_PREFS, AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val view: View = layoutInflater.inflate(R.layout.daily_goal_dialog, null)
        val goalEntered = view.findViewById<EditText>(R.id.etDailyGoal)
        val saveBtn = view.findViewById<Button>(R.id.bSaveInput)

        val dailyDialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Daily Step Goal")
            .setMessage("Edit your daily steps goal")
            .setView(view)
            .create()
        saveBtn.setOnClickListener {
            if (goalEntered.text.isEmpty()) {
                goalEntered.error = "Daily goal not entered"
            } else {
                editor.putString(DAILY_GOAL_FROM_PREFS, goalEntered.text.toString())
                editor.apply()
                binding.tvDailyGoal.text = goalEntered.text.toString()
                dailyDialog.dismiss()
            }
        }
        dailyDialog.show()
    }

    private fun signOut() {
        signedInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseAuth.signOut()
            }
            requireActivity().finishAffinity()
        }
        startActivity(
            Intent(
                requireContext(),
                StartingScreenActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    private fun setupGoogleSignIn() {
        GoogleSignInSingleton.setupGoogleSignIn(requireContext())
        firebaseAuth = GoogleSignInSingleton.setupFirebaseUser()
        signedInClient = GoogleSignInSingleton.fetchGoogleSignedInClient()
        signedInFirebaseUser = GoogleSignInSingleton.fetchFirebaseUser()
    }

    private fun checkFirebaseUser() {
        Glide.with(this).load(signedInFirebaseUser?.photoUrl).fitCenter().circleCrop()
            .into(binding.ivProfileImage)
        binding.tvNameInfo.text = signedInFirebaseUser?.displayName
        binding.tvEmailInfo.text = signedInFirebaseUser?.email
        val dailyGoal = requireContext().getSharedPreferences(
            DAILY_GOAL_PREFS,
            AppCompatActivity.MODE_PRIVATE
        ).getString(DAILY_GOAL_FROM_PREFS, "")
        binding.tvDailyGoal.text = dailyGoal
    }
}