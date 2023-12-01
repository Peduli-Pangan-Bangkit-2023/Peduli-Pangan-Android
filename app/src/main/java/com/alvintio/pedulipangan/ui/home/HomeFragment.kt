package com.alvintio.pedulipangan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.alvintio.pedulipangan.adapter.FoodAdapter
import com.alvintio.pedulipangan.databinding.FragmentHomeBinding
import com.alvintio.pedulipangan.adapter.RestaurantAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val restaurantList: List<String> = listOf("Restaurant 1", "Restaurant 2", "Restaurant 3",
            "Restaurant 4", "Restaurant 5", "Restaurant 6", "Restaurant 7", "Restaurant 8",
            "Restaurant 9", "Restaurant 10", "Restaurant 11", "Restaurant 12", "Restaurant 13",
            "Restaurant 14", "Restaurant 15", "Restaurant 16", "Restaurant 17", "Restaurant 18")

        val restaurantAdapter = RestaurantAdapter(restaurantList)
        val foodAdapter = FoodAdapter(restaurantList, restaurantAdapter)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = foodAdapter

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
