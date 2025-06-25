package com.yjsoft.led.util

import android.content.Context
import com.yjsoft.core.utils.YJUtils
import com.yjsoft.led.R

object SettingsUtils {
    private const val PREFS_NAME = "settings"
    private const val KEY_START_MESSAGE = "start_message"
    private const val KEY_SCREEN_WIDTH = "screen_width"
    private const val KEY_SCREEN_HEIGHT = "screen_height"

    fun getStartMessage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_START_MESSAGE, context.getString(R.string.start_message))
            ?: context.getString(R.string.start_message)
    }

    fun setStartMessage(context: Context, message: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_START_MESSAGE, message).apply()
    }

    fun getScreenWidth(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_SCREEN_WIDTH, 64)
    }

    fun getScreenHeight(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_SCREEN_HEIGHT, 64)
    }

    fun setScreenSize(context: Context, width: Int, height: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_SCREEN_WIDTH, width)
            .putInt(KEY_SCREEN_HEIGHT, height)
            .apply()
    }

    fun applyScreenSize(context: Context) {
        YJUtils.setScreenWidth(getScreenWidth(context))
        YJUtils.setScreenHeight(getScreenHeight(context))
    }
}
