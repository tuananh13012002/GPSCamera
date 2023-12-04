package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemHomeChildBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemHomeMoreBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beGone
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beVisible
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants
import jp.wasabeef.blurry.Blurry
import java.text.SimpleDateFormat
import java.util.Locale

class HomeChildAdapter: Adapter<ViewHolder>(){
    private val mediaModels = ArrayList<MediaModel>()
    private lateinit var onEventHomeChild: IOnEVentHomeChild

    fun applyEvent(onEventHomeChild: IOnEVentHomeChild){
        this.onEventHomeChild = onEventHomeChild
    }

    fun applyData(mediaModels: ArrayList<MediaModel>){
        this.mediaModels.clear()
        this.mediaModels.addAll(mediaModels)
        notifyDataSetChanged()
    }

    class HomeChildViewHolder(private val binding: ItemHomeChildBinding): ViewHolder(binding.root){
        fun binds(mediaModel: MediaModel, onClickItem: ()-> Unit){
            Glide.with(binding.imgHomeChild.context).load(mediaModel.data).into(binding.imgHomeChild)
            if (mediaModel.duration != null){
                binding.tvDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaModel.duration)
                binding.tvDuration.beVisible()
            }else{
                binding.tvDuration.beGone()
            }
            binding.viewContainerItem.setOnClickListener {
                onClickItem()
            }
        }
    }

    class ItemMoreViewHolder(private val binding: ItemHomeMoreBinding): ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun binds(mediaModel: MediaModel, mediaModels: ArrayList<MediaModel>, onClickItem: ()-> Unit){
            binding.viewContainerItem.setOnClickListener {
                onClickItem()
            }
            Glide.with(binding.imgHomeChild.context).asBitmap().load(mediaModel.data).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    Blurry.with(binding.imgHomeChild.context)
                        .radius(8)
                        .sampling(8)
                        .from(resource)
                        .into(binding.imgHomeChild)
                }
                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
            binding.tvSize.text = "+${mediaModels.size - 2}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == Constants.TYPE_NORMAL)
            HomeChildViewHolder(ItemHomeChildBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else
            ItemMoreViewHolder(ItemHomeMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = if (mediaModels.size > 6) 6 else mediaModels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaModel = mediaModels[position]
        if (holder is HomeChildViewHolder){
            holder.binds(mediaModel){onEventHomeChild.onClickNormal(mediaModel)}
        }
        if (holder is ItemMoreViewHolder){
            holder.binds(mediaModel, mediaModels){onEventHomeChild.onClickMore(mediaModel)}
        }
    }

    override fun getItemViewType(position: Int): Int = if (position == 5) Constants.TYPE_MORE else Constants.TYPE_NORMAL

    interface IOnEVentHomeChild{
        fun onClickNormal(mediaModel: MediaModel)
        fun onClickMore(mediaModel: MediaModel)
    }
}