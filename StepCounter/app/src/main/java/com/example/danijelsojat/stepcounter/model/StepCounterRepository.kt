package com.example.danijelsojat.stepcounter.model

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface IStepCounterPresenterEvents {

    // interface preko kojeg presenter sluša promjene
    fun onNewDataFetched(newCount: Int)
    fun onTodayDataFetched(todayCount: Int)
    fun onSevenDaysDataFetched(lastSevenDaysCount: Int)
    fun onThirtyDaysDataFetched(lastThirtyDaysCount: Int)
}

class StepCounterRepository(val onStepCounterPresenterEvents: IStepCounterPresenterEvents): IStepCounterRepositoryHistoryEvents, IStepCounterRepositorySensorEvents {

    // inicijalizacija history i sensor client objekta za obradu u repositoryu
    val googleFitHistoryClient: GoogleFitHistoryClient = GoogleFitHistoryClient()
    val googleFitSensorClient: GoogleFitSensorClient = GoogleFitSensorClient()
    var stepCounterPresenterEvents: IStepCounterPresenterEvents? = null

    init {
        googleFitHistoryClient.stepCounterRepositoryHistoryEvents = this
        googleFitSensorClient.stepCounterRepositorySensorEvents = this
    }

    fun fetchData(context: Context, account: GoogleSignInAccount) {
        googleFitHistoryClient.fetchData(context, account)
    }
    override fun onTodayDataFetched(todayCount: Int) {
        onStepCounterPresenterEvents.onTodayDataFetched(todayCount)
    }

    override fun onSevenDaysDataFetched(lastSevenDaysCount: Int) {
        onStepCounterPresenterEvents.onSevenDaysDataFetched(lastSevenDaysCount)
    }

    override fun onThirtyDaysDataFetched(lastThirtyDaysCount: Int) {
        onStepCounterPresenterEvents.onThirtyDaysDataFetched(lastThirtyDaysCount)
    }

    fun fetchNewData(context: Context, account: GoogleSignInAccount) {
        googleFitSensorClient.fetchNewData(context, account)
    }

    override fun onNewDataFetched(newCount: Int) {
        onStepCounterPresenterEvents.onNewDataFetched(newCount)
    }
}