package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.compass

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.FragmentCompassBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Compass
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil
import com.google.android.gms.location.LocationServices
import javax.inject.Inject

class CompassFragment @Inject constructor() : AbsFragment<FragmentCompassBinding>() {

    private var compass: Compass? = null


    override fun getLayoutRes(): Int {
        return R.layout.fragment_compass
    }

    override fun initAction() {
        if (hasLocationPermission()) {
            if(SystemUtil.haveNetworkConnection(requireContext())) getLocationUpdates() else Toast.makeText(requireContext(),  requireActivity().getString(R.string.check_permission_internet), Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationUpdates() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    binding.progressBar.root.visibility = View.GONE
                    DeviceUtil.getAddress(it, requireContext(), binding.txtAddress)
                    binding.txtLat.text = DeviceUtil.convertCoordinateToDegreesLat(location.latitude)
                    binding.txtLong.text = DeviceUtil.convertCoordinateToDegreesLong(location.longitude)
                    val elevation = String.format("%.0f", location.altitude)
                    binding.txtElevation.text = requireActivity().getString(R.string.text_elevation,elevation.replace("-", "").trim())
                }
            }
    }

    override fun initView() {
        if (!DeviceUtil.isLocationEnabled(requireContext())) {
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(locationSettingsIntent)
        }
        try {
            compass = Compass(requireContext())
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Either accelerometer or magnetic sensor not found",
                Toast.LENGTH_LONG
            ).show()
        }
        compass?.arrowView = binding.compassImageView
        compass?.degree?.observe(viewLifecycleOwner) {
            binding.txtDegree.text = DeviceUtil.checkDegreeDirection(it.toInt())
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
                binding.progressBar.root.visibility = View.GONE
                if(context?.let { SystemUtil.haveNetworkConnection(it) } == true) getLocationUpdates() else Toast.makeText(requireContext(),  requireActivity().getString(R.string.check_permission_internet), Toast.LENGTH_SHORT).show()
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
        if(DeviceUtil.isLocationEnabled(requireContext())) getLocationUpdates()
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