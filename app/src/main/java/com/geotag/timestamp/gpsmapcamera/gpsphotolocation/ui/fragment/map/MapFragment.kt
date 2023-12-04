package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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
import javax.inject.Inject

class MapFragment @Inject constructor() : Fragment(), OnMapReadyCallback {
    private var googleMap: GoogleMap?=null
    private lateinit var mapView: MapView
    private lateinit var txtAddress: TextView
    private lateinit var txtLat: TextView
    private lateinit var txtLong: TextView
    private lateinit var ivCurrentLocation: ImageView
    private lateinit var progressBar: LinearLayout
    private var initialZoom: Float = 12f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        initView(rootView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        initAction()
        return rootView
    }

    private fun initAction() {
        ivCurrentLocation.setOnClickListener {
            initialZoom++
            if (SystemUtil.haveNetworkConnection(requireContext())) showCurrentLocation(initialZoom) else Toast.makeText(
                requireContext(),
                requireActivity().getString(R.string.check_permission_internet),
                Toast.LENGTH_SHORT
            ).show()
        }
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
        if (hasLocationPermission()) {
            if (SystemUtil.haveNetworkConnection(requireContext())) {
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
                        val markerView = LayoutInflater.from(requireContext()).inflate(R.layout.marker_layout, null)
                        val txtMarker = markerView.findViewById<TextView>(R.id.txt_marker)
                        val cardView = markerView.findViewById<CardView>(R.id.marker_card_view)
                        txtMarker.text = marker.position.toString()
                        txtMarker.isSelected = true
                        return markerView
                    }
                })
            } else {
                Toast.makeText(requireContext(),  requireActivity().getString(R.string.check_permission_internet), Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentLocation(zoom: Float) {
        // disable button current location default
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.isMyLocationEnabled = true
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                progressBar.visibility=View.GONE
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    DeviceUtil.getAddress(location, requireContext(), txtAddress)
                    txtLat.text = String.format("%.2f", location.latitude)
                    txtLong.text = String.format("%.2f", location.longitude)
//                    val markerOptions = MarkerOptions().position(currentLatLng).title("Current Location")
//                    googleMap.addMarker(markerOptions)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom))
                }
            }
    }

    private fun viewToBitmap(view: View): Bitmap? {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }


    private fun initView(rootView: View) {
        if (!DeviceUtil.isLocationEnabled(requireContext())) {
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(locationSettingsIntent)
        }
        mapView = rootView.findViewById(R.id.mapView)
        txtAddress = rootView.findViewById(R.id.txt_address)
        txtLat = rootView.findViewById(R.id.txt_lat)
        txtLong = rootView.findViewById(R.id.txt_long)
        progressBar = rootView.findViewById(R.id.progress)
        ivCurrentLocation = rootView.findViewById(R.id.iv_current_location)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!DeviceUtil.isLocationEnabled(requireContext())) {
                    val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(locationSettingsIntent)
                }
                progressBar.visibility = View.GONE
                showCurrentLocation(initialZoom)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if(DeviceUtil.isLocationEnabled(requireContext())) showCurrentLocation(initialZoom)
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}