package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.bottomsheet

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.BottomSheetInfoBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beGone
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beVisible
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DateFormat
import java.util.Locale

class BottomSheetInfo: BottomSheetDialogFragment(){
    private lateinit var binding: BottomSheetInfoBinding

    companion object{
        fun newInstance(mediaModel: MediaModel?) = BottomSheetInfo().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.EXTRA_MEDIA_DATA_TO_BOTTOM_SHEET, mediaModel)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_info, container, false)
        if (arguments != null){
            val mediaModel = requireArguments().getParcelable<MediaModel>(Constants.EXTRA_MEDIA_DATA_TO_BOTTOM_SHEET)
            binding.tvName.text = mediaModel?.name
            binding.tvDate.text = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault()).format(mediaModel?.dateTaken)
            binding.tvPath.text = mediaModel?.data
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val bitmap = BitmapFactory.decodeFile(mediaModel?.data, options)
            if (bitmap != null){
                binding.tvResolution.text = "${bitmap.width}x${bitmap.height}"
                binding.viewResolution.beVisible()
            }else{
                binding.viewResolution.beGone()
            }
        }
        initEvent()
        return binding.root
    }

    private fun initEvent() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}