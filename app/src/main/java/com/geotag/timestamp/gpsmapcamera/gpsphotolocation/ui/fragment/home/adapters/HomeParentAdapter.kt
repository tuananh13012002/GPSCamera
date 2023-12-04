package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModel
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.MediaModelParent
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemHomeParentBinding

class HomeParentAdapter: Adapter<HomeParentAdapter.HomeViewHolder>() {
    private val mediaModelParents = ArrayList<MediaModelParent>()
    private val mediaPlaceholders = ArrayList<Pair<String, Array<Int>>>()
    private lateinit var onEventHomeParent: IOnEventHomeParent

    fun applyData(mediaModelParents: ArrayList<MediaModelParent>){
        this.mediaModelParents.clear()
        this.mediaModelParents.addAll(mediaModelParents)
        notifyDataSetChanged()
    }
    fun applyDataPlaceholder(mediaPlaceholders: ArrayList<Pair<String, Array<Int>>>){
        this.mediaPlaceholders.clear()
        this.mediaPlaceholders.addAll(mediaPlaceholders)
        notifyDataSetChanged()
    }

    fun applyEvent(onEventHomeParent: IOnEventHomeParent){
        this.onEventHomeParent = onEventHomeParent
    }

    class HomeViewHolder(private val binding: ItemHomeParentBinding): ViewHolder(binding.root){
        fun binds(mediaModelParents: MediaModelParent, mediaPlaceholder: Pair<String, Array<Int>>?, onEventHomeParent: IOnEventHomeParent){
            if (mediaModelParents.mediaModels.isNotEmpty()){
                val mAdapter = HomeChildAdapter()
                binding.rvChildHome.apply {
                    adapter = mAdapter
                    layoutManager = GridLayoutManager(context, 3)
                }
                mAdapter.applyData(mediaModelParents.mediaModels)
                mAdapter.applyEvent(object: HomeChildAdapter.IOnEVentHomeChild {
                    override fun onClickNormal(mediaModel: MediaModel) {
                        onEventHomeParent.onClickNormalItem(mediaModel)
                    }

                    override fun onClickMore(mediaModel: MediaModel) {
                        onEventHomeParent.onClickMoreItem(mediaModel)
                    }
                })
                binding.tvTitle.text = mediaModelParents.title
                binding.tvViewAll.setOnClickListener {
                    onEventHomeParent.onClickViewAll(mediaModelParents)
                }
            }else{
                if (mediaPlaceholder != null){
                    val mAdapter = PlaceholderAdapter()
                    mAdapter.applyData(mediaPlaceholder.second)
                    binding.rvChildHome.apply {
                        adapter = mAdapter
                        layoutManager = GridLayoutManager(context, 3)
                    }
                    binding.tvTitle.text = mediaPlaceholder.first
                    binding.tvViewAll.setOnClickListener {
                        onEventHomeParent.onClickViewAll(mediaModelParents)
                    }
                    mAdapter.applyEvent(object : PlaceholderAdapter.IOnClickPlaceholderListener {
                        override fun onClick(resId: Int) {
                            onEventHomeParent.onClickPlaceholder(resId)
                        }
                    })
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(ItemHomeParentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = mediaModelParents.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val mediaModelParent = mediaModelParents[position]
        holder.binds(mediaModelParent, if (mediaPlaceholders.size - 1 >= position)
            mediaPlaceholders[position]
        else
                null, onEventHomeParent)
    }

    interface IOnEventHomeParent{
        fun onClickNormalItem(mediaModel: MediaModel)
        fun onClickMoreItem(mediaModel: MediaModel)
        fun onClickViewAll(mediaModelParents: MediaModelParent)

        fun onClickPlaceholder(resId: Int)
    }
}