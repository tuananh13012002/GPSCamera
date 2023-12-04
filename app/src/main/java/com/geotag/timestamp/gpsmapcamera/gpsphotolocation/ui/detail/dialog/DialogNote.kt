package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.detail.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.DialogAddNoteBinding

class DialogNote(context: Context) : Dialog(context) {
    private lateinit var binding: DialogAddNoteBinding
    private var listenerAddNote: ListenerAddNote?=null

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString().isNotEmpty()){
                binding.btnConfirm.setCardBackgroundColor(Color.parseColor("#F1B15E"))
                binding.txtConfirm.setTextColor(Color.parseColor("#F6F7F8"))
            } else{
                binding.btnConfirm.setCardBackgroundColor(Color.parseColor("#C5C9D3"))
                binding.txtConfirm.setTextColor(Color.parseColor("#E2E4E9"))
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DialogAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAction()
        binding.edNote.addTextChangedListener(textWatcher)
        binding.txtTitle.isSelected = true
    }

    fun setContent(note:String){
        binding.edNote.setText(note)
    }


    fun setListener(listenerAddNote: ListenerAddNote){
        this.listenerAddNote=listenerAddNote
    }


    private fun initAction() {
        binding.btnConfirm.setOnClickListener {
            listenerAddNote?.confirm(binding.edNote.text.toString())
            dismiss()
            binding.edNote.text.clear()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
            binding.edNote.text.clear()
        }
    }
}