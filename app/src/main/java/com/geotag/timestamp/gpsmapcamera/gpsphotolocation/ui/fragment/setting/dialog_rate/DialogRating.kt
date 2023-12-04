package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.ui.fragment.setting.dialog_rate

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.sharePrefs.SharePrefs
import com.willy.ratingbar.ScaleRatingBar


@SuppressLint("MissingInflatedId")
class DialogRating(private val context: Context) : Dialog(
    context, R.style.CustomAlertDialog
) {
    private var onPress: OnPress? = null
    private val tvTitle: TextView
    private val tvContent: TextView
    private val ratingBar: ScaleRatingBar
    private val imgIcon: ImageView
    private val editFeedback: EditText
    private val sharedPreference: SharedPreferences? = null
    private val editor: SharedPreferences.Editor? = null
    private val KEY_CHECK_OPEN_APP = "KEY CHECK OPEN APP"
    private val btnRate: Button
    private val Send: Button? = null
    private val Cancel: Button? = null
    private val btnLater: Button

    private var sharePrefs: SharePrefs?=null

    init {
        setContentView(R.layout.dialog_rating)
        val attributes = window!!.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = attributes
        window!!.setSoftInputMode(16)
        tvTitle = findViewById<View>(R.id.tvTitle) as TextView
        tvContent = findViewById<View>(R.id.tvContent) as TextView
        ratingBar = findViewById<View>(R.id.ratingBar) as ScaleRatingBar
        imgIcon = findViewById<View>(R.id.imgIcon) as ImageView
        editFeedback = findViewById<View>(R.id.editFeedback) as EditText
        btnRate = findViewById<View>(R.id.btnRate) as Button
        btnLater = findViewById<View>(R.id.btnLater) as Button

        onclick()
        changeRating()

        setCancelable(false)

        editFeedback.visibility = View.GONE
        btnRate.text = context.getString(R.string.text_rate)
        btnLater.text=context.getString(R.string.text_exit)
        imgIcon.setImageResource(R.drawable.rating_default)
        tvTitle.text = context.getString(R.string.title_dialog_rating_0)
        tvContent.text = context.getString(R.string.content_dialog_rating_0)
        ratingBar.rating == 5f

        sharePrefs= SharePrefs(context)

    }

    interface OnPress {
        fun sendThank()
        fun rating()
        fun cancel()
        fun later()
    }

    fun init(context: Context?, onPress: OnPress?) {
        this.onPress = onPress
    }

    private fun changeRating() {
        ratingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            when (rating.toString()) {
                "1.0" -> {
                    tvTitle.text = context.getString(R.string.title_dialog_rating)
                    tvContent.text = context.getString(R.string.content_dialog_rating)
                    editFeedback.visibility = View.GONE
                    imgIcon.setImageResource(R.drawable.rating_1)
                }

                "2.0" -> {
                    tvTitle.text = context.getString(R.string.title_dialog_rating)
                    tvContent.text = context.getString(R.string.content_dialog_rating)
                    editFeedback.visibility = View.GONE
                    imgIcon.setImageResource(R.drawable.rating_2)
                }

                "3.0" -> {
                    tvTitle.text = context.getString(R.string.title_dialog_rating)
                    tvContent.text = context.getString(R.string.content_dialog_rating)
                    editFeedback.visibility = View.GONE
                    imgIcon.setImageResource(R.drawable.rating_3)
                }

                "4.0" -> {
                    tvTitle.text = context.getString(R.string.title_dialog_rating_4_5)
                    tvContent.text = context.getString(R.string.content_dialog_rating_4_5)
                    editFeedback.visibility = View.GONE
                    imgIcon.setImageResource(R.drawable.rating_4)
                }

                "5.0" -> {
                    tvTitle.text = context.getString(R.string.title_dialog_rating_4_5)
                    tvContent.text = context.getString(R.string.content_dialog_rating_4_5)
                    editFeedback.visibility = View.GONE
                    imgIcon.setImageResource(R.drawable.rating_5)
                }

                else -> {
                    tvTitle.text = context.getString(R.string.title_dialog_rating_0)
                    tvContent.text = context.getString(R.string.content_dialog_rating_0)
                    editFeedback.visibility = View.GONE
                    imgIcon.setImageResource(R.drawable.rating_default)
                }
            }
        }
    }


    val newName: String get() = editFeedback.text.toString()

    private fun onclick() {
        btnRate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (ratingBar.rating == 0f) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.please_feedback),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (ratingBar.rating <= 4.0) {
                    imgIcon.visibility = View.VISIBLE
                    onPress!!.sendThank()
                } else {
                    //Edit
                    //imageView.setVisibility(View.VISIBLE);
                    imgIcon.visibility = View.VISIBLE
                    sharePrefs?.isRate=true
                    onPress!!.rating()
                }
            }
        })
        btnLater.setOnClickListener { onPress!!.later() }
    }
}