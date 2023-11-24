package com.alvintio.pedulipangan.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alvintio.pedulipangan.ui.list.ListFragment
import com.alvintio.pedulipangan.ui.map.MapsFragment

class DashboardPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Assuming you have two fragments (list and map)
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListFragment() as Fragment // Replace with your actual list fragment class
            1 -> MapsFragment()// Replace with your actual map fragment class
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
