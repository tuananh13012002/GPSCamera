package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.compass

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.facebook.internal.Validate.hasLocationPermission
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.FragmentCompassBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Compass
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil
import com.google.android.gms.location.LocationServices
import javax.inject.Inject

class CompassAct @Inject constructor() : AbsActivity<FragmentCompassBinding>() {

    private var compass: Compass? = null

    override fun initView() {
        if (!DeviceUtil.isLocationEnabled(this)) {
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(locationSettingsIntent)
        }
        try {
            compass = Compass(this)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Toast.makeText(this, "Either accelerometer or magnetic sensor not found", Toast.LENGTH_LONG).show()
        }
        compass?.arrowView = binding.compass
        compass?.degree?.observe(this) {
            binding.txtDegree.text = DeviceUtil.checkDegreeDirection(it.toInt())
        }
    }

    override fun initAction() {
        if (hasLocationPermission(this)) {
            if (SystemUtil.haveNetworkConnection(this)) getLocationUpdates() else Toast.makeText(
                this,
                getString(R.string.check_permission_internet),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            requestPermission()
        }
        binding.imgBack.setOnClickListener {
            finish()
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

    @SuppressLint("MissingPermission")
    fun getLocationUpdates() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    binding.progressBar.root.visibility = View.GONE
                    DeviceUtil.getAddress(it, this, binding.txtAddress)
                    binding.txtLat.text =
                        DeviceUtil.convertCoordinateToDegreesLat(location.latitude)
                    binding.txtLong.text =
                        DeviceUtil.convertCoordinateToDegreesLong(location.longitude)
                    val elevation = String.format("%.0f", location.altitude)
                    binding.txtElevation.text =
                        getString(R.string.text_elevation, elevation.replace("-", "").trim())
                }
            }
    }

    override fun getContentView() = R.layout.fragment_compass

    override fun bindViewModel() {
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
                binding.progressBar.root.visibility = View.GONE
                if (SystemUtil.haveNetworkConnection(this)) getLocationUpdates() else Toast.makeText(
                    this,
                    getString(R.string.check_permission_internet),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (DeviceUtil.isLocationEnabled(this)) getLocationUpdates()
        compass?.start()
    }

    override fun onPause() {
        super.onPause()
        compass?.stop()
    }


    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}