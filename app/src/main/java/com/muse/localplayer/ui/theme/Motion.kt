package com.muse.localplayer.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext

/**
 * Muse 统一动效语言。
 *
 * 设计原则（对齐动效规范）：
 * - 反馈类动效（按下、选中）短促克制：100~200ms，不喧宾夺主；
 * - 状态类动效（展开、切换）200~320ms，进出场有节奏；
 * - 退场快于入场（约 75%）；
 * - 默认不使用弹跳/回弹，仅在最轻量的“确认感”场景使用轻阻尼弹簧；
 * - 全量尊重系统「减少动态效果」：动画缩放为 0 时，一切位移/缩放/旋转退化为纯淡入或无动画。
 */
object MuseMotion {
    /** 按压反馈：按下 110ms 快速下沉，抬起 220ms 轻弹回弹。 */
    const val PRESS_IN_MILLIS = 110
    const val PRESS_OUT_MILLIS = 220

    /** 状态切换：常规过渡时长。 */
    const val STATE_MILLIS = 260

    /** 列表项/内容入场时长。 */
    const val ENTER_MILLIS = 280

    /** 退场时长，约为入场的 75%。 */
    const val EXIT_MILLIS = 190

    /** 列表/入场错峰步长。 */
    const val STAGGER_STEP_MILLIS = 40

    /** 按压缩放的目标比例。 */
    const val PRESS_SCALE = 0.97f

    /** 播放/暂停这类图标切换的缩放起点。 */
    const val ICON_SWAP_SCALE = 0.8f
}

/**
 * 探测系统是否开启了「减少动态效果」。
 *
 * Android 的等价物是开发者选项/无障碍中的动画缩放比例：
 * `ANIMATOR_DURATION_SCALE == 0f` 表示动画被完全关闭。
 * 读取失败时保守地返回 false（保持动效），避免因异常误伤正常体验。
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE
            ) == 0f
        }.getOrDefault(false)
    }
}

/**
 * 是否把动效时长降级为 0：供 animateXxxAsState 的 durationMillis 直接使用。
 */
fun motionMillis(reduceMotion: Boolean, millis: Int): Int = if (reduceMotion) 0 else millis

/**
 * 列表/卡片按压反馈：按下轻微缩小，抬起带回弹。
 *
 * 用法：`Modifier.musePressScale(interactionSource, reduceMotion)`
 * 需与 `Modifier.clickable(interactionSource = ..., indication = ...)` 共用同一个 interactionSource。
 */
fun Modifier.musePressScale(
    interactionSource: MutableInteractionSource,
    reduceMotion: Boolean,
    pressedScale: Float = MuseMotion.PRESS_SCALE
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (pressed && !reduceMotion) pressedScale else 1f,
        animationSpec = if (pressed) {
            tween(durationMillis = MuseMotion.PRESS_IN_MILLIS, easing = FastOutSlowInEasing)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "muse_press_scale"
    )
    this.scale(scale.value)
}

/**
 * 为可点击元素创建统一的按压交互源。
 */
@Composable
fun rememberMuseInteraction(): MutableInteractionSource = remember { MutableInteractionSource() }

/**
 * 一个带“确认感”的布尔切换动画：用于收藏、选中这类需要轻微反馈的状态。
 *
 * @return 0f~1f 的动画进度，可直接用于缩放/透明度/颜色插值。
 */
@Composable
fun rememberSwitchProgress(target: Boolean, reduceMotion: Boolean): State<Float> =
    animateFloatAsState(
        targetValue = if (target) 1f else 0f,
        animationSpec = if (reduceMotion) {
            tween(durationMillis = 0)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "muse_switch_progress"
    )

/**
 * 读取一个随时间平滑跟随的数值动画：用于进度、音量等连续变化量。
 * 与直接赋值相比，可消除进度条“跳变”。
 */
@Composable
fun rememberSmoothedFloat(target: Float, reduceMotion: Boolean): State<Float> =
    animateFloatAsState(
        targetValue = target,
        animationSpec = if (reduceMotion) {
            tween(durationMillis = 0)
        } else {
            tween(durationMillis = MuseMotion.STATE_MILLIS, easing = LinearOutSlowInEasing)
        },
        label = "muse_smoothed_float"
    )
