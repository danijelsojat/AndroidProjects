package com.example.danijelsojat.stepcounter.model

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface IOnGoogleFitDataFetched {
    fun onNewDataFetched(newCount: Int)
    fun onTodayDataFetched(todayCount: Int)
    fun onSevenDaysDataFetched(lastSevenDaysCount: Int)
    fun onThirtyDaysDataFetched(lastThirtyDaysCount: Int)
}

class StepCounterPresenter(val onGoogleFitDataFetched: IOnGoogleFitDataFetched): IStepCounterPresenterEvents {

    val stepCounterRepository: StepCounterRepository = StepCounterRepository(this)

    init {
        stepCounterRepository.stepCounterPresenterEvents = this
    }

    fun fetchNewData(context: Context, account: GoogleSignInAccount) {
        stepCounterRepository.fetchNewData(context, account)
    }

    fun fetchData(context: Context, account: GoogleSignInAccount) {
        stepCounterRepository.fetchData(context, account)
    }

    override fun onNewDataFetched(newCount: Int) {
        onGoogleFitDataFetched.onNewDataFetched(newCount)
    }

    override fun onTodayDataFetched(todayCount: Int) {
        onGoogleFitDataFetched.onTodayDataFetched(todayCount)
    }

    override fun onSevenDaysDataFetched(lastSevenDaysCount: Int) {
        onGoogleFitDataFetched.onSevenDaysDataFetched(lastSevenDaysCount)
    }

    override fun onThirtyDaysDataFetched(lastThirtyDaysCount: Int) {
        onGoogleFitDataFetched.onThirtyDaysDataFetched(lastThirtyDaysCount)
    }
}