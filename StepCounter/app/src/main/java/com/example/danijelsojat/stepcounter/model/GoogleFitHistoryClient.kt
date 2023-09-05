package com.example.danijelsojat.stepcounter.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import com.example.danijelsojat.stepcounter.NEW_SENSOR_PREFS
import com.example.danijelsojat.stepcounter.SENSOR_PREFS
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

interface IStepCounterRepositoryHistoryEvents {

    // interface preko kojeg repository sluša promjene
    fun onTodayDataFetched(todayCount: Int)
    fun onSevenDaysDataFetched(lastSevenDaysCount: Int)
    fun onThirtyDaysDataFetched(lastThirtyDaysCount: Int)
}

class GoogleFitHistoryClient {

    // klasa za dohvaćanje podataka o povijesti koraka

    var stepCounterRepositoryHistoryEvents: IStepCounterRepositoryHistoryEvents? = null
    var todayCount: Int = 0
    var lastSevenDaysCount: Int = 0
    var lastThirtyDaysCount: Int = 0
    var googleFitDataList: ArrayList<Int> = arrayListOf()

    fun fetchData(context: Context, account: GoogleSignInAccount) {
        // funkcija koja na start time i end time odrađuje query po Google Fit podacima za zadnjih 30 dana
        val end = LocalDateTime.now()
        val start = end.minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(context, account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                for (bucket in response.buckets) {
                    for (dataSet in bucket.dataSets) {
                        saveInfo(dataSet)
                    }
                }
                onTodayDataFetched(context)
                onSevenDaysDataFetched()
                onThirtyDaysDataFetched()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to access Google Fit", Toast.LENGTH_SHORT).show()
                Timber.d("HistoryClient: failed to get data from google fit", e)
            }
    }

    private fun saveInfo(dataSet: DataSet) {
        // funkcija koja obrađuje dobivene data setove i iz njih dohvaća samo korake
        for (dp in dataSet.dataPoints) {
            val steps = dp.getValue(Field.FIELD_STEPS).asInt()
            googleFitDataList += steps
        }
    }

    private fun onTodayDataFetched(context: Context) {
        // funkcija za obradu dnevnih dobivenih koraka
        // ako postoji zapis za danas to će u listi biti 31. item i njega spremam u shared prefs
        // te ga šaljem kroz onTodayData Fetched a ako ne postoji postavljam ga na 0
        todayCount = if (googleFitDataList.size == 31) {
            googleFitDataList[30]
        } else {
            0
        }
        val sharedPreferences = context.getSharedPreferences(SENSOR_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(NEW_SENSOR_PREFS, todayCount)
        editor.apply()

        stepCounterRepositoryHistoryEvents?.onTodayDataFetched(todayCount)
    }

    private fun onSevenDaysDataFetched() {
        // obrada podataka za zadnjih 7 dana, prolazim kroz listu i uzimam samo tih prijašnjih 7 brojeva te ih zbrajam
        // zbroj šaljem kroz onSevenDaysDataFetched
        for (i in 23..29) {
            lastSevenDaysCount += googleFitDataList[i]
        }
        stepCounterRepositoryHistoryEvents?.onSevenDaysDataFetched(lastSevenDaysCount)
    }

    private fun onThirtyDaysDataFetched() {
        // obrada podataka za zadnjih 30 dana, prolazim kroz listu i uzimam samo tih prijašnjih 30 brojeva te ih zbrajam
        // zbroj šaljem kroz onSevenDaysDataFetched
        for (i in 0..29) {
            lastThirtyDaysCount += googleFitDataList[i]
        }
        stepCounterRepositoryHistoryEvents?.onThirtyDaysDataFetched(lastThirtyDaysCount)
    }
}