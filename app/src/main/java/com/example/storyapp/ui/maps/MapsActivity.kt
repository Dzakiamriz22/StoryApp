package com.example.storyapp.ui.maps

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val mapsViewModel: MapsViewModel by viewModels()
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)

        // Set up Google Map
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        fetchStoriesWithLocation()
    }

    private fun fetchStoriesWithLocation() {
        val token = sharedPreferences.getString("token", "") ?: ""

        mapsViewModel.getStoriesWithLocation(token).observe(this) { stories ->
            if (stories.isNotEmpty()) {
                showMarkers(stories)
            }
        }
    }

    private fun showMarkers(stories: List<Story>) {
        // Tambahkan marker untuk setiap story dengan lokasi
        stories.forEach { story ->
            val position = LatLng(story.lat!!, story.lon!!)
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(story.name) // Judul marker adalah nama pengguna atau judul story
                    .snippet(story.description)
            )
        }

        // Fokuskan kamera ke lokasi pertama (jika ada)
        val firstLocation = stories.firstOrNull()
        firstLocation?.let {
            val position = LatLng(it.lat!!, it.lon!!)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5f))
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}