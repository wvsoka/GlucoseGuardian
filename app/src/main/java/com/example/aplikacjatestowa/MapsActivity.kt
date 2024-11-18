package com.example.googlemapsapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aplikacjatestowa.R
import com.example.aplikacjatestowa.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import naszeAktywnosci.MainActivity
import org.json.JSONException
import org.json.JSONObject
import android.widget.SeekBar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userId: String
    private lateinit var buttonBack: Button
    private lateinit var seekBarRadius: SeekBar
    private lateinit var textViewRadius: TextView

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
        private const val MAX_RADIUS = 50000 // Max radius in meters (50 km)
        private const val STEP_RADIUS = 5000 // Step radius in meters (5 km)
    }

    private var selectedRadius = MAX_RADIUS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        userId = intent.getStringExtra("uID").toString()

        buttonBack = findViewById(R.id.backFromMaps_button)
        buttonBack.setOnClickListener {
            openActivityMain()
        }

        seekBarRadius = findViewById(R.id.seekBar_radius)
        textViewRadius = findViewById(R.id.textView_radius)

        seekBarRadius.max = 9 // This is 50 km with 5 km steps (0 * 5km to 9 * 5km)
        seekBarRadius.progress = 9 // Default to 50 km initially
        updateRadiusText()

        // Listener for SeekBar to update radius value
        seekBarRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedRadius = (progress + 1) * STEP_RADIUS // Convert progress to radius
                updateRadiusText()
                if (this@MapsActivity::lastLocation.isInitialized) {
                    findNearbyPharmacies(LatLng(lastLocation.latitude, lastLocation.longitude))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateRadiusText() {
        val radiusKm = selectedRadius / 1000
        textViewRadius.text = "Selected radius: $radiusKm km"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this)) //do popupa

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 11f))
                findNearbyPharmacies(currentLatLong)
            }
        }
    }

    private fun findNearbyPharmacies(location: LatLng) {
        val apiKey = getString(R.string.google_maps_key)
        val locationString = "${location.latitude},${location.longitude}"
        val radius = selectedRadius
        val type = "pharmacy"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&type=$type&key=$apiKey"

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    mMap.clear()
                    val jsonObject = JSONObject(response)
                    val results = jsonObject.getJSONArray("results")

                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val latLng = place.getJSONObject("geometry")
                            .getJSONObject("location")
                        val lat = latLng.getDouble("lat")
                        val lng = latLng.getDouble("lng")
                        val placeName = place.getString("name")

                        var statusText = "Brak informacji o statusie"
                        if (place.has("opening_hours")) {
                            val openingHours = place.getJSONObject("opening_hours")
                            val isOpenNow = openingHours.optBoolean("open_now", false)
                            statusText = if (isOpenNow) "Otwarte" else "Zamknięte"
                        }

                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(lat, lng))
                                .title(placeName)
                                .snippet(statusText)
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("MapsActivity", "Błąd podczas wyszukiwania: ${error.message}")
            }) {}

        Volley.newRequestQueue(this).add(request)
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false



    private fun openActivityMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", userId)
        startActivity(intent)
    }


    class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

        private val window = (context as Activity).layoutInflater.inflate(R.layout.info_pharmacy_window, null)

        private fun render(marker: Marker, view: View) {
            val title = view.findViewById<TextView>(R.id.textView_InfoPharmacy)
            val isOpenView = view.findViewById<TextView>(R.id.textView_isopen)

            // Ustawienie nazwy apteki
            title.text = marker.title

            // Ustawienie statusu otwarcia (snippet)
            val statusText = marker.snippet ?: "Brak informacji"
            isOpenView.text = statusText

            // Zmiana koloru tekstu w zależności od statusu
            if (statusText.contains("Otwarte", true)) {
                isOpenView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            } else if (statusText.contains("Zamknięte", true)) {
                isOpenView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }
        }

        override fun getInfoWindow(marker: Marker): View? {
            render(marker, window)
            return window
        }

        override fun getInfoContents(marker: Marker): View? {
            return null
        }
    }


}
