package com.example.bottomnavyt

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import okhttp3.Callback
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class RestaurantListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var getLocationButton: Button
    private lateinit var adapter: RestaurantAdapter

    private val LOCATION_REQUEST_CODE = 1
    private var apiKey = "AIzaSyCG-YhCR3j6oq8Av3Jz78OuTrjbUnzLtzI"
    private var location: String = ""
    private var radius: Int = 1500

    // Constants for SharedPreferences
    private val PREFS_NAME = "MyPrefs"
    private val PREF_LOCATION = "last_location"
    private val PREF_RADIUS = "last_radius"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_list, container, false)

        recyclerView = view.findViewById(R.id.restaurantRecyclerView)
        getLocationButton = view.findViewById(R.id.getLocationButton)
        adapter = RestaurantAdapter()

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // Load the last selected location values from SharedPreferences
        val lastSavedLocation = loadLastLocationFromSharedPreferences()
        if (lastSavedLocation != null) {
            location = lastSavedLocation.first
            radius = lastSavedLocation.second

            // Split the location into latitude and longitude
            val coordinates = location.split(", ")
            val latitude = coordinates[0]
            val longitude = coordinates[1]

            // Use getAddressFromLocation to get the address and update the button text
            getAddressFromLocation(latitude, longitude) { address ->
                activity?.runOnUiThread {
                    getLocationButton.text = address
                }
            }

            getNearbyRestaurants(apiKey, location, radius, adapter)
        } else {
            getLocationButton.text = "Select Location"
        }

        getLocationButton.setOnClickListener {
            val locationIntent = Intent(requireActivity(), LocationActivity::class.java)
            startActivityForResult(locationIntent, LOCATION_REQUEST_CODE)
        }

        adapter.onRestaurantFavoriteClickListener = { restaurant ->
            // Check for the WRITE_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, save the restaurant data
                saveRestaurantToFavorites(restaurant)
            } else {
                // Request the permission
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
        }

        adapter.onRestaurantUnfavoriteClickListener = { restaurant ->
            deleteRestaurantFromFavorites(restaurant)
        }

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val latitude = data?.getStringExtra("latitude")
            val longitude = data?.getStringExtra("longitude")
            val radiusStr = data?.getStringExtra("radius")

            if (latitude != null && longitude != null && radiusStr != null) {
                location = "$latitude, $longitude"
                radius = radiusStr.toInt()
                // Save the values in SharedPreferences
                saveLocationToSharedPreferences(location, radius)
                getNearbyRestaurants(apiKey, location, radius, adapter)

                // Convert latitude and longitude to address
                getAddressFromLocation(latitude, longitude) { address ->
                    activity?.runOnUiThread {
                        getLocationButton.text = address
                    }
                }
            } else {
                getLocationButton.text = "Select Location"
            }
        }
    }

    private fun getNearbyRestaurants(apiKey: String, location: String, radius: Int, adapter: RestaurantAdapter) {
        val client = OkHttpClient()
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=$location" +
                "&radius=$radius" +
                "&type=restaurant" +
                "&key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .build()

        if (::progressBar.isInitialized) progressBar.visibility = View.VISIBLE

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val restaurants = parseRestaurantData(responseData)
                    activity?.runOnUiThread {
                        if (::adapter.isInitialized) adapter.setData(restaurants)
                        if (::progressBar.isInitialized) progressBar.visibility = View.GONE
                    }
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle network or request failure
                activity?.runOnUiThread {
                    if (::progressBar.isInitialized) progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun getAddressFromLocation(latitude: String, longitude: String, callback: (address: String) -> Unit) {
        val client = OkHttpClient()
        val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val jsonObject = JSONObject(responseData!!)
                    val results = jsonObject.getJSONArray("results")
                    if (results.length() > 0) {
                        val formattedAddress = results.getJSONObject(0).getString("formatted_address")
                        callback(formattedAddress)
                    } else {
                        callback("Address not found")
                    }
                } else {
                    callback("Error getting address")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback("Network error")
            }
        })
    }

    private fun parseRestaurantData(responseData: String?): List<Restaurant> {
        val restaurantList = mutableListOf<Restaurant>()
        responseData?.let {
            val jsonObject = JSONObject(it)
            val results = jsonObject.getJSONArray("results")

            for (i in 0 until results.length()) {
                val restaurant = results.getJSONObject(i)
                val name = restaurant.getString("name")
                val vicinity = restaurant.getString("vicinity")

                val newRestaurant = Restaurant(name, vicinity)
                restaurantList.add(newRestaurant)
            }
        }
        return restaurantList
    }

    private fun saveRestaurantToFavorites(restaurant: Restaurant) {
        val file = File(requireContext().getExternalFilesDir(null), "favorites.csv")

        if (file.exists() && file.readText().contains("${restaurant.name},${restaurant.vicinity}")) {
            // The restaurant is already in the favorites, so we don't add it again
            return
        }
        try {
            val writer = FileWriter(file, true) // True for appending data
            writer.append("${restaurant.name},${restaurant.vicinity}\n")
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteRestaurantFromFavorites(restaurant: Restaurant) {
        val file = File(requireContext().getExternalFilesDir(null), "favorites.csv")

        if (file.exists()) {
            val restaurants = file.readLines().toMutableList()
            restaurants.removeIf { line -> line.contains("${restaurant.name},${restaurant.vicinity}") }
            file.writeText(restaurants.joinToString(separator = "\n"))
        }
    }

    private fun saveLocationToSharedPreferences(location: String, radius: Int) {
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(PREF_LOCATION, location)
        editor.putInt(PREF_RADIUS, radius)
        editor.apply()
    }

    private fun loadLastLocationFromSharedPreferences(): Pair<String, Int>? {
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val location = sharedPreferences.getString(PREF_LOCATION, null)
        val radius = sharedPreferences.getInt(PREF_RADIUS, -1)

        if (location != null && radius != -1) {
            return Pair(location, radius)
        }

        return null
    }
}
