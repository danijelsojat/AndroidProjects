package com.example.danijelsojat.stepcounter.model

import android.annotation.SuppressLint
import android.content.Context
import com.example.danijelsojat.stepcounter.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object GoogleSignInSingleton {

    private lateinit var gso: GoogleSignInOptions
    @SuppressLint("StaticFieldLeak")
    private lateinit var gsc: GoogleSignInClient
    private lateinit var account: GoogleSignInAccount
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fitnessOptions: FitnessOptions

    fun setupGoogleSignIn(context: Context) {
        gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(context, gso)
        createFitnessOptions()
        account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
    }

    fun setupFirebaseUser(): FirebaseAuth {
        firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth
    }

    fun createFitnessOptions(): FitnessOptions {
        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()
        return fitnessOptions
    }

    fun fetchGoogleSignedInClient(): GoogleSignInClient {
        return gsc
    }

    fun fetchFirebaseUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}