package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.intro

import android.content.Intent
import androidx.viewpager2.widget.ViewPager2
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.IntroEntity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityIntroBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.intro.adaper.IntroAdapter
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.permission.PermissionAct
import javax.inject.Inject

class IntroAct @Inject constructor() : AbsActivity<ActivityIntroBinding>() {
    private lateinit var introAdapter: IntroAdapter
    private var position = 0

    override fun getContentView(): Int {
        return R.layout.activity_intro
    }

    override fun bindViewModel() {
    }

    override fun initView() {
        if(!sharePrefs.isIntro){
            startActivity(Intent(this, PermissionAct::class.java))
            finish()
        }
        val intro = listOf(
            IntroEntity(getString(R.string.title_intro_1), getString(R.string.content_intro_1), R.drawable.intro_1),
            IntroEntity(getString(R.string.title_intro_2), getString(R.string.content_intro_2), R.drawable.intro_2),
            IntroEntity(getString(R.string.title_intro_3), getString(R.string.content_intro_3), R.drawable.intro_3)
        )
        introAdapter = IntroAdapter(intro)
        binding.viewPager.adapter = introAdapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    binding.buttonStartIntro.text=getString(R.string.text_start)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        binding.dotsIndicator.setViewPager2(binding.viewPager)

        binding.buttonStartIntro.setOnClickListener {
            position = binding.viewPager.currentItem
            if (position < intro.size) {
                position++
                binding.viewPager.currentItem = position
            }

            if (position == intro.size) {
                sharePrefs.isIntro=false
                startActivity(Intent(this, PermissionAct::class.java))
                finish()
            }
        }
        binding.buttonBackIntro.setOnClickListener {
            position = binding.viewPager.currentItem
            if (position < intro.size) {
                position--
                binding.viewPager.currentItem = position
            }

        }
    }

    override fun initAction() {

    }
}