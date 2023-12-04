package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.style.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemStyleBinding

class StyleAdapter(private val context: Context, private val type: Int) :
    RecyclerView.Adapter<StyleAdapter.StyleViewHolder>() {
    private var selectedItem = 0
    private lateinit var onEventStyleListener: IOnEventStyleListener

    private val itemFonts: MutableList<Pair<String, Int>> = mutableListOf(
        Pair("Manrope", R.font.manrope_medium),
        Pair("Mako", R.font.mako),
        Pair("Marck Script", R.font.marckscript),
        Pair("Modak", R.font.modak),
        Pair("Andana Pro", R.font.andana),
    )
    private val itemsText: MutableList<Pair<Int, String>> = mutableListOf(
        Pair(R.color.text_color_0, "White"),
        Pair(R.color.text_color_1, "Black"),
        Pair(R.color.text_color_2, "Red"),
        Pair(R.color.text_color_3, "Blue"),
        Pair(R.color.text_color_4, "Green"),
    )

    fun setPosSelected(pos: Int){
        this.selectedItem = pos
        notifyDataSetChanged()
    }

    fun applyEvent(onEventStyleListener: IOnEventStyleListener){
        this.onEventStyleListener = onEventStyleListener
    }

    inner class StyleViewHolder(val binding: ItemStyleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindFont(
            itemFonts: Pair<String, Int>,
            position: Int,
            onItemSelected:(itemFonts: Pair<String, Int>, pos: Int)->Unit
        ) {
            if (selectedItem == position) binding.itemLangImgCheck.setImageResource(R.drawable.checked)
            else binding.itemLangImgCheck.setImageResource(R.drawable.not_checked)
            binding.itemLangToTransTvName.text = itemFonts.first
            binding.itemLangToTransImgAva.visibility = View.GONE
            val params = binding.itemLangToTransTvName.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = 3
            binding.itemLangToTransTvName.layoutParams = params
            binding.itemLangToTransTvName.typeface = ResourcesCompat.getFont(context, itemFonts.second)
            binding.root.setOnClickListener {
                selectedItem = position
                onItemSelected(itemFonts, position)
                notifyDataSetChanged()
            }
        }

        fun bindTextColor(
            itemsText: Pair<Int, String>,
            position: Int,
            onItemSelected:(itemsText: Pair<Int, String>, pos: Int)->Unit
        ) {
            if (selectedItem == position) binding.itemLangImgCheck.setImageResource(R.drawable.checked)
            else binding.itemLangImgCheck.setImageResource(R.drawable.not_checked)
            binding.itemLangToTransTvName.text = itemsText.second
            binding.itemLangToTransImgAva.visibility = View.VISIBLE
            binding.itemLangToTransImgAva.backgroundTintList = ContextCompat.getColorStateList(context, itemsText.first)
            binding.root.setOnClickListener {
                selectedItem = position
                onItemSelected(itemsText, position)
                notifyDataSetChanged()
            }
//            val sharePrefs = SharePrefs(binding.root.context)
//            if(sharePrefs.fontCurrent > 0){
//                binding.itemLangToTransTvName.apply {
//                    typeface = ResourcesCompat.getFont(binding.itemLangToTransTvName.context, sharePrefs.fontCurrent)
//                }
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder {
        val binding = ItemStyleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StyleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (type == 1) {
            itemFonts.size
        } else {
            itemsText.size
        }
    }

    override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
        if (type == 1)
            holder.bindFont(itemFonts[position], position){ item, pos ->
                onEventStyleListener.onClickFont(itemFonts[position], position)
            }
        else
            holder.bindTextColor(itemsText[position], position){item, pos ->
                onEventStyleListener.onClickText(itemsText[position], position)
            }
    }

    interface IOnEventStyleListener{
        fun onClickFont(itemFonts: Pair<String, Int>, position: Int)
        fun onClickText(itemsText: Pair<Int, String>, position: Int)

    }
}