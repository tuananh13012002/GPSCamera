package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.sharePrefs.SharePrefs
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.di.ViewModelFactory
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.SystemUtil
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class AbsActivity<DB : ViewDataBinding> : DaggerAppCompatActivity() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var binding: DB

    protected lateinit var sharePrefs: SharePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtil.setPreLanguage(this, SystemUtil.getPreLanguage(this))
        SystemUtil.setLocale(this)
        binding = DataBindingUtil.setContentView(this, getContentView())
        bindViewModel()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        sharePrefs = SharePrefs(this)
        initView()
        initAction()
    }

    abstract fun initView()

    abstract fun initAction()

    abstract fun getContentView(): Int
    abstract fun bindViewModel()
    override fun onStop() {
        super.onStop()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}