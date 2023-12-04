package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityMainBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.camera.CameraActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.HomeFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.HomeViewModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.setting.SettingFr
import javax.inject.Inject

class MainActivity @Inject constructor() : AbsActivity<ActivityMainBinding>()
//    , BottomMainWidget.IOnEventBottomMain
{

//    private val viewModel: MainActivityViewModel by viewModels {
//        viewModelFactory
//    }

    private val viewModel by viewModels<HomeViewModel>()

    private var lastClickTime: Long = 0
    private val clickInterval: Long = 1000

    override fun initView() {
        sharePrefs.isFirstOpen = false
        replaceFragment(HomeFragment())
    }

    override fun initAction() {
//        binding.widgetBottomMain.setEvent(this)
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {

                    replaceFragment(HomeFragment())
                }

//                R.id.menu_map -> {
//                    val currentTime = SystemClock.elapsedRealtime()
//                    if (currentTime - lastClickTime >= clickInterval) {
//                        lastClickTime = currentTime
//                        replaceFragment(MapFragment())
//                    }
//                }
//
//                R.id.menu_compass -> {
//                    replaceFragment(CompassFragment())
//                }

                R.id.menu_setting -> {
                    replaceFragment(SettingFr())
                }
            }
            true
        }
        binding.flScan.setOnClickListener {
            startActivity(CameraActivity.getIntent(this,0))
        }
        initActionSetting()
    }


    private fun insertData(){
        viewModel.deleteAll()
        val medias=viewModel.getMediaFile(this)
        medias.map {
            viewModel.insert(it)
        }
    }

    private fun initActionSetting() {
        val backSettingsFragment = intent.getBooleanExtra("back_setting", false)
        if (backSettingsFragment) {
            replaceFragment(SettingFr())
            binding.bottomNav.selectedItemId = R.id.menu_setting
        }
    }

     private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun bindViewModel() {
    }

    override fun onResume() {
        super.onResume()
        insertData()
    }

//    override fun onClickItem(pos: Int) {
//        when (pos) {
//            0 -> {
//                replaceFragment(HomeFragment())
//            }
//
//            1-> {
//                replaceFragment(MapFragment())
//            }
//
//            2 -> {
//                replaceFragment(CompassFragment())
//            }
//
//            3 -> {
//                startActivity(CameraActivity.getIntent(this))
//            }
//        }
//    }
}