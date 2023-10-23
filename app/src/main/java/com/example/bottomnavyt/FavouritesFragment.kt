package com.example.bottomnavyt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment() {

    private lateinit var tvRestaurantNameDetail: TextView
    private lateinit var tvRestaurantAddressDetail: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        tvRestaurantNameDetail = view.findViewById(R.id.tvRestaurantNameDetail)
        tvRestaurantAddressDetail = view.findViewById(R.id.tvRestaurantAddressDetail)

        val name = arguments?.getString("name")
        val address = arguments?.getString("address")

        tvRestaurantNameDetail.text = name
        tvRestaurantAddressDetail.text = address

        return view
    }
}
