package com.example.bottomnavyt
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class Product(
    val name: String,
    var rating: Float,
    val attributes: List<String>,
    val pricePerDay: Double
) : Parcelable
