package com.example.bottomnavyt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class RestaurantAdapter : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {
    private var restaurants: List<Restaurant> = emptyList()

    var onRestaurantFavoriteClickListener: ((Restaurant) -> Unit)? = null
    var onRestaurantUnfavoriteClickListener: ((Restaurant) -> Unit)? = null

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)
        val restaurantAddress: TextView = itemView.findViewById(R.id.restaurantAddress)
        val favoriteButton: Button = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.restaurantName.text = restaurant.name
        holder.restaurantAddress.text = restaurant.vicinity

        // Check if the restaurant is already in the favorites
        if (isRestaurantFavorite(restaurant, holder.itemView)) {
            holder.favoriteButton.text = "Unfavorite"
        } else {
            holder.favoriteButton.text = "Favorite"
        }

        holder.favoriteButton.setOnClickListener {
            if (holder.favoriteButton.text == "Favorite") {
                onRestaurantFavoriteClickListener?.invoke(restaurant)
                holder.favoriteButton.text = "Unfavorite"
            } else {
                onRestaurantUnfavoriteClickListener?.invoke(restaurant)
                holder.favoriteButton.text = "Favorite"
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    fun setData(data: List<Restaurant>) {
        restaurants = data
        notifyDataSetChanged()
    }

    private fun isRestaurantFavorite(restaurant: Restaurant, itemView: View): Boolean {
        val file = File(itemView.context.getExternalFilesDir(null), "favorites.csv")
        return file.exists() && file.readText().contains("${restaurant.name},${restaurant.vicinity}")
    }
}
