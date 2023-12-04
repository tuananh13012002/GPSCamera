package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.fragment

import androidx.fragment.app.viewModels
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.FragmentPhotoBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beGone
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.toast
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.DetailActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.MediaViewModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.adapters.DetailAdapterV2

class PhotoFr : AbsFragment<FragmentPhotoBinding>() , DetailAdapterV2.OnListener {

    private val viewModel by viewModels<MediaViewModel> ()

    override fun getLayoutRes() = R.layout.fragment_photo

    override fun initAction() {
    }

    override fun initView() {
        initRcv()
    }

    private fun initRcv() {
        viewModel.getMedias.observe(viewLifecycleOwner){
            it?.let { list->
                binding.root.postDelayed({
                     binding.progress.root.beGone()
                },3000)
                val detailAdapter = DetailAdapterV2(requireContext(), list.filter { it.duration==null }.toMutableList(),this,1)
                binding.rvDetail.adapter = detailAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initRcv()
    }

    override fun onClickMedia(mediaModel: MediaModel?) {
        mediaModel.takeIf { it!=null }?.let {
            DetailActivity.startActivity(requireActivity(), it)
        }?: kotlin.run {
            requireContext().toast(R.string.sample_file)
        }
    }
}
