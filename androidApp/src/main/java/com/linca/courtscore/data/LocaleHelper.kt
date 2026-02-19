package com.linca.courtscore.data

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            "en" -> Locale.ENGLISH
            "es" -> Locale("es")
            "ca" -> Locale("ca")
            else -> Locale.getDefault()
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun getLocaleFromLanguageCode(languageCode: String): Locale {
        return when (languageCode) {
            "en" -> Locale.ENGLISH
            "es" -> Locale("es")
            "ca" -> Locale("ca")
            else -> Locale.getDefault()
        }
    }
}

