package com.alvintio.pedulipangan.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alvintio.pedulipangan.adapter.FoodAdapter
import com.alvintio.pedulipangan.data.remote.ApiConfig
import com.alvintio.pedulipangan.databinding.FragmentHomeBinding
import com.alvintio.pedulipangan.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val locationManager by lazy {
        requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private val locationPermissionCode = 123
    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0
    private var userLocation: Location? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid

        userId?.let {
            firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("name")
                        binding.tvTitle.text = "Selamat Datang, $username!"
                    } else {
                        Log.d("HomeFragment", "Dokumen tidak ditemukan.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("HomeFragment", "Gagal membaca data: $exception")
                }
        }

        getUserLocation()
        getProducts()

        if (hasLocationPermission()) {
            getNearestRestaurants()
        } else {
            requestLocationPermission()
        }


        return view
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getProducts()
                getNearestRestaurants()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getUserLocation() {
        if (hasLocationPermission()) {
            try {
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        userLatitude = location.latitude
                        userLongitude = location.longitude
                        Log.d("UserLocation", "Latitude: $userLatitude, Longitude: $userLongitude")
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                    override fun onProviderEnabled(provider: String) {}

                    override fun onProviderDisabled(provider: String) {}
                }

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1f,
                    locationListener
                )

                val lastKnownLocation: Location? =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                lastKnownLocation?.let {
                    userLatitude = it.latitude
                    userLongitude = it.longitude
                    userLocation = it
                    Log.d("UserLocation", "Latitude: $userLatitude, Longitude: $userLongitude")
                }
            } catch (e: SecurityException) {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getNearestRestaurants() {
        if (hasLocationPermission()) {
            val apiService = ApiConfig.getApiService()

            try {
                val lastKnownLocation: Location? =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                lastKnownLocation?.let { location ->
                    apiService.getProducts()
                        .enqueue(object : Callback<List<Food>> {
                            override fun onResponse(
                                call: Call<List<Food>>,
                                response: Response<List<Food>>
                            ) {
                                if (response.isSuccessful) {
                                    val restaurantList = response.body()
                                    restaurantList?.let {
                                        val nearestRestaurants = filterRestaurantsByDistance(
                                            it,
                                            location.latitude,
                                            location.longitude,
                                            15.0 // Jarak maksimal dalam kilometer
                                        )
                                        Log.d("Restaurants", nearestRestaurants.toString())
                                        setupNearestRestaurantRecyclerView(nearestRestaurants)
                                    }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to get nearest restaurants",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<List<Food>>, t: Throwable) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
            } catch (e: SecurityException) {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun filterRestaurantsByDistance(
        restaurants: List<Food>,
        userLatitude: Double,
        userLongitude: Double,
        maxDistance: Double
    ): List<Food> {
        val filteredRestaurants = mutableListOf<Food>()

        userLocation?.let { userLoc ->
            for (restaurant in restaurants) {
                val restaurantLocation = Location("RestaurantLocation")
                restaurantLocation.latitude = restaurant.latitude
                restaurantLocation.longitude = restaurant.longitude

                val distance = userLoc.distanceTo(restaurantLocation) / 1000

                if (distance <= maxDistance) {
                    filteredRestaurants.add(restaurant)
                }
            }
        }

        return filteredRestaurants
    }

    private fun setupNearestRestaurantRecyclerView(restaurantList: List<Food>) {
        val foodAdapter = FoodAdapter(restaurantList)

        foodAdapter.setUserLocation(userLatitude, userLongitude)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewNearestRestaurant.layoutManager = layoutManager

        binding.recyclerViewNearestRestaurant.adapter = foodAdapter
    }


    private fun getProducts() {
        val apiService = ApiConfig.getApiService()

        apiService.getProducts().enqueue(object : Callback<List<Food>> {
            override fun onResponse(call: Call<List<Food>>, response: Response<List<Food>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    productList?.let {
                        val randomProducts = it.shuffled().take(5)
                        setupRecyclerView(randomProducts)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to get products",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Food>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(productList: List<Food>) {
        val foodAdapter = FoodAdapter(productList)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.adapter = foodAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
