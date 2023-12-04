package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.extension

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.R

fun View.beGone(){
    this.visibility = View.GONE
}

fun View.beVisible(){
    this.visibility = View.VISIBLE
}

fun View.beInvisible(){
    this.visibility = View.INVISIBLE
}

fun ViewGroup.moveAheadListAnim(context : Context){
    this.beVisible()
    val animationTopic = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_move_ahead)
    this.layoutAnimation = animationTopic
    this.layoutAnimationListener = null
    this.startLayoutAnimation()
}
fun ViewGroup.slideUpAnimation(context : Context){
    this.beVisible()
    val animationTopic = AnimationUtils.loadAnimation(context, R.anim.bottom_sheet_slide)
    startAnimation(animationTopic)
}
fun ViewGroup.slideDownAnimation(context : Context){
    val animationTopic = AnimationUtils.loadAnimation(context, R.anim.bottom_sheet_slide_down)
    animationTopic.setAnimationListener(object : Animation.AnimationListener{
        override fun onAnimationStart(p0: Animation?) { }
        override fun onAnimationEnd(p0: Animation?) { beGone() }
        override fun onAnimationRepeat(p0: Animation?) { }
    })
    startAnimation(animationTopic)
}

fun ViewGroup.dropdownListAnim(context : Context){
    val animationTopic = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
    this.layoutAnimation = animationTopic
    this.layoutAnimationListener = object : Animation.AnimationListener{
        override fun onAnimationStart(p0: Animation?) {}
        override fun onAnimationEnd(p0: Animation?) { beGone() }
        override fun onAnimationRepeat(p0: Animation?) {}
    }
    this.startLayoutAnimation()
}

fun TextView.setColorResource(resId: Int){
    setTextColor(ContextCompat.getColor(context, resId))
}
private var lastClickTime: Long = 0
private const val clickInterval: Long = 1000
fun View.setOnSingleClickListener(listener: (View) -> Unit) {
    this.setOnClickListener {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= clickInterval) {
            lastClickTime = currentTime
            listener(it)
        }
    }
}

fun View.onClickListener(onClick:(View) -> Unit){
    setOnClickListener {
        if (it.isEnabled){
            it.isEnabled = false
            onClick(this)
            it.postDelayed({it.isEnabled = true}, 500)
        }
    }
}