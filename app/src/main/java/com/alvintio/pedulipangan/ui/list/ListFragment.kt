package com.alvintio.pedulipangan.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.adapter.FoodAdapter
import com.alvintio.pedulipangan.data.remote.ApiConfig
import com.alvintio.pedulipangan.model.Food
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListFragment : Fragment() {
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        foodAdapter = FoodAdapter(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = foodAdapter

        getFoodList()

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
                    }
                }
            }

            override fun onFailure(call: Call<List<Food>>, t: Throwable) {
            }
        })
    }
}
