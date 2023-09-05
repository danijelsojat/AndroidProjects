package com.example.danijelsojat.stepcounter.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.danijelsojat.stepcounter.DAILY_GOAL_FROM_PREFS
import com.example.danijelsojat.stepcounter.DAILY_GOAL_PREFS
import com.example.danijelsojat.stepcounter.R
import com.example.danijelsojat.stepcounter.SEVEN_DAYS_STEPS
import com.example.danijelsojat.stepcounter.databinding.FragmentSevenDayInfoBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class SevenDayInfoFragment : Fragment() {

    // fragment za prikaz povijesti koraka za zadnjih 7 dana

    private lateinit var binding: FragmentSevenDayInfoBinding
    var sevenDaySteps: Int = 0
    var dailyGoal: Float = 0f
    lateinit var barDataSet1: BarDataSet
    lateinit var barDataSet2: BarDataSet
    lateinit var barEntriesList: ArrayList<BarEntry>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // preuzimanje podataka iz bundle i shared prefs za dnevni cilj za usporedbu
        arguments?.let {
            sevenDaySteps = it.getInt(SEVEN_DAYS_STEPS)
        }
        dailyGoal = requireContext().getSharedPreferences(
            DAILY_GOAL_PREFS,
            AppCompatActivity.MODE_PRIVATE
        ).getString(DAILY_GOAL_FROM_PREFS, "")?.toFloat() ?: 0f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSevenDayInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun updateUI() {
        // obrada i prikaz podataka u grafu
        // preuzeto sa https://github.com/PhilJay/MPAndroidChart
        // obrada i izgled nisu baš prejasno pojašnjeni, malo sam se s time igrao kako bi dobio
        // što lijepši prikaz ali nisam baš pohvatao sve customizacije koje se ispod koriste
        barDataSet1 = BarDataSet(getBarChartDataForSet1(), "Steps made in last 7 days")
        barDataSet1.color = resources.getColor(R.color.blue_700)
        barDataSet2 = BarDataSet(getBarChartDataForSet2(), "Steps goal for last 7 days")
        barDataSet2.color = resources.getColor(R.color.grey_700)

        // on below line we are adding bar data set to bar data
        val data = BarData(barDataSet1, barDataSet2)

        // on below line we are setting data to our chart
        binding.barChart.data = data

        // on below line we are setting description enabled.
        binding.barChart.description.isEnabled = false

        // on below line setting x axis
        val xAxis = binding.barChart.xAxis

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(true)

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // below line is to set granularity
        // to our x axis labels.
        xAxis.granularity = 4f

        // below line is to enable
        // granularity to our x axis.
        xAxis.isGranularityEnabled = true

        // below line is to make our
        // bar chart as draggable.
        binding.barChart.isDragEnabled = false
        binding.barChart.setScaleEnabled(false)

        // below line is to make visible
        // range for our bar chart.
        binding.barChart.setVisibleXRangeMaximum(1f)

        // below line is to add bar
        // space to our chart.
        val barSpace = 0.2f

        // below line is use to add group
        // spacing to our bar chart.
        val groupSpace = 0.5f

        // we are setting width of
        // bar in below line.
        data.barWidth = 0.3f

        // below line is to set minimum
        // axis to our chart.
        binding.barChart.xAxis.axisMinimum = 0f
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.axisRight.axisMinimum = 0f

        // below line is to
        // animate our chart.
        binding.barChart.animate()

        // below line is to group bars
        // and add spacing to it.
        binding.barChart.groupBars(0f, groupSpace, barSpace)

        // below line is to invalidate
        // our bar chart.
        binding.barChart.invalidate()
    }

    private fun getBarChartDataForSet1(): ArrayList<BarEntry> {
        barEntriesList = ArrayList()
        // on below line we are adding
        // data to our bar entries list
        barEntriesList.add(BarEntry(1f, sevenDaySteps.toFloat()))
        return barEntriesList
    }

    private fun getBarChartDataForSet2(): ArrayList<BarEntry> {
        barEntriesList = ArrayList()
        // on below line we are adding data
        // to our bar entries list
        barEntriesList.add(BarEntry(1f, dailyGoal*7))
        return barEntriesList
    }
}