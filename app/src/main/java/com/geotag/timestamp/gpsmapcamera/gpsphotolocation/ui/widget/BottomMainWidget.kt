package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.WidgetBottomMainBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beGone
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beVisible

class BottomMainWidget(context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs)
{
    private val binding: WidgetBottomMainBinding
    private lateinit var onEventBottomMain: IOnEventBottomMain
    init {
        binding = WidgetBottomMainBinding.inflate(LayoutInflater.from(context), this, true)
        initEvents()
    }

    fun setEvent(onEventBottomMain: IOnEventBottomMain){
        this.onEventBottomMain = onEventBottomMain
    }

    private fun initEvents() {
        homeActive()
        binding.btnHome.setOnClickListener {
            onItemClick(0)
        }
        binding.btnMap.setOnClickListener {
            onItemClick(1)
        }
        binding.btnCompass.setOnClickListener {
            onItemClick(2)
        }
        binding.btnCamera.setOnClickListener {
            onItemClick(3)
        }
    }

    private fun onItemClick(pos: Int){
        when(pos){
            0 -> {
                homeActive()
            }
            1 -> {
                mapActive()
            }
            2 -> {
                compassActive()
            }
            3 -> {
                cameraActive()
            }
        }
        onEventBottomMain.onClickItem(pos)
    }

    private fun cameraActive() {

    }

    private fun compassActive() {
        binding.imgHome.setImageResource(R.drawable.ic_home)
        binding.dotHome.beGone()
        binding.imgMap.setImageResource(R.drawable.ic_map)
        binding.dotMap.beGone()
        binding.imgCompass.setImageResource(R.drawable.ic_compass_selected)
        binding.dotCompass.beVisible()
    }

    private fun mapActive() {
        binding.imgHome.setImageResource(R.drawable.ic_home)
        binding.dotHome.beGone()
        binding.imgMap.setImageResource(R.drawable.ic_map_selected)
        binding.dotMap.beVisible()
        binding.imgCompass.setImageResource(R.drawable.ic_compass)
        binding.dotCompass.beGone()
    }

    private fun homeActive() {
        binding.imgHome.setImageResource(R.drawable.ic_home_selected)
        binding.dotHome.beVisible()
        binding.imgMap.setImageResource(R.drawable.ic_map)
        binding.dotMap.beGone()
        binding.imgCompass.setImageResource(R.drawable.ic_compass)
        binding.dotCompass.beGone()
    }

    interface IOnEventBottomMain{
        fun onClickItem(pos: Int)
    }
}