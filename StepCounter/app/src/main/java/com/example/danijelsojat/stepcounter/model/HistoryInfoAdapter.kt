package com.example.danijelsojat.stepcounter.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

// view pager adapter

class HistoryInfoAdapter(val fragments: List<Fragment>, supportFragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(supportFragmentManager, lifecycle){

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}