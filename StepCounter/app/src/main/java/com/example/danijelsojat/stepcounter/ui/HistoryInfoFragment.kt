package com.example.danijelsojat.stepcounter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.danijelsojat.stepcounter.SEVEN_DAYS_STEPS
import com.example.danijelsojat.stepcounter.THIRTY_DAYS_STEPS
import com.example.danijelsojat.stepcounter.databinding.FragmentHistoryInfoBinding
import com.example.danijelsojat.stepcounter.model.HistoryInfoAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HistoryInfoFragment : Fragment() {

    // fragment za prikaz povijesti koraka

    private lateinit var binding: FragmentHistoryInfoBinding
    private lateinit var todayInfoFragment: TodayInfoFragment
    private lateinit var sevenDayInfoFragment: SevenDayInfoFragment
    private lateinit var thirtyDayInfoFragment: ThirtyDayInfoFragment
    var sevenDaySteps: Int = 0
    var thirtyDaySteps: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inicijalizacija fragmenata i dohvaćanje podataka iz bundlea te proslijeđivanje istih podataka u sljedeće fragmente

        initFragments()
        arguments?.let {
            sevenDaySteps = it.getInt(SEVEN_DAYS_STEPS)
            thirtyDaySteps = it.getInt(THIRTY_DAYS_STEPS)

            val sevenDayBundle = Bundle().apply {
                putInt(SEVEN_DAYS_STEPS, sevenDaySteps)
            }
            sevenDayInfoFragment.arguments = sevenDayBundle

            val thirtyDayBundle = Bundle().apply {
                putInt(THIRTY_DAYS_STEPS, thirtyDaySteps)
            }
            thirtyDayInfoFragment.arguments= thirtyDayBundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // kreiranje viewa i view pagera

        val fragments = listOf(todayInfoFragment, sevenDayInfoFragment, thirtyDayInfoFragment)
        val titles = listOf("Today", "Last 7 days", "Last 30 days")

        binding.viewPager.isSaveEnabled = false
        binding.viewPager.adapter = activity?.let {
            HistoryInfoAdapter(fragments,
                childFragmentManager, viewLifecycleOwner.lifecycle)
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager){
                tab, position -> tab.text = titles[position]
        }.attach()
    }

    private fun initFragments() {
        // inicijalizacija fragmenata koji se korite u view pageru
        todayInfoFragment = TodayInfoFragment()
        sevenDayInfoFragment = SevenDayInfoFragment()
        thirtyDayInfoFragment = ThirtyDayInfoFragment()
    }

}