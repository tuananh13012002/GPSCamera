package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.stamp

import android.content.Context
import android.content.Intent
import androidx.core.content.res.ResourcesCompat
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityStampOptionBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.setColorResource
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.style.StyleAct
import javax.inject.Inject

class StampOptionAct @Inject constructor() : AbsActivity<ActivityStampOptionBinding>() {
    override fun initView() {
//        handleChangeTheme()
    }

    override fun initAction() {
        binding.fontStyle.setOnClickListener {
            startActivity(StyleAct.getIntent(this, 1))
        }
        binding.btnTextColorStyle.setOnClickListener {
            startActivity(StyleAct.getIntent(this, 2))
        }
        binding.btnChooseLang.setOnClickListener {
            finish()
        }
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            sharePrefs.isShowMap=isChecked
        }
        binding.btnLogoOption.setOnClickListener {
            startActivity(StyleAct.getIntent(this, 3))
        }
        binding.switch1.isChecked = sharePrefs.isShowMap
    }


    override fun getContentView(): Int {
        return R.layout.activity_stamp_option
    }

    override fun bindViewModel() {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
//        handleChangeTheme()

    }

    private fun handleChangeTheme() {
        if (sharePrefs.fontCurrent > 0){
            binding.tvTitile.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.fontStyle.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.btnTextColorStyle.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
            binding.rateSetting.typeface = ResourcesCompat.getFont(this, sharePrefs.fontCurrent)
        }
        if (sharePrefs.colorCurrent > 0){
            binding.tvTitile.setColorResource(sharePrefs.colorCurrent)
            binding.fontStyle.setColorResource(sharePrefs.colorCurrent)
            binding.btnTextColorStyle.setColorResource(sharePrefs.colorCurrent)
            binding.rateSetting.setColorResource(sharePrefs.colorCurrent)
        }
    }
    companion object{
        fun getIntent(context: Context):Intent{
            return Intent(context, StampOptionAct::class.java)
        }
    }
}