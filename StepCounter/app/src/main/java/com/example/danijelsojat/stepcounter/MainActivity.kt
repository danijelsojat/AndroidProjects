package com.example.danijelsojat.stepcounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.danijelsojat.stepcounter.databinding.ActivityMainBinding
import com.example.danijelsojat.stepcounter.model.GoogleSignInSingleton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var signedInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private var signedInFirebaseUser: FirebaseUser? = null

    // ovaj activity koristim za Google login formu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignIn()
        checkFirebaseUser()
        buttonLogic()
    }

    private fun buttonLogic() {
        // logika buttona, pokreće Google login formu
        binding.bSignIn.setOnClickListener {
            val signInIntent = signedInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CLIENT_REQUEST_CODE)
        }
    }

    private fun checkFirebaseUser() {
        // provjera logiranog usera, ako postoji pokreće se second activity
        if (signedInFirebaseUser != null) {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }
    }

    private fun setupGoogleSignIn() {
        // inicijalizacija Google acc singletona
        GoogleSignInSingleton.setupGoogleSignIn(this)
        firebaseAuth = GoogleSignInSingleton.setupFirebaseUser()
        signedInClient = GoogleSignInSingleton.fetchGoogleSignedInClient()
        signedInFirebaseUser = GoogleSignInSingleton.fetchFirebaseUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // obrada rezultata nakon Google login forme

        if (requestCode == GOOGLE_SIGN_IN_CLIENT_REQUEST_CODE) {
            val signInAccountTask: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(signInAccountTask)
        }
    }

    private fun handleSignInResult(signInAccountTask: Task<GoogleSignInAccount>) {
        if (signInAccountTask.isSuccessful) {
            try {
                val googleSignInAccount: GoogleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                handleCredentials(googleSignInAccount)
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleCredentials(googleSignInAccount: GoogleSignInAccount) {
        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        firebaseAuth.signInWithCredential(authCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@MainActivity, SecondActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } else {
                    Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}