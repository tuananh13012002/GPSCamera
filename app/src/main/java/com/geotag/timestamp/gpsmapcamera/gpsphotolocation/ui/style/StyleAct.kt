package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.style

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityStyleBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beGone
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.beVisible
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension.onClickListener
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.style.adapter.StyleAdapter
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class StyleAct @Inject constructor() : AbsActivity<ActivityStyleBinding>(),
    StyleAdapter.IOnEventStyleListener {
    private var posColorCurrent = 0
    private var posFontCurrent = 0
    private var colorCurrent = 0
    private var fontCurrent = 0

    private val styleFontAdapter by lazy {
        StyleAdapter(this, 1)
    }
    private val styleTextAdapter by lazy {
        StyleAdapter(this, 2)
    }

    override fun initView() {
        posColorCurrent = sharePrefs.posColorCurrent
        posFontCurrent = sharePrefs.posFontCurrent
        colorCurrent = sharePrefs.colorCurrent
        fontCurrent = sharePrefs.fontCurrent
        val type = intent.getIntExtra(KEY_TYPE, 0)
        when (type) {
            1 -> {
                binding.titleStyle.text=getString(R.string.title_font_style)
                binding.rv.adapter = styleFontAdapter
                styleFontAdapter.setPosSelected(sharePrefs.posFontCurrent)
                styleFontAdapter.applyEvent(this)
            }
            2 -> {
                binding.titleStyle.text=getString(R.string.title_text_color)
                binding.rv.adapter = styleTextAdapter
                styleTextAdapter.setPosSelected(sharePrefs.posColorCurrent)
                styleTextAdapter.applyEvent(this)
            }
            3->{
                binding.titleStyle.text=getString(R.string.logo_option)
                binding.rv.beGone()
                binding.layoutLogo.beVisible()
                binding.ivSelect.beGone()
                sharePrefs.logoSelect.takeIf { it?.isNotEmpty()==true }?.let {
                    Glide.with(this)
                        .load(it)
                        .centerCrop()
                        .into(binding.imgLogo)
                }
                binding.btnEditLogo.onClickListener {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    startResult.launch(intent)
                }
            }
        }
    }

    private val startResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val selectedImageUri = data.data
                    val documentFile: DocumentFile? = selectedImageUri?.let { DocumentFile.fromSingleUri(this, it) }
                    val fileAvt = documentFile?.let { convertDocumentFileToFile(this, it) }
                    fileAvt.takeIf { it!=null }?.let { file->
                        sharePrefs.logoSelect=file.path
                    }
                    Glide.with(this)
                        .load(selectedImageUri)
                        .centerCrop()
                        .circleCrop()
                        .into(binding.imgLogo)
                }
            }
        }

    private fun convertDocumentFileToFile(context: Context, documentFile: DocumentFile): File? {
        val inputStream = context.contentResolver.openInputStream(documentFile.uri)

        if (inputStream != null) {
            val outputFile = File(context.cacheDir, documentFile.name ?: "converted_file")
            val outputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(4 * 1024) // 4K buffer size
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            return outputFile
        }

        return null
    }

    override fun initAction() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivSelect.setOnClickListener {
            sharePrefs.colorCurrent = colorCurrent
            sharePrefs.fontCurrent = fontCurrent
            sharePrefs.posColorCurrent = posColorCurrent
            sharePrefs.posFontCurrent = posFontCurrent
            it.postDelayed({finish()}, 300)
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_style
    }

    override fun bindViewModel() {

    }

    companion object {
        const val KEY_TYPE = "StyleAct_Type"
        fun getIntent(context: Context, type: Int): Intent {
            val intent = Intent(context, StyleAct::class.java)
            intent.putExtra(KEY_TYPE, type)
            return intent
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClickFont(itemFonts: Pair<String, Int>, position: Int) {
        posFontCurrent = position
        fontCurrent = itemFonts.second
    }

    override fun onClickText(itemsText: Pair<Int, String>, position: Int) {
        posColorCurrent = position
        colorCurrent = itemsText.first
    }
}