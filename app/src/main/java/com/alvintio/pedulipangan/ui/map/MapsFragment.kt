package com.alvintio.pedulipangan.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alvintio.pedulipangan.R
import com.alvintio.pedulipangan.data.remote.ApiConfig
import com.alvintio.pedulipangan.model.Food
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Response

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_maps, container, false)

        mapView = rootView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val apiService = ApiConfig.getApiService()

        apiService.getProducts().enqueue(object : retrofit2.Callback<List<Food>> {
            override fun onResponse(call: Call<List<Food>>, response: Response<List<Food>>) {
                if (response.isSuccessful) {
                    val foodList = response.body()
                    foodList?.let {
                        updateMarkers(it)
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<List<Food>>, t: Throwable) {
            }
        })
    }

    private fun updateMarkers(foodList: List<Food>) {
        activity?.runOnUiThread {
            mMap.clear()
            foodList.forEach { food ->
                val location = LatLng(food.latitude, food.longitude)
                mMap.addMarker(MarkerOptions().position(location).title(food.name))
            }

            if (foodList.isNotEmpty()) {
                val initialLocation = LatLng(foodList[0].latitude, foodList[0].longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
