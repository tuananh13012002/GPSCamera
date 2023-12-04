package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.facebook.internal.Validate.hasLocationPermission
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapAct : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var mapView: MapView?=null
    private var txtAddress: TextView?=null
    private var txtLat: TextView?=null
    private var txtLong: TextView?=null
    private var ivCurrentLocation: ImageView?=null
    private var imgBack: ImageView?=null
    private var progressBar: LinearLayout?=null
    private var initialZoom: Float = 12f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtil.setPreLanguage(this, SystemUtil.getPreLanguage(this))
        SystemUtil.setLocale(this)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        setContentView(R.layout.acitivity_map)
        initView()
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        initAction()
    }

    private fun initAction() {
        ivCurrentLocation?.setOnClickListener {
            initialZoom++
            if (SystemUtil.haveNetworkConnection(this)) showCurrentLocation(initialZoom) else Toast.makeText(
                this, getString(R.string.check_permission_internet),
                Toast.LENGTH_SHORT
            ).show()
        }
        imgBack?.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        if (!DeviceUtil.isLocationEnabled(this)) {
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(locationSettingsIntent)
        }
        mapView = findViewById(R.id.mapView)
        txtAddress = findViewById(R.id.txt_address)
        txtLat = findViewById(R.id.txt_lat)
        txtLong = findViewById(R.id.txt_long)
        progressBar = findViewById(R.id.progress)
        ivCurrentLocation = findViewById(R.id.iv_current_location)
        imgBack = findViewById(R.id.img_back)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
//        val markerView = LayoutInflater.from(requireContext()).inflate(R.layout.marker_layout, null)
//        val txtMarker = markerView.findViewById<TextView>(R.id.txt_marker)
//        val cardView = markerView.findViewById<CardView>(R.id.marker_card_view)
//        val bimap = viewToBitmap(cardView)?.let {
//            Bitmap.createScaledBitmap(
//                it,
//                cardView.width,
//                cardView.height,
//                false
//            )
//        }
//        val smallMarker = bimap?.let { BitmapDescriptorFactory.fromBitmap(it) }
        if (hasLocationPermission(this)) {
            if (SystemUtil.haveNetworkConnection(this)) {
                showCurrentLocation(initialZoom)
                googleMap?.setOnMapClickListener { latLng ->
                    googleMap?.clear()
                    val markerOptions = MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    val marker = googleMap?.addMarker(markerOptions)
                    marker?.showInfoWindow()
                }
                googleMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                    override fun getInfoWindow(marker: Marker): View? {
                        return null // Return null to use default info window
                    }

                    override fun getInfoContents(marker: Marker): View? {
                        val markerView = LayoutInflater.from(this@MapAct).inflate(R.layout.marker_layout, null)
                        val txtMarker = markerView.findViewById<TextView>(R.id.txt_marker)
                        val cardView = markerView.findViewById<CardView>(R.id.marker_card_view)
                        txtMarker.text = marker.position.toString()
                        txtMarker.isSelected = true
                        return markerView
                    }
                })
            } else {
                Toast.makeText(this,  getString(R.string.check_permission_internet), Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission()
        }
    }


    @SuppressLint("MissingPermission")
    private fun showCurrentLocation(zoom: Float) {
        // disable button current location default
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.isMyLocationEnabled = true
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                progressBar?.visibility=View.GONE
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    txtAddress?.let { it1 -> DeviceUtil.getAddress(location, this, it1) }
                    txtLat?.text = String.format("%.2f", location.latitude)
                    txtLong?.text = String.format("%.2f", location.longitude)
//                    val markerOptions = MarkerOptions().position(currentLatLng).title("Current Location")
//                    googleMap.addMarker(markerOptions)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom))
                }
            }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!DeviceUtil.isLocationEnabled(this)) {
                    val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(locationSettingsIntent)
                }
                progressBar?.visibility = View.GONE
                showCurrentLocation(initialZoom)
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(DeviceUtil.isLocationEnabled(this)) showCurrentLocation(initialZoom)
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }


    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }



}