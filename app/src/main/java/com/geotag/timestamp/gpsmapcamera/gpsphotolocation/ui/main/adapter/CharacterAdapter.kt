package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.ResultEntity


class CharacterAdapter(
) : PagingDataAdapter<ResultEntity, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<ResultEntity>() {
    override fun areItemsTheSame(oldItem: ResultEntity, newItem: ResultEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ResultEntity, newItem: ResultEntity): Boolean {
        return oldItem.id == newItem.id
    }
}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CharacterViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as CharacterViewHolder).bind(it) }
    }
}



