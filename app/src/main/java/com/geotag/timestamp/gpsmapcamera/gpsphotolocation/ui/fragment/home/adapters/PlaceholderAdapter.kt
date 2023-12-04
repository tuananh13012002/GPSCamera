package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemPlaceHolderBinding

class PlaceholderAdapter: Adapter<PlaceholderAdapter.PlaceholderViewHolder>() {
    private val mListPlaceholder = ArrayList<Int>()
    private lateinit var onEventPlaceholderListener: IOnClickPlaceholderListener
    fun applyData(mListPlaceholder: Array<Int>){
        this.mListPlaceholder.clear()
        this.mListPlaceholder.addAll(mListPlaceholder)
        notifyDataSetChanged()
    }

    fun applyEvent(onEventPlaceholderListener: IOnClickPlaceholderListener){
        this.onEventPlaceholderListener = onEventPlaceholderListener
    }

    class PlaceholderViewHolder(private val binding: ItemPlaceHolderBinding): ViewHolder(binding.root){
        fun binds(resId: Int, onClickItem: (resId: Int)-> Unit){
            Glide.with(binding.imgPlaceHolder.context).load(resId).into(binding.imgPlaceHolder)
            binding.root.setOnClickListener {
                onClickItem(resId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceholderViewHolder {
        return PlaceholderViewHolder(ItemPlaceHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = mListPlaceholder.size

    override fun onBindViewHolder(holder: PlaceholderViewHolder, position: Int) {
        holder.binds(mListPlaceholder[position]){
            onEventPlaceholderListener.onClick(it)
        }
    }

    interface IOnClickPlaceholderListener{
        fun onClick(resId: Int)
    }
}