package com.alvintio.pedulipangan.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.adapter.FoodAdapter
import com.alvintio.pedulipangan.data.remote.ApiConfig
import com.alvintio.pedulipangan.model.Food
import com.alvintio.pedulipangan.ui.dashboard.DashboardViewModel
import com.alvintio.pedulipangan.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListFragment : Fragment() {
    private lateinit var foodAdapter: FoodAdapter
    private var allFoodList: List<Food> = emptyList()
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        foodAdapter = FoodAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = foodAdapter

        getFoodList()

        sharedViewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            Log.d("ListFragment", "Received search query: $query")
            performLocalSearch(query)
        }

        return view
    }

    private fun getFoodList() {
        val apiService = ApiConfig.getApiService()

        apiService.getProducts().enqueue(object : Callback<List<Food>> {
            override fun onResponse(call: Call<List<Food>>, response: Response<List<Food>>) {
                if (response.isSuccessful) {
                    val foodList = response.body()
                    foodList?.let {
                        foodAdapter.updateData(it)
                        allFoodList = it
                    }
                }
            }

            override fun onFailure(call: Call<List<Food>>, t: Throwable) {
            }
        })
    }

    private fun performLocalSearch(query: String) {
        Log.d("ListFragment", "Performing local search: $query")

        val searchResults = allFoodList.filter { food ->
            food.name.contains(query, ignoreCase = true)
        }

        foodAdapter.updateData(searchResults)
    }
}