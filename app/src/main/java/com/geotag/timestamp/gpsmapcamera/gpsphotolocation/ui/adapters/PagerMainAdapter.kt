package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.compass.CompassFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.HomeFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.map.MapFragment

class PagerMainAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val frags = arrayListOf(
        HomeFragment(),
        MapFragment(),
        CompassFragment()
    )

    override fun getItemCount(): Int = frags.size

    override fun createFragment(position: Int): Fragment = frags[position] as Fragment

}