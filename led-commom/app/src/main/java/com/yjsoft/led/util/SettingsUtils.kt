package com.yjsoft.led.util

import android.content.Context
import com.yjsoft.led.R

object SettingsUtils {
    private const val PREFS_NAME = "settings"
    private const val KEY_START_MESSAGE = "start_message"

    fun getStartMessage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_START_MESSAGE, context.getString(R.string.start_message))
            ?: context.getString(R.string.start_message)
    }

    fun setStartMessage(context: Context, message: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_START_MESSAGE, message).apply()
    }
}
