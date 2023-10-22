package com.example.bottomnavyt

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class LocationActivity : AppCompatActivity() {

    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var mapView: MapView
    private lateinit var locateButton: Button
    private lateinit var radiusSeekBar: SeekBar
    private val LOCATION_PERMISSION_REQUEST = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null
    private var radiusCircle: Circle? = null
    private var userInteractedWithMap: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Initialize views
        latitudeEditText = findViewById(R.id.latitudeEditText)
        longitudeEditText = findViewById(R.id.longitudeEditText)
        submitButton = findViewById(R.id.submitButton)
        mapView = findViewById(R.id.mapView)
        locateButton = findViewById(R.id.locateButton)
        radiusSeekBar = findViewById(R.id.radiusSeekBar)

        // Initialize the map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            googleMap = map

            // Enable map panning (moving)
            val uiSettings: UiSettings = googleMap.uiSettings
            uiSettings.isScrollGesturesEnabled = true

            // Enable zoom gestures (double-tap to zoom)
            uiSettings.isZoomGesturesEnabled = true

            // Set an initial location, e.g., Melbourne
            val initialLocation = LatLng(-37.8136, 144.9631)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))

            // Register a click listener for the map
            map.setOnMapClickListener { latLng ->
                // This code should be executed when you click on the map
                // It allows you to select a different latitude and longitude
                val latitude = latLng.latitude.toString()
                val longitude = latLng.longitude.toString()

                // Update the EditText fields with the selected values
                latitudeEditText.setText(latitude)
                longitudeEditText.setText(longitude)

                // Add or update the marker to the clicked location
                if (currentMarker == null) {
                    val markerOptions = MarkerOptions().position(latLng)
                    currentMarker = map.addMarker(markerOptions)
                } else {
                    currentMarker?.position = latLng
                }

                // Update the radius circle with the new marker position
                updateRadiusCircle()
            }

            map.setOnMapLongClickListener { latLng ->
                // Handle long click to pan the map
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }

            // Rest of your code
        }

        radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the "Radius (meters)" TextView with the current radius value
                val radiusText = "Radius (meters): $progress"
                findViewById<TextView>(R.id.radiusLabel).text = radiusText
                updateRadiusCircle()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not used in this example
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not used in this example
            }
        })

        submitButton.setOnClickListener {
            // Handle the submit button click
            val latitude = latitudeEditText.text.toString()
            val longitude = longitudeEditText.text.toString()
            val radius = radiusSeekBar.progress.toString()

            val resultIntent = Intent()
            resultIntent.putExtra("latitude", latitude)
            resultIntent.putExtra("longitude", longitude)
            resultIntent.putExtra("radius", radius)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        locateButton.setOnClickListener {
            // Check if location permissions are granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates()
            } else {
                // Request location permissions
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            }
        }

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates()
            }
        }
    }

    // Method to start location updates and update the EditText fields
    private fun startLocationUpdates() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatitude = location.latitude
                        val currentLongitude = location.longitude

                        // Update the EditText fields with the current location
                        latitudeEditText.setText(currentLatitude.toString())
                        longitudeEditText.setText(currentLongitude.toString())

                        // Create a LatLng for the current location
                        val currentLocation = LatLng(currentLatitude, currentLongitude)

                        // Check if the user hasn't moved or zoomed the map manually
                        if (!userInteractedWithMap) {
                            // Center the map around the current location marker with zoom level 16
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
                        }

                        // Update the marker on the map
                        if (currentMarker == null) {
                            val markerOptions = MarkerOptions().position(currentLocation)
                            currentMarker = googleMap.addMarker(markerOptions)
                        } else {
                            currentMarker?.position = currentLocation
                        }

                        // Update the radius circle with the new marker position
                        updateRadiusCircle()
                    }
                }
        } catch (e: SecurityException) {
            // Handle the case where the permission is granted but there's still a security exception
            // (e.g., GPS disabled or location provider is not available)
            // You can display a message to the user or take other appropriate action here.
        }
    }

    // Method to update the radius circle on the map
    private fun updateRadiusCircle() {
        currentMarker?.position?.let { currentLocation ->
            val radius = radiusSeekBar.progress.toDouble()
            if (radiusCircle == null) {
                radiusCircle = googleMap.addCircle(
                    CircleOptions()
                        .center(currentLocation)
                        .radius(radius)
                        .strokeWidth(2f)
                        .strokeColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .fillColor(ContextCompat.getColor(this, R.color.circleFillColor))
                )
            } else {
                radiusCircle?.center = currentLocation
                radiusCircle?.radius = radius
            }
        }
    }
}
