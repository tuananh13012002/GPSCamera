package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.multi_lang.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.models.LanguageEntity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ItemMultiLangBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil

class MultiLangAdapter(
    private var list: MutableList<LanguageEntity>,
    private val context: Context,
    var selectedPosition: Int,
    val callBack: (Int, LanguageEntity) -> Unit
) : RecyclerView.Adapter<MultiLangAdapter.LanguageViewHolder>() {

    companion object {
        val dummyData = mutableListOf(
            LanguageEntity(name = "English", code = "en", avatar = R.drawable.color_united_kingdom),
            LanguageEntity(name = "French", code = "fr", avatar = R.drawable.color_france),
            LanguageEntity(name = "German", code = "de", avatar = R.drawable.color_germany),
            LanguageEntity(name = "Hindi", code = "hi", avatar = R.drawable.color_india),
            LanguageEntity(name = "Spanish", code = "es", avatar = R.drawable.color_spain),
            LanguageEntity(name = "Vietnamese", code = "vi", avatar = R.drawable.color_vietnam),
        )
    }

    inner class LanguageViewHolder(val binding: ItemMultiLangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForColorStateLists")
        fun bind(item: LanguageEntity, isSelected: Boolean) {
            binding.apply {
                if (isSelected) {
                    SystemUtil.setPreLanguage(context, item.code)
                    SystemUtil.setLocale(context)
                    itemLangImgCheck.setImageResource(R.drawable.checked)
                    itemLang.setBackgroundResource(R.drawable.bg_item_language_selected)
                } else {
                    itemLangImgCheck.setImageResource(R.drawable.not_checked)
                    itemLang.setBackgroundResource(R.drawable.bg_item_language_normal)
                }
                itemLangToTransTvName.text = item.name
                item.avatar?.let { itemLangToTransImgAva.setImageResource(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        return LanguageViewHolder(ItemMultiLangBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position == selectedPosition)
        holder.binding.root.setOnClickListener {
            list[selectedPosition].isSelected = false
            selectedPosition = position
            list[selectedPosition].isSelected = true
            notifyDataSetChanged()
            callBack(position, item)
            savePositionLang(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun savePositionLang(position: Int) {
        val pref = context.getSharedPreferences(
            "myPref", AppCompatActivity.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putInt("positionLang", position)
        editor.commit()
    }

}