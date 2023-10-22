package com.example.bottomnavyt

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationData(val radius: Int, val latitude: Double, val longitude: Double) : Parcelable
