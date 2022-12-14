package com.dicoding.picodiploma.loginwithanimation.view.main

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.getTimeUpload
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.utility.NetworkResponse
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        getMyLocation()

        lifecycleScope.launchWhenCreated {
            mainViewModel.getUser().collect { user ->
                val token = user.token
                Log.d("User Token Maps", token)
                binding.apply {
                    mainViewModel.getStoriesWithLocation(token).observe(this@MapsActivity){ dataStories ->
                        when(dataStories){
                            is NetworkResponse.Success -> {
                                Toast.makeText(this@MapsActivity, "Sukses Memuat", Toast.LENGTH_SHORT).show()
                                with(dataStories.data.stories){
                                    forEach { stories ->
                                        Log.d("Data Maps", stories.toString())
                                        try {
                                            Glide.with(this@MapsActivity)
                                                .asBitmap()
                                                .circleCrop()
                                                .load(stories.photoUrl)
                                                .error(R.drawable.ic_image_add)
                                                .into(object : SimpleTarget<Bitmap>(){
                                                    override fun onResourceReady(
                                                        resource: Bitmap,
                                                        transition: Transition<in Bitmap>?
                                                    ) {
                                                        if (stories.lat != null && stories.lon != null){
                                                            googleMap.addMarker(
                                                                MarkerOptions()
                                                                    .position(LatLng(stories.lat, stories.lon))
                                                                    .title(stories.name)
                                                                    .snippet(getTimeUpload(stories.createdAt))
                                                                    .icon(
                                                                        BitmapDescriptorFactory.fromBitmap(
                                                                            Bitmap.createScaledBitmap(resource, 100, 100, false)
                                                                        )
                                                                    )
                                                            )
                                                        }
                                                    }
                                                })
                                        } catch (e: Exception){
                                            Log.d("Exeption Maps", e.toString())
                                            val imgError = resources.getDrawable(R.drawable.ic_image_add).toBitmap(100, 100, null)
                                            googleMap.addMarker(
                                                MarkerOptions()
                                                    .position(LatLng(stories.lat!!, stories.lon!!))
                                                    .title(stories.name)
                                                    .snippet(getTimeUpload(stories.createdAt))
                                                    .icon(
                                                        BitmapDescriptorFactory.fromBitmap(
                                                            Bitmap.createScaledBitmap(imgError, 100, 100, false)
                                                        )
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                            is NetworkResponse.Loading -> {
                                Log.d("Data Loading Maps", dataStories.toString())
                                Toast.makeText(this@MapsActivity, "Sedang Memuat", Toast.LENGTH_SHORT).show()
                            }
                            is NetworkResponse.Error -> {
                                Log.d("Data Loading Maps", dataStories.toString())
                                Toast.makeText(this@MapsActivity, "Gagal Memuat Data", Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            Log.e("Maps Style", "Style parsing success.")
            if (!success) {
                Log.e("Maps Style", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("Maps Style", "Can't find style. Error: ", exception)
        }
    }
}