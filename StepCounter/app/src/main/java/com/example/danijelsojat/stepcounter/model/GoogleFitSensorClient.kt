package com.example.danijelsojat.stepcounter.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.example.danijelsojat.stepcounter.NEW_SENSOR_PREFS
import com.example.danijelsojat.stepcounter.SENSOR_PREFS
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import java.util.concurrent.TimeUnit

interface IStepCounterRepositorySensorEvents {
    fun onNewDataFetched(newCount: Int)
}

private const val TAG = "GoogleFitSensorClient"

class GoogleFitSensorClient {

    var stepCounterRepositorySensorEvents: IStepCounterRepositorySensorEvents? = null
    var newCount = 0

    fun fetchNewData(context: Context, account: GoogleSignInAccount) {
        Fitness.getRecordingClient(context, account)
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.d(TAG, "RecordingClient: recording client successfully subscribed")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "RecordingClient: problem subscribing recording client", e)
            }

        Fitness.getRecordingClient(context, account)
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                for (sc in subscriptions) {
                    val dt = sc.dataType
                    if (dt != null) {
                        Log.d(TAG, "RecordingClient: Active subscription for data type: ${dt.name}")
                    }
                }
            }

        val listener = OnDataPointListener { dataPoint ->
            for (field in dataPoint.dataType.fields) {
                val value = dataPoint.getValue(field)
                newCount = value.asInt()
            }
            onNewDataFetched(context)
        }

        Fitness.getSensorsClient(context, account)
            .add(
                SensorRequest.Builder()
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                    .setSamplingRate(10, TimeUnit.SECONDS)
                    .build(), listener
            )
            .addOnSuccessListener {
                Log.d(TAG, "SensorsClient: listener registered")
            }
            .addOnFailureListener {
                Log.d(TAG, "fetchDataFromGoogleFit: listener not registered")
            }

        Fitness.getSensorsClient(context, account)
            .findDataSources(
                DataSourcesRequest.Builder()
                    .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                    .setDataSourceTypes(DataSource.TYPE_RAW)
                    .build()
            )
            .addOnSuccessListener { dataSources ->
                dataSources.forEach {
                    Log.d(TAG, "SensorsClient: DataSource found: ${it.streamIdentifier}")
                    Log.d(TAG, "SensorsClient: DataSource type: ${it.dataType.name}")
                    if (it.dataType == DataType.TYPE_STEP_COUNT_DELTA) {
                        Log.d(TAG, "SensorsClient: Data source for STEP COUNT DELTA found!")
                    }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "SensorsClient: Data source for STEP COUNT DELTA NOT found!")
            }
    }

    private fun onNewDataFetched(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SENSOR_PREFS, MODE_PRIVATE)
        val previousCount = sharedPreferences.getInt(NEW_SENSOR_PREFS, 0)
        val totalCount = previousCount + newCount
        
        val editor = sharedPreferences.edit()
        editor.putInt(NEW_SENSOR_PREFS, totalCount)
        editor.apply()
        stepCounterRepositorySensorEvents?.onNewDataFetched(totalCount)
    }
}