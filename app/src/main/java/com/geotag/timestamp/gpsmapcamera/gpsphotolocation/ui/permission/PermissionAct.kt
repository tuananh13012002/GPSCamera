package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.permission

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityPermissionBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.hasPermission
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main.MainActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil
import javax.inject.Inject

class PermissionAct @Inject constructor() : AbsActivity<ActivityPermissionBinding>() {
    override fun initView() {
    }

    override fun initAction() {
        setDefaultSwitch()
        binding.btnGo.setOnClickListener {
            if (binding.switch1.isChecked && binding.switch2.isChecked && binding.switch3.isChecked ) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                checkStorePermission()
            }
        }
        binding.switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                checkCameraPermission()
            }
        }
        binding.switch3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                checkLocationPermission()
            }
        }
        binding.switch4.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                checkBodySensorsPermission()
            }
        }
        maxPermission()
    }

    private fun checkBodySensorsPermission() {
        if (hasPermission(DeviceUtil.arrayBodySensorsPermission)) {
            ActivityCompat.requestPermissions(this, DeviceUtil.arrayBodySensorsPermission, REQUEST_CODE_BODY_SENSORS_PERMISSION)
        }
    }

    private fun setDefaultSwitch() {
        binding.switch1.isChecked = hasPermission(DeviceUtil.arrayStoragePermission)
        if(binding.switch1.isChecked){binding.switch1.isEnabled = false}
        binding.switch2.isChecked = hasPermission(DeviceUtil.arrayRecordPermission)
        if(binding.switch2.isChecked){binding.switch2.isEnabled = false}
        binding.switch3.isChecked = hasPermission(DeviceUtil.arrayLocationPermission)
        if(binding.switch3.isChecked){binding.switch3.isEnabled = false}
        binding.switch4.isChecked = hasPermission(DeviceUtil.arrayBodySensorsPermission)
        if(binding.switch4.isChecked){binding.switch4.isEnabled = false}
    }


    override fun getContentView(): Int {
        return R.layout.activity_permission
    }

    override fun bindViewModel() {
    }

    private fun checkLocationPermission(){
        if (!hasPermission(DeviceUtil.arrayLocationPermission)) {
            ActivityCompat.requestPermissions(this, DeviceUtil.arrayLocationPermission, PERMISSION_REQUEST_CODE)
        }
    }


    private fun checkStorePermission() {
        if (!hasPermission(DeviceUtil.arrayStoragePermission)) {
            ActivityCompat.requestPermissions(this, DeviceUtil.arrayStoragePermission, REQUEST_CODE_STORAGE_PERMISSION)
        }
    }

    private fun maxPermission() {
        if (!binding.switch1.isChecked) {
            binding.switch1.setOnClickListener {
                indexClickPermissionStore++
                if (indexClickPermissionStore > 2) {
                    startSettingApp()
                }
            }
        }

        if (!binding.switch2.isChecked) {
            binding.switch2.setOnClickListener {
                indexClickPermissionCamera++
                if (indexClickPermissionCamera > 2) {
                    startSettingApp()
                }
            }
        }
        if (!binding.switch3.isChecked) {
            binding.switch3.setOnClickListener {
                indexClickPermissionLocation++
                if (indexClickPermissionLocation > 2) {
                    startSettingApp()
                }
            }
        }
    }

    private fun startSettingApp() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        requestAppSettingsLauncher.launch(intent)
    }

    private val requestAppSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (indexClickPermissionStore > 2) {
                    checkStorePermission()
                }
                if (indexClickPermissionCamera > 2) {
                    checkCameraPermission()
                }
                if (indexClickPermissionLocation > 2) {
                    checkLocationPermission()
                }
            }
        }

    private fun checkCameraPermission() {
        if (!hasPermission(DeviceUtil.arrayRecordPermission)) {
            ActivityCompat.requestPermissions(this, DeviceUtil.arrayRecordPermission, CAMERA_PERMISSION_CODE)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (binding.switch1.isChecked && binding.switch2.isChecked && binding.switch3.isChecked) {
            binding.btnGo.visibility = View.VISIBLE
        }else{
            binding.btnGo.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                if (permissionWasAllowed(grantResults)){
                    binding.switch1.isChecked = true
                    binding.switch1.isEnabled = false
                }else{
                    binding.switch1.isChecked = false
                }
            }
            CAMERA_PERMISSION_CODE -> {
                if (permissionWasAllowed(grantResults)){
                    binding.switch2.isChecked = true
                    binding.switch2.isEnabled = false
                }else{
                    binding.switch2.isChecked = false
                }
            }
            PERMISSION_REQUEST_CODE ->{
                if (permissionWasAllowed(grantResults)){
                    if (!DeviceUtil.isLocationEnabled(this)) {
                        val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(locationSettingsIntent)
                    }
                    binding.switch3.isChecked = true
                    binding.switch3.isEnabled = false
                }else{
                    binding.switch3.isChecked = false
                }
            }
            REQUEST_CODE_BODY_SENSORS_PERMISSION -> {
                if (permissionWasAllowed(grantResults)){
                    binding.switch4.isChecked = true
                    binding.switch4.isEnabled = false
                }else{
                    binding.switch4.isChecked = false
                }
            }
        }
    }

    private fun permissionWasAllowed(arr: IntArray): Boolean{
        var granted = 0
        arr.forEach { it ->
            if (it == PackageManager.PERMISSION_DENIED){ granted++ }
        }
        return granted == 0
    }

    override fun onResume() {
        super.onResume()
        binding.switch1.isChecked = DeviceUtil.hasStoragePermission(this)
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 101
        private const val CAMERA_PERMISSION_CODE = 1001
        private const val PERMISSION_REQUEST_CODE = 123
        private const val REQUEST_CODE_BODY_SENSORS_PERMISSION = 125
        var indexClickPermissionStore = 0
        var indexClickPermissionCamera = 0
        var indexClickPermissionLocation = 0
    }
}