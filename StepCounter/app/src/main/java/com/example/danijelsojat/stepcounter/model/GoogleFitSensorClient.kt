package com.example.danijelsojat.stepcounter.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.danijelsojat.stepcounter.NEW_SENSOR_PREFS
import com.example.danijelsojat.stepcounter.SENSOR_PREFS
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import timber.log.Timber
import java.util.concurrent.TimeUnit

interface IStepCounterRepositorySensorEvents {

    // interface preko kojeg repository sluša promjene
    fun onNewDataFetched(newCount: Int)
}

class GoogleFitSensorClient {

    // klasa za registraciju listenera na sensor promjene te spremanje i obradu tih podataka

    var stepCounterRepositorySensorEvents: IStepCounterRepositorySensorEvents? = null
    var newCount = 0

    fun fetchNewData(context: Context, account: GoogleSignInAccount) {

        // funkcija za dohvaćanje novih koraka registriranih preko senzora uređaja

        Fitness.getRecordingClient(context, account)
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Timber.d("RecordingClient: recording client successfully subscribed")
            }
            .addOnFailureListener { e ->
                Timber.d("RecordingClient: problem subscribing recording client", e)
            }

        Fitness.getRecordingClient(context, account)
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                for (sc in subscriptions) {
                    val dt = sc.dataType
                    if (dt != null) {
                        Timber.d("RecordingClient: Active subscription for data type: ${dt.name}")
                    }
                }
            }

        val listener = OnDataPointListener { dataPoint ->
            // postavljanje listenera na promjene senzora, taj podatak se sprema u varijablu
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
                Timber.d("SensorsClient: listener registered")
            }
            .addOnFailureListener {
                Timber.d("fetchDataFromGoogleFit: listener not registered")
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
                    Timber.d("SensorsClient: DataSource found: ${it.streamIdentifier}")
                    Timber.d("SensorsClient: DataSource type: ${it.dataType.name}")
                    if (it.dataType == DataType.TYPE_STEP_COUNT_DELTA) {
                        Timber.d("SensorsClient: Data source for STEP COUNT DELTA found!")
                    }
                }
            }
            .addOnFailureListener {
                Timber.d("SensorsClient: Data source for STEP COUNT DELTA NOT found!")
            }
    }

    private fun onNewDataFetched(context: Context) {
        //  kreiranje shared prefs za spremanje promjena u senzoru
        // za prvi broj se postavlja dohvaćeni dnevni broj koraka
        // na taj broj nadodajem nove korake iz senzora te ih spremam u isti shared prefs radi korištenja u fragmentu
        // za prikaz dnevnog grafa i šaljem preko onNewDataFetched u activity i home fragment za prikaz i promjenu
        // u progress baru
        val sharedPreferences = context.getSharedPreferences(SENSOR_PREFS, MODE_PRIVATE)
        val previousCount = sharedPreferences.getInt(NEW_SENSOR_PREFS, 0)
        val totalCount = previousCount + newCount
        
        val editor = sharedPreferences.edit()
        editor.putInt(NEW_SENSOR_PREFS, totalCount)
        editor.apply()
        stepCounterRepositorySensorEvents?.onNewDataFetched(totalCount)
    }
}