package com.muse.localplayer.playback

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer

/** 单个均衡器频段：中心频率（Hz）与当前增益（毫贝）。 */
data class EqualizerBand(
    val index: Int,
    val centerFreqHz: Int,
    val levelMb: Int,
    val minLevelMb: Int,
    val maxLevelMb: Int
)

/** 均衡器 UI 快照，供 Compose 渲染开关、预设与频段滑块。 */
data class EqualizerState(
    val available: Boolean = true,
    val enabled: Boolean = false,
    val presetIndex: Int = 0,
    val presets: List<String> = emptyList(),
    val bands: List<EqualizerBand> = emptyList(),
    val bassSupported: Boolean = false,
    val bassStrength: Int = 0,
    val bassMax: Int = BASS_MAX_STRENGTH
) {
    companion object {
        const val BASS_MAX_STRENGTH = 1000
    }
}

/**
 * 均衡器与低音增强封装，基于 android.media.audiofx。
 * 作用于全局输出混音会话（session 0），与系统音效面板的处理方式保持一致。
 * 设备不支持时静默降级，界面会根据 [available] 隐藏控件。
 */
class EqualizerController {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    init {
        attach()
    }

    private fun attach() {
        if (equalizer != null) return
        equalizer = runCatching { Equalizer(0, OUTPUT_MIX_AUDIO_SESSION) }.getOrNull()
        bassBoost = equalizer?.let { runCatching { BassBoost(0, OUTPUT_MIX_AUDIO_SESSION) }.getOrNull() }
    }

    /** 读取当前状态快照；任何底层异常都返回“不可用”。 */
    fun snapshot(): EqualizerState {
        val eq = equalizer ?: return EqualizerState(available = false)
        return runCatching {
            val presetCount = eq.numberOfPresets.toInt()
            val presets = (0 until presetCount).mapNotNull { index ->
                runCatching { eq.getPresetName(index.toShort()) }.getOrNull()
            }
            val range = eq.bandLevelRange
            val bandCount = eq.numberOfBands.toInt()
            val bands = (0 until bandCount).map { index ->
                val band = index.toShort()
                EqualizerBand(
                    index = index,
                    centerFreqHz = eq.getCenterFreq(band) / 1000,
                    levelMb = eq.getBandLevel(band).toInt(),
                    minLevelMb = range.getOrNull(0)?.toInt() ?: -1500,
                    maxLevelMb = range.getOrNull(1)?.toInt() ?: 1500
                )
            }
            val boost = bassBoost
            EqualizerState(
                available = true,
                enabled = eq.enabled,
                presetIndex = eq.currentPreset.toInt(),
                presets = presets,
                bands = bands,
                bassSupported = boost != null,
                bassStrength = boost?.let { runCatching { it.roundedStrength.toInt() }.getOrDefault(0) } ?: 0,
                bassMax = EqualizerState.BASS_MAX_STRENGTH
            )
        }.getOrElse { EqualizerState(available = false) }
    }

    fun setEnabled(enabled: Boolean) {
        equalizer?.enabled = enabled
        bassBoost?.enabled = enabled
    }

    fun setPreset(presetIndex: Int) {
        val eq = equalizer ?: return
        runCatching { eq.usePreset(presetIndex.toShort()) }
    }

    fun setBandLevel(bandIndex: Int, levelMb: Int) {
        val eq = equalizer ?: return
        runCatching { eq.setBandLevel(bandIndex.toShort(), levelMb.toShort()) }
    }

    fun setBassBoostStrength(strength: Int) {
        val boost = bassBoost ?: return
        runCatching { boost.setStrength(strength.coerceIn(0, EqualizerState.BASS_MAX_STRENGTH).toShort()) }
    }

    fun release() {
        runCatching { equalizer?.release() }
        equalizer = null
        runCatching { bassBoost?.release() }
        bassBoost = null
    }

    private companion object {
        // Android 音效框架约定：0 代表全局输出混音会话。
        const val OUTPUT_MIX_AUDIO_SESSION = 0
    }
}
