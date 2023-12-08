package com.alvintio.pedulipangan.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alvintio.pedulipangan.data.remote.ApiConfig
import com.alvintio.pedulipangan.model.Food
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {
    private val _foodList = MutableLiveData<List<Food>>()
    val foodList: LiveData<List<Food>> get() = _foodList

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    private var originalFoodList: List<Food> = emptyList()

    fun fetchFoods() {
        val apiService = ApiConfig.getApiService()

        apiService.getProducts().enqueue(object : Callback<List<Food>> {
            override fun onResponse(call: Call<List<Food>>, response: Response<List<Food>>) {
                if (response.isSuccessful) {
                    originalFoodList = response.body() ?: emptyList()
                    _foodList.value = originalFoodList
                }
            }

            override fun onFailure(call: Call<List<Food>>, t: Throwable) {
            }
        })
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }


    private fun performManualSearch() {
        val query = _searchQuery.value.orEmpty().toLowerCase()

        val searchResults = if (query.isNotEmpty()) {
            originalFoodList.filter { food ->
                food.name.toLowerCase().contains(query.toLowerCase())
            }
        } else {
            originalFoodList
        }

        _foodList.value = searchResults
    }

}
