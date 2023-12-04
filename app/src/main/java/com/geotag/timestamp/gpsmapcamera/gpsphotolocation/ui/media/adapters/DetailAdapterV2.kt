package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.media.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemDefaultBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemHomeChildBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beGone
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beVisible
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.toast
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants.TYPE_IMAGE
import java.text.SimpleDateFormat
import java.util.Locale

class DetailAdapterV2 (
    private val context: Context,
    private var list: MutableList<MediaModel>,
    private val onListener: OnListener,
    private val type:Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val hasData = list.isNotEmpty()

    companion object {
        const val VIEW_TYPE_REAL = 1
        const val VIEW_TYPE_FAKE = 2
    }

    inner class DetailViewHolder(val binding: ItemHomeChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(mediaModel: MediaModel, position: Int){
                Glide.with(binding.imgHomeChild.context).load(mediaModel.data).into(binding.imgHomeChild)
                if (mediaModel.duration != null){
                    binding.tvDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaModel.duration)
                    binding.tvDuration.beVisible()
                }else{
                    binding.tvDuration.beGone()
                }
                binding.root.setOnClickListener {
                   onListener.onClickMedia(mediaModel)
                }
            }
    }
    inner class DefaultViewHolder(val binding: ItemDefaultBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_REAL -> {
                val binding = ItemHomeChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DetailViewHolder(binding)
            }
            VIEW_TYPE_FAKE -> {
                val binding = ItemDefaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DefaultViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_REAL -> {
                val realViewHolder = holder as DetailViewHolder
                val media = list[position]
                realViewHolder.bind(media,position)
            }
            VIEW_TYPE_FAKE -> {
                val defaultViewHolder = holder as DefaultViewHolder
                if(type==TYPE_IMAGE){
                    defaultViewHolder.binding.tvDuration.beGone()
                }else{
                    defaultViewHolder.binding.tvDuration.beVisible()
                }
                defaultViewHolder.binding.root.setOnClickListener {
                   context.toast(R.string.sample_file)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasData) {
            VIEW_TYPE_REAL
        } else {
            VIEW_TYPE_FAKE
        }
    }

    override fun getItemCount(): Int {
        return if(!hasData) 1 else list.size
    }

    interface OnListener{
        fun onClickMedia(mediaModel: MediaModel?)
    }
}