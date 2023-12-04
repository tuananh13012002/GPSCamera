package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModelParent
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.FragmentHomeBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.hasPermission
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.toast
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.DetailActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.compass.CompassAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.adapters.HomeParentAdapter
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.map.MapAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.MediaActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.DeviceUtil
import javax.inject.Inject

class HomeFragment @Inject constructor(): AbsFragment<FragmentHomeBinding>(),
    HomeParentAdapter.IOnEventHomeParent {

    private val launchPer = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        var granted = 0
        it.forEach {(k,v) ->
            if (!v){
                granted++
            }
        }
        if (granted == 0){
            initRcv()
        }else{
            requireContext().toast(R.string.permission_denied)
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_home

    private val mediaModelParents = ArrayList<MediaModelParent>()

    private lateinit var viewModel: HomeViewModel

    private lateinit var mAdapter: HomeParentAdapter


    override fun initAction() {
        binding.map.setOnClickListener {
            startActivity(Intent(requireContext(), MapAct::class.java))
        }
        binding.compass.setOnClickListener {
            startActivity(Intent(requireContext(), CompassAct::class.java))
        }
    }

    override fun initView() {
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        binding.homeFragment = this
//        binding.imgNext.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_F6F7F8))
        requestPer()
    }

    private fun requestPer() {
        if (requireContext().hasPermission(DeviceUtil.arrayStoragePermission)){
            initRcv()
        }else{
            launchPer.launch(DeviceUtil.arrayStoragePermission)
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireContext().hasPermission(DeviceUtil.arrayStoragePermission)){
            initRcv()
        }
    }

    private fun initRcv() {
        val mediaPlaceHolder = arrayListOf(
            Pair(resources.getString(R.string.gallery), arrayOf(R.drawable.no_image)),
        )
        mediaModelParents.clear()
        val images = viewModel.getMediaFile(requireContext())
        images.reverse()
        images.sortByDescending { it.dateTaken }
        mediaModelParents.add(MediaModelParent(resources.getString(R.string.gallery), images))
        mAdapter = HomeParentAdapter()
        binding.rvHome.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
        mAdapter.applyDataPlaceholder(mediaPlaceHolder)
        mAdapter.applyData(mediaModelParents)
        mAdapter.applyEvent(this)
    }

    override fun onClickNormalItem(mediaModel: MediaModel) {
        DetailActivity.startActivity(requireActivity(), mediaModel)
    }

    override fun onClickMoreItem(mediaModel: MediaModel) {
        MediaActivity.startActivity(requireActivity(), mediaModel.type)
    }

    override fun onClickViewAll(mediaModelParents: MediaModelParent) {
        MediaActivity.startActivity(requireActivity(),0)
    }

    override fun onClickPlaceholder(resId: Int) {
        requireContext().toast(R.string.sample_file)
    }

}