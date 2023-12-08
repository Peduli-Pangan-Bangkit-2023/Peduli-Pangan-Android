package com.alvintio.pedulipangan.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.databinding.FragmentDashboardBinding
import com.alvintio.pedulipangan.viewmodel.SharedViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    @SuppressLint("UseCompatLoadingForColorStateLists")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager: ViewPager2 = root.findViewById(R.id.viewPager)
        val adapter = DashboardPagerAdapter(requireActivity())
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        val searchView = binding.searchView
        searchView.isFocusable = true
        searchView.isFocusableInTouchMode = true
        searchView.isClickable = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                sharedViewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })

        val tabLayout: TabLayout = root.findViewById(R.id.tabLayout)
        tabLayout.setBackgroundColor(resources.getColor(R.color.dark_green))
        tabLayout.tabTextColors = resources.getColorStateList(R.color.black)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.list_dashboard)
                1 -> getString(R.string.map_dashboard)
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
