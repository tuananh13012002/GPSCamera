package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.splash

import android.content.Intent
import android.os.Bundle
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivitySplashBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.hasPermission
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main.MainActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.multi_lang.MultiLangAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.permission.PermissionAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil
import com.google.android.gms.ads.MobileAds
import javax.inject.Inject

class SplashActivity @Inject constructor() : AbsActivity<ActivitySplashBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
    }

    override fun initView() {
        binding.imgLaunch.postDelayed({
            SystemUtil.setPreLanguage(this, SystemUtil.getPreLanguage(this))
            SystemUtil.setLocale(this)
            openMainActivity()
        }, 3000)
    }

    override fun initAction() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_splash
    }

    override fun bindViewModel() {

    }

    private fun openMainActivity() {
        if (hasPermission(DeviceUtil.arrayStoragePermission) &&
            hasPermission(DeviceUtil.arrayRecordPermission) &&
            hasPermission(DeviceUtil.arrayLocationPermission)
//            && hasPermission(DeviceUtil.arrayBodySensorsPermission)
        ) {

            if (!sharePrefs.isFirstOpen) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(MultiLangAct.getIntent(this, 1))
                finish()
            }
        } else {
            if (!sharePrefs.isFirstOpen) {
                startActivity(Intent(this, PermissionAct::class.java))
                finish()
            } else {
                startActivity(MultiLangAct.getIntent(this, 1))
                finish()
            }
        }


    }
}