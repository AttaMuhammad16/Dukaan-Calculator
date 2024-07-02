package com.dukaancalculator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dukaancalculator.ui.fragments.MaalFragment
import com.dukaancalculator.ui.fragments.MoreFragment
import com.dukaancalculator.ui.fragments.SalesFragment
import com.dukaancalculator.ui.fragments.UdhharFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf(
        SalesFragment(),
        MaalFragment(),
        UdhharFragment(),
        MoreFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
