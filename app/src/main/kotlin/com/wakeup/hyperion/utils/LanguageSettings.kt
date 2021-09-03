package com.wakeup.hyperion.utils

import android.content.Context
import java.util.*

object LanguageSettings {
    const val ENGLISH = "en"
    const val FRENCH = "fr"

    fun setLocale(context: Context, localeName: String) {
        val locale = Locale(localeName)
        val res = context.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = locale
        res.updateConfiguration(conf, dm)
    }
}