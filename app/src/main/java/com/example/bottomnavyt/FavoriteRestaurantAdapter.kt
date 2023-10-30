package com.example.bottomnavyt
// FavoriteRestaurantAdapter.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteRestaurantAdapter(private val onDeleteClickListener: (Restaurant) -> Unit) :
    RecyclerView.Adapter<FavoriteRestaurantAdapter.ViewHolder>() {

    private var restaurantList: MutableList<Restaurant> = mutableListOf()

    fun setData(data: List<Restaurant>) {
        restaurantList.clear()
        restaurantList.addAll(data)
        notifyDataSetChanged()
    }

    fun deleteItem(restaurant: Restaurant) {
        val position = restaurantList.indexOf(restaurant)
        if (position != -1) {
            restaurantList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantNameTextView: TextView = itemView.findViewById(R.id.restaurantNameTextView)
        val restaurantDescriptionTextView: TextView =
            itemView.findViewById(R.id.restaurantDescriptionTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_restaurant_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        holder.restaurantNameTextView.text = restaurant.name
        holder.restaurantDescriptionTextView.text = restaurant.vicinity

        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(restaurant)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }
}
