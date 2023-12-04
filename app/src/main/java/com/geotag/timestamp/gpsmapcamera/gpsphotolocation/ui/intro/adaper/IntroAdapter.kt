package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.intro.adaper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.IntroEntity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemIntroBinding

class IntroAdapter(private val items: List<IntroEntity>) :
    RecyclerView.Adapter<IntroAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = ItemIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class CarouselViewHolder(val binding: ItemIntroBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: IntroEntity) {
            binding.imageViewCarouselItem.setImageResource(model.icon ?: 0)
            binding.titleImageIntro.text = model.title ?: ""
            binding.contentImgIntro.text = model.content ?: ""
        }
    }
}