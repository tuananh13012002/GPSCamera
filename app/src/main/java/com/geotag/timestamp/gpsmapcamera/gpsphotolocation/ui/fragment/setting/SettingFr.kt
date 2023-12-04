package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.setting

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.base.AbsFragment
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.databinding.FragmentSettingBinding
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.setting.dialog_rate.DialogRating
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.multi_lang.MultiLangAct
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.stamp.StampOptionAct
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import javax.inject.Inject

class SettingFr @Inject constructor() : AbsFragment<FragmentSettingBinding>() {

    private var manager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_setting
    }

    override fun initAction() {
        binding.imgBack.setOnClickListener {
        }
        binding.langSetting.setOnClickListener {
            startMultiLanguage()
        }
        binding.stampSetting.setOnClickListener {
            startActivity(StampOptionAct.getIntent(requireContext()))
        }

        binding.rateSetting.setOnClickListener {
            showDialogRate()
        }
        binding.privacySetting.setOnClickListener {
            startPolicy()
        }
    }

    override fun initView() {
        binding.rateSetting.isVisible=!sharePrefs.isRate
    }

    private fun startMultiLanguage() {
        startActivity(MultiLangAct.getIntent(requireContext(), 2))
    }

    private fun startPolicy() {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://shiptonstudio.netlify.app/policy"))
        startActivity(intent)
    }

    private fun showDialogRate() {
        val ratingDialog = DialogRating(requireContext())
        ratingDialog.init(
            requireContext(),
            object : DialogRating.OnPress {
                override fun sendThank() {
                    Toast.makeText(requireContext(), getString(R.string.thank_you), Toast.LENGTH_SHORT).show()
                    ratingDialog.dismiss()
                    requireActivity().window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }

                override fun rating() {
                    manager = ReviewManagerFactory.create(requireContext())
                    val request: Task<ReviewInfo> = manager!!.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            reviewInfo = task.result
                            val flow: Task<Void> =
                                manager!!.launchReviewFlow(activity!!, reviewInfo!!)
                            flow.addOnSuccessListener {
                                ratingDialog.dismiss()
                                requireActivity().window.decorView.systemUiVisibility =
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                            }
                        } else {
                            ratingDialog.dismiss()
                            requireActivity().window.decorView.systemUiVisibility =
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        }
                        binding.rateSetting.isVisible = !sharePrefs.isRate
                    }
                }

                override fun cancel() {}
                override fun later() {
                    ratingDialog.dismiss()
                    requireActivity().window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }
            }
        )
        try {
            ratingDialog.show()
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
    }
}