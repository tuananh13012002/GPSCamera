package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.DialogConfirmDeleteBinding

class DialogConfirmDelete(context: Context) : Dialog(context) {
    private lateinit var binding: DialogConfirmDeleteBinding
    private var listenerDelete: ListenerDelete? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DialogConfirmDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAction()
        binding.txtTitle.isSelected = true
    }

    fun setListener(listenerDelete: ListenerDelete) {
        this.listenerDelete = listenerDelete
    }

    fun setType(type:String){
        binding.txtContentReminder.text = context.getString(R.string.do_you_want_to_delete_this_photo,type)
    }

    private fun initAction() {
        binding.btnConfirm.setOnClickListener {
            listenerDelete?.confirm()
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}