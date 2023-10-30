package com.example.bottomnavyt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class FavoriteRestaurantsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_restaurants, container, false)

        recyclerView = view.findViewById(R.id.favoriteRestaurantRecyclerView)
        adapter = RestaurantAdapter()

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        loadFavoriteRestaurants()

        return view
    }

    private fun loadFavoriteRestaurants() {
        val file = File(requireContext().getExternalFilesDir(null), "favorites.csv")

        if (file.exists()) {
            try {
                val reader = BufferedReader(FileReader(file))
                val restaurantList = mutableListOf<Restaurant>()

                reader.forEachLine {
                    val parts = it.split(",")
                    if (parts.size >= 2) {
                        val restaurant = Restaurant(parts[0], parts[1])
                        restaurantList.add(restaurant)
                    }
                }

                adapter.setData(restaurantList) // Assuming your adapter has a setData method to update the displayed data

                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}