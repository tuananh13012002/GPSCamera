package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.camera.view.PreviewView

class CustomPreviewView : FrameLayout {
    private val textPaint = Paint()
    private var overlayText: String = ""
    private val previewView: PreviewView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        // Create a PreviewView
        previewView = PreviewView(context)
        addView(previewView)

        initialize()
    }

    private fun initialize() {
        textPaint.color = Color.WHITE
        textPaint.textSize = 50f
        textPaint.isAntiAlias = true
    }

    fun setOverlayText(text: String) {
        this.overlayText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Vẽ văn bản lên CustomPreviewViewContainer
        canvas.drawText(overlayText, 50f, 50f, textPaint)
    }
}