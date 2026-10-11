package com.muse.localplayer.core

import android.content.Context
import android.content.res.Configuration
import org.json.JSONObject
import java.util.Locale

/**
 * 极简本地化：以中文原文为键，按系统语言在 assets/i18n/<语言>.json 中查表。
 *
 * 查不到的条目原样返回中文，所以漏译不会出现空白或乱码。
 * 简体中文是默认语言、不需要词表；同时提供繁体中文、英文、日文、韩文。
 */
object I18n {
    private const val DEFAULT = "zh-CN"

    private val table = HashMap<String, String>(1024)
    private var loaded = ""

    /** 在 Activity 进入组合之前调用，按当前系统语言装载词表。 */
    fun attach(context: Context) {
        val language = resolveLanguage(context)
        if (language == loaded) return
        table.clear()
        loaded = language
        if (language == DEFAULT) return
        runCatching {
            context.assets.open("i18n/$language.json").bufferedReader().use { reader ->
                val json = JSONObject(reader.readText())
                json.keys().forEach { key -> table[key] = json.getString(key) }
            }
        }
    }

    fun translate(source: String): String = table[source] ?: source

    private fun resolveLanguage(context: Context): String {
        val configuration: Configuration = context.resources.configuration
        val locale = if (configuration.locales.isEmpty) Locale.getDefault() else configuration.locales[0]
        return when (locale.language) {
            "zh" -> {
                val traditional = locale.script.equals("Hant", true) ||
                    locale.country.equals("TW", true) ||
                    locale.country.equals("HK", true) ||
                    locale.country.equals("MO", true)
                if (traditional) "zh-Hant" else DEFAULT
            }
            "en" -> "en"
            "ja" -> "ja"
            "ko" -> "ko"
            // 其余语言统一回退英文，避免出现中文与目标语言混杂的空白界面
            else -> "en"
        }
    }
}

/** 取当前系统语言的文案；缺条目时回退中文原文。 */
fun T(source: String): String = I18n.translate(source)
