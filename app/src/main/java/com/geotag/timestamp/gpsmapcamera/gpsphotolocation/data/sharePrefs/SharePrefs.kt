package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.data.sharePrefs

import android.content.Context
import android.content.SharedPreferences
import com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils.Constants
import javax.inject.Inject

class SharePrefs @Inject constructor(private val context: Context) {
    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    var isFirstOpen: Boolean
        get() = getPref(context).getBoolean(Constants.KEY_FIRST_OPEN, true)
        set(isFirstOpen) = getPref(context).edit().putBoolean(Constants.KEY_FIRST_OPEN, isFirstOpen).apply()

    var isMultiLang: Boolean
        get() = getPref(context).getBoolean(Constants.KEY_FIRST_LANGUAGE, true)
        set(isFirstOpen) = getPref(context).edit().putBoolean(Constants.KEY_FIRST_LANGUAGE, isFirstOpen).apply()

    var isIntro: Boolean
        get() = getPref(context).getBoolean(Constants.KEY_FIRST_INTRO, true)
        set(isFirstOpen) = getPref(context).edit().putBoolean(Constants.KEY_FIRST_INTRO, isFirstOpen).apply()


    var fontCurrent: Int
        get() = getPref(context).getInt(Constants.KEY_FONT_CURRENT, 0)
        set(fontCurrent) = getPref(context).edit().putInt(Constants.KEY_FONT_CURRENT, fontCurrent)
            .apply()

    var posFontCurrent: Int
        get() = getPref(context).getInt(Constants.KEY_POS_FONT_CURRENT, 0)
        set(posFontCurrent) = getPref(context).edit()
            .putInt(Constants.KEY_POS_FONT_CURRENT, posFontCurrent).apply()

    var colorCurrent: Int
        get() = getPref(context).getInt(Constants.KEY_COLOR_CURRENT, 0)
        set(colorCurrent) = getPref(context).edit()
            .putInt(Constants.KEY_COLOR_CURRENT, colorCurrent).apply()
    var posColorCurrent: Int
        get() = getPref(context).getInt(Constants.KEY_POS_COLOR_CURRENT, 0)
        set(posColorCurrent) = getPref(context).edit()
            .putInt(Constants.KEY_POS_COLOR_CURRENT, posColorCurrent).apply()

    var cameraRecent: String?
        get() = getPref(context).getString(Constants.KEY_CAMERA_RECENT, "")
        set(cameraRecent) = getPref(context).edit().putString(Constants.KEY_CAMERA_RECENT, cameraRecent).apply()

    var fileShare: String?
        get() = getPref(context).getString(Constants.KEY_FILE_SHARE, "")
        set(fileShare) = getPref(context).edit().putString(Constants.KEY_FILE_SHARE, fileShare).apply()


    var isShowMap: Boolean
        get() = getPref(context).getBoolean(Constants.KEY_SHOW_MAP, false)
        set(isShowMap) = getPref(context).edit().putBoolean(Constants.KEY_SHOW_MAP, isShowMap).apply()


    var logoSelect: String?
        get() = getPref(context).getString(Constants.KEY_LOGO_SELECT, "")
        set(logoSelect) = getPref(context).edit().putString(Constants.KEY_LOGO_SELECT, logoSelect).apply()

    var isRate: Boolean
        get() = getPref(context).getBoolean(Constants.IS_RATE, false)
        set(isRate) = getPref(context).edit().putBoolean(Constants.IS_RATE, isRate).apply()

}