package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.multi_lang

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.ActivityMultilangBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.intro.IntroAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.main.MainActivity
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.multi_lang.adapter.MultiLangAdapter
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil
import javax.inject.Inject

class MultiLangAct @Inject constructor() : AbsActivity<ActivityMultilangBinding>() {
    private var type: Int? = null

    override fun initView() {
        binding.rvLangs.adapter = MultiLangAdapter(MultiLangAdapter.dummyData, this, getPosition()) { position, item ->
                SystemUtil.setPreLanguage(this, item.code)
                SystemUtil.setLocale(this)
            }
    }

    override fun initAction() {
        type = intent.getIntExtra(TYPE_LANG, 0)
        when (type) {
            1 -> {
                binding.typeLang1.visibility = View.VISIBLE
                binding.typeLang2.visibility = View.GONE
                binding.tvTitile.gravity = Gravity.LEFT
                if(!sharePrefs.isMultiLang){
                    startActivity(Intent(this, IntroAct::class.java))
                    finish()
                }
                binding.btnChooseLang.setOnClickListener {
                    sharePrefs.isMultiLang = false
                    startActivity(Intent(this, IntroAct::class.java))
                    finish()
                }
            }

            2 -> {
                binding.typeLang1.visibility = View.GONE
                binding.typeLang2.visibility = View.VISIBLE
                binding.tvTitile.gravity = Gravity.CENTER
                binding.imgBack.setOnClickListener {
                    onBackPressed()
                }
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_multilang
    }

    override fun bindViewModel() {
    }

    companion object {
        private const val TYPE_LANG = "MultiLangAct_Lang"
        fun getIntent(context: Context, type: Int): Intent {
            val intent = Intent(context, MultiLangAct::class.java)
            intent.putExtra(TYPE_LANG, type)
            return intent
        }
    }

    private fun getPosition(): Int {
        val pref = baseContext.getSharedPreferences("myPref", MODE_PRIVATE)
        return pref.getInt("positionLang", 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("back_setting", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}