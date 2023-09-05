package com.example.danijelsojat.stepcounter.timber

import android.app.Application
import timber.log.Timber

class TimberPlanter : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}