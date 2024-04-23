package com.example.bottomnavyt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val loveButton: ImageView = itemView.findViewById(R.id.loveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.restaurantName.text = restaurant.name
        holder.restaurantAddress.text = restaurant.vicinity

        // Set the initial icon for the love button
        val isFavorite = isRestaurantFavorite(restaurant, holder.itemView)
        holder.loveButton.setImageResource(
            if (isFavorite) R.drawable.heart_filled else R.drawable.heart_unfilled // Use your actual drawable resource names
        )

        // Set a click listener for the love button
        holder.loveButton.setOnClickListener {
            if (isFavorite) {
                onRestaurantUnfavoriteClickListener?.invoke(restaurant)
                holder.loveButton.setImageResource(R.drawable.heart_unfilled)
            } else {
                onRestaurantFavoriteClickListener?.invoke(restaurant)
                holder.loveButton.setImageResource(R.drawable.heart_filled)
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
