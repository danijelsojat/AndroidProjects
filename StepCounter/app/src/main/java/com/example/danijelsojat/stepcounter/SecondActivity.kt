package com.example.danijelsojat.stepcounter

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.danijelsojat.stepcounter.databinding.ActivitySecondBinding
import com.example.danijelsojat.stepcounter.model.GoogleSignInSingleton
import com.example.danijelsojat.stepcounter.model.IOnGoogleFitDataFetched
import com.example.danijelsojat.stepcounter.model.StepCounterPresenter
import com.example.danijelsojat.stepcounter.ui.HistoryInfoFragment
import com.example.danijelsojat.stepcounter.ui.HomeFragment
import com.example.danijelsojat.stepcounter.ui.ProfileFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity(), IOnGoogleFitDataFetched {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var account: GoogleSignInAccount
    private lateinit var fitnessOptions: FitnessOptions
    private lateinit var stepCounterPresenter: StepCounterPresenter
    private lateinit var homeFragment: HomeFragment
    private lateinit var historyInfoFragment: HistoryInfoFragment
    private lateinit var profileFragment: ProfileFragment
    private var dailyGoal: String? = null
    private var newSteps: Int = 0
    private var todaySteps: Int = 0
    private var sevenDaySteps: Int = 0
    private var thirtyDaySteps: Int = 0
    private var doubleBackToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPermissionCheck()
        initToolbar()
        initBottomBar()
        initFragments()
        setCurrentFragment(homeFragment)
    }

    private fun userPermissionCheck() {

        // provjera dopuštenja
        if (!hasActivityPermission() && !hasCoarseLocationPermission() && !hasFineLocationPermission()) {
            requestPermissions()
        } else {
            initStepCounterPresenter()
        }
    }

    private fun hasActivityPermission(): Boolean {

        // provjera dopuštenja za aktivnost
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCoarseLocationPermission(): Boolean {

        // provjera dopuštenja za lokaciju
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasFineLocationPermission(): Boolean {

        // provjera dopuštenja za lokaciju
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {

        // traženje dopuštenja
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACTIVITY_RECOGNITION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            MY_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun activityPermissionDialog() {

        // dialog za obavijest kako su dopuštenja za lokaciju i aktivnost bitna
        val permissionDialog: AlertDialog = AlertDialog.Builder(this@SecondActivity)
            .setTitle("Permissions")
            .setMessage("Application requires this permissions to work properly, you can also change them manually in app info in your settings")
            .setPositiveButton("OK") { _, _ ->
                requestPermissions()
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .create()
        permissionDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        // obrada rezultata nakon dopuštenja/odbijanja dopuštenja
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                initStepCounterPresenter()
            } else {
                activityPermissionDialog()
            }
        }
    }

    private fun initStepCounterPresenter() {
        // inicijalizacija presentera te dohvaćanje podataka sa Fita ako su dopuštenja dana
        stepCounterPresenter = StepCounterPresenter(this)
        fitnessOptions = GoogleSignInSingleton.createFitnessOptions()
        account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                GOOGLE_FIT_PERMISSION_REQUEST_CODE,
                account,
                fitnessOptions
            )
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                stepCounterPresenter.fetchNewData(this@SecondActivity, account)
                stepCounterPresenter.fetchData(this@SecondActivity, account)
            }
        }
    }

    override fun onNewDataFetched(newCount: Int) {
        // obrada novih koraka koje senzor čita i slanje tih podataka u fragment
        newSteps = newCount
        homeFragment.updateSteps(newSteps)
    }

    override fun onTodayDataFetched(todayCount: Int) {
        // obrada podataka za današnji dan, nakon dohvaćanja podataka pokreće se dialog za upis dnevnog cilja koraka
        todaySteps = todayCount
        initDailyGoal()
    }

    override fun onSevenDaysDataFetched(lastSevenDaysCount: Int) {
        // obrada podataka za zadnjih 7 dana
        sevenDaySteps = lastSevenDaysCount
    }

    override fun onThirtyDaysDataFetched(lastThirtyDaysCount: Int) {
        // obrada podataka za zadnjih 30 dana, nakon što se dohvate ti podaci šaljem ih kroz bundle u fragment
        thirtyDaySteps = lastThirtyDaysCount
        val bundle = Bundle().apply {
            putInt(SEVEN_DAYS_STEPS, sevenDaySteps)
            putInt(THIRTY_DAYS_STEPS, thirtyDaySteps)
        }
        historyInfoFragment.arguments = bundle
    }

    private fun initDailyGoal() {
        // dialog za upis dnevnog cilja koraka, taj podatak spremam u shared prefs
        val sharedPreferences = getSharedPreferences(DAILY_GOAL_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        dailyGoal = sharedPreferences.getString(DAILY_GOAL_FROM_PREFS, "")

        // custom made izgled dialoga
        val view: View = layoutInflater.inflate(R.layout.daily_goal_dialog, null)
        val goalEntered = view.findViewById<EditText>(R.id.etDailyGoal)
        val saveBtn = view.findViewById<Button>(R.id.bSaveInput)

        if (dailyGoal.isNullOrEmpty()) {
            val dailyDialog: AlertDialog = AlertDialog.Builder(this@SecondActivity)
                .setTitle("Daily Step Goal")
                .setMessage("For best app experience enter your daily steps goal")
                .setView(view)
                .create()
            saveBtn.setOnClickListener {
                if (goalEntered.text.isNullOrEmpty()) {
                    goalEntered.error = "Daily goal not entered"
                } else {
                    editor.putString(DAILY_GOAL_FROM_PREFS, goalEntered.text.toString())
                    editor.apply()
                    dailyGoal = goalEntered.text.toString()
                    dailyDialog.dismiss()
                    homeFragment.updateSteps(todaySteps)
                }
            }
            dailyDialog.show()
        } else {
            homeFragment.updateSteps(todaySteps)
        }
    }

    private fun initFragments() {
        // kreiranje svih fragmenata koji se koriste u ovom activityu
        homeFragment = HomeFragment()
        historyInfoFragment = HistoryInfoFragment()
        profileFragment = ProfileFragment()
    }

    private fun initToolbar() {
        // inicijalizacija toolbara
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setIcon(R.drawable.app_white_icon)
        supportActionBar?.setTitle(R.string.app_name)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun initBottomBar() {
        // inicijalizacija bottombara
        binding.bottomNavView.selectedItemId = R.id.home
        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.history -> setCurrentFragment(historyInfoFragment)
                R.id.profile -> setCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        // funkcija za postavljanje fragmenta u frame layout
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragmentView, fragment)
            commit()
        }
    }

    override fun onBackPressed() {
        // logika za gašenje aplikacije, potreban dvostruki pritisak na back button
        if (doubleBackToExit) {
            super.onBackPressed()
        } else {
            Toast.makeText(this@SecondActivity, "Press again to exit", Toast.LENGTH_SHORT).show()
        }
        this.doubleBackToExit = true
        Handler(Looper.getMainLooper()).postDelayed(Runnable {doubleBackToExit = false}, 2000)
    }

    override fun onDestroy() {
        // budući da korake koje senzor učitava spremam u shared preferences prilikom gašenja aplikacije čistim spremljene vrijednosti
        super.onDestroy()
        getSharedPreferences(SENSOR_PREFS, MODE_PRIVATE).edit().putInt(NEW_SENSOR_PREFS, 0)
            .apply()
    }
}