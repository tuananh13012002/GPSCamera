package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.Fragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityMediaBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.onClickListener
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.camera.CameraActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.fragment.PhotoFr
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.fragment.VideoFr
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants.EXTRA_TYPE_DATA_TO_DETAIL
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants.EXTRA_TYPE_STRING_DATA_TO_DETAIL
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Strings
import javax.inject.Inject

class MediaActivity @Inject constructor() : AbsActivity<ActivityMediaBinding>() {


    companion object {
        fun startActivity(activity: Activity, type: Int) {
            activity.startActivity(Intent(activity, MediaActivity::class.java).apply {
                putExtra(EXTRA_TYPE_DATA_TO_DETAIL, type)
            })
        }

        fun startActivity(activity: Activity, type: String) {
            activity.startActivity(Intent(activity, MediaActivity::class.java).apply {
                putExtra(EXTRA_TYPE_STRING_DATA_TO_DETAIL, type)
            })
        }

        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, MediaActivity::class.java).apply {
            })
        }
    }

    override fun initView() {
        val type=intent.getIntExtra(EXTRA_TYPE_DATA_TO_DETAIL,0)
        Strings.log("TuanAnh kkk",type)
        if(type==0){
            replaceFragment(PhotoFr())
            binding.btnPhoto.setBackgroundResource(R.drawable.tab_indicator)
            binding.btnPhoto.setTextColor(Color.parseColor("#F1B15E"))

            binding.btnVideo.setBackgroundResource(R.drawable.tab_indicator_normal)
            binding.btnVideo.setTextColor(Color.parseColor("#252932"))

        }else{
            replaceFragment(VideoFr())
            binding.btnVideo.setBackgroundResource(R.drawable.tab_indicator)
            binding.btnVideo.setTextColor(Color.parseColor("#F1B15E"))

            binding.btnPhoto.setBackgroundResource(R.drawable.tab_indicator_normal)
            binding.btnPhoto.setTextColor(Color.parseColor("#252932"))
        }
        initTab()
    }

    private fun initTab() {
        binding.btnPhoto.onClickListener{
            binding.btnPhoto.setBackgroundResource(R.drawable.tab_indicator)
            binding.btnPhoto.setTextColor(Color.parseColor("#F1B15E"))

            binding.btnVideo.setBackgroundResource(R.drawable.tab_indicator_normal)
            binding.btnVideo.setTextColor(Color.parseColor("#252932"))

            replaceFragment(PhotoFr())
        }
        binding.btnVideo.onClickListener{
            binding.btnVideo.setBackgroundResource(R.drawable.tab_indicator)
            binding.btnVideo.setTextColor(Color.parseColor("#F1B15E"))

            binding.btnPhoto.setBackgroundResource(R.drawable.tab_indicator_normal)
            binding.btnPhoto.setTextColor(Color.parseColor("#252932"))

            replaceFragment(VideoFr())

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment)
        fragmentTransaction.commit()
        when(fragment){
            is PhotoFr ->binding.flScan.setOnClickListener { startActivity(CameraActivity.getIntent(this,0)) }
            is VideoFr ->binding.flScan.setOnClickListener { startActivity(CameraActivity.getIntent(this,1)) }
        }
    }

    override fun initAction() {
        binding.imgBack.setOnClickListener {
            finish()
        }
    }


    override fun getContentView(): Int = R.layout.activity_media

    override fun bindViewModel() {

    }

}