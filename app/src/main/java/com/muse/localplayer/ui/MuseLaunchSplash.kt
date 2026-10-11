package com.muse.localplayer.ui

import android.provider.Settings
import com.muse.localplayer.core.T
import com.muse.localplayer.ui.theme.rememberReducedMotion

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.muse.localplayer.R
import kotlin.math.abs
import kotlin.math.sin
import kotlinx.coroutines.delay

/**
 * 应用首启时覆盖主界面的品牌开屏。
 *
 * 动效分五层时序列展开（总时长仍控制在两秒内）：
 * 1. 氛围层：云层氛围图双层反向漂移，营造纵深；
 * 2. 声波层：圆章外两道声波环错峰由内向外扩散淡出，呼应「声场抵达」；
 * 3. 主体层：圆章弹性入场（spring 过冲）+ 一道斜向光泽扫掠；
 * 4. 文字层：品牌名 / 副标题 / 英文标逐行错峰上浮淡入；
 * 5. 细节层：底部三点呼吸脉冲 + Canvas 光尘缓缓上浮。
 */
@Composable
fun MuseLaunchSplash(onFinished: () -> Unit) {
    // 尊重系统「减少动态效果」：动画缩放为 0 时降级为纯淡入，不再做位移/扩散
    val reduceMotion = rememberReducedMotion()
    var entered by remember { mutableStateOf(false) }
    // 文字错峰：各层依次入场
    var titleIn by remember { mutableStateOf(false) }
    var subtitleIn by remember { mutableStateOf(false) }
    var captionIn by remember { mutableStateOf(false) }

    val brandAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "brandAlpha"
    )
    // 弹性过冲入场，圆章更有生气
    val brandScale by animateFloatAsState(
        targetValue = if (entered) 1f else if (reduceMotion) 0.94f else 0.72f,
        animationSpec = if (reduceMotion) {
            tween(durationMillis = 320, easing = FastOutSlowInEasing)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "brandScale"
    )
    val brandRotation by animateFloatAsState(
        targetValue = if (entered || reduceMotion) 0f else -8f,
        animationSpec = if (reduceMotion) {
            tween(durationMillis = 320, easing = FastOutSlowInEasing)
        } else {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        },
        label = "brandRotation"
    )
    val networkAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0.16f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "networkAlpha"
    )

    // 底色光晕：两层反向漂移，制造纵深呼吸
    val ambient = rememberInfiniteTransition(label = "museAmbient")
    val cloudDriftRaw by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2_600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "cloudDrift"
    )
    val glowDriftRaw by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3_200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowDrift"
    )
    // 声波环：两层错峰扩散
    val waveFrontRaw by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2_200, easing = LinearEasing)),
        label = "waveFront"
    )
    // 光泽扫掠：左上→右下循环
    val sweepRaw by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2_600, easing = LinearEasing)),
        label = "sweep"
    )
    // 光尘缓慢上浮
    val dustRaw by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6_000, easing = LinearEasing)),
        label = "dust"
    )
    // 底部脉冲点
    val pulseRaw by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1_200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    // 减少动态时把所有循环动效冻结在静态相位
    val motion = if (reduceMotion) 0f else 1f
    val cloudDrift = 0.5f + (cloudDriftRaw - 0.5f) * motion
    val glowDrift = 0.5f + (glowDriftRaw - 0.5f) * motion
    val waveFront = if (reduceMotion) 0.35f else waveFrontRaw
    val sweep = if (reduceMotion) 0.5f else sweepRaw
    val dust = if (reduceMotion) 0.4f else dustRaw
    val pulse = if (reduceMotion) 0.5f else pulseRaw

    LaunchedEffect(Unit) {
        entered = true
        delay(80)
        titleIn = true
        delay(120)
        subtitleIn = true
        delay(120)
        captionIn = true
        delay(SPLASH_HOLD_MILLIS)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF062A67)),
        contentAlignment = Alignment.Center
    ) {
        // 1) 云层氛围图：双层反向漂移
        Image(
            painter = painterResource(R.drawable.muse_launch_cloud_atmosphere),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.06f + cloudDrift * 0.05f
                    scaleY = 1.06f + cloudDrift * 0.05f
                    translationY = -26f + cloudDrift * 52f
                }
                .alpha(networkAlpha)
        )
        // 反向漂移的第二层光晕（叠加在氛围图上，颜色更低饱和）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.1f + glowDrift * 0.08f
                    scaleY = 1.1f + glowDrift * 0.08f
                    translationY = 30f - glowDrift * 60f
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x332E6BE8), Color(0x10061E52), Color.Transparent)
                    )
                )
                .alpha(networkAlpha)
        )
        // Canvas 光尘：若干微光点缓缓上浮明灭
        Canvas(modifier = Modifier.fillMaxSize().alpha(networkAlpha * 0.9f)) {
            val seeds = listOf(
                floatArrayOf(0.18f, 0.86f, 1.0f, 0.16f),
                floatArrayOf(0.34f, 0.62f, 1.6f, 0.10f),
                floatArrayOf(0.52f, 0.94f, 0.7f, 0.20f),
                floatArrayOf(0.68f, 0.48f, 1.3f, 0.14f),
                floatArrayOf(0.82f, 0.78f, 0.9f, 0.18f),
                floatArrayOf(0.44f, 0.30f, 1.4f, 0.12f),
                floatArrayOf(0.92f, 0.34f, 1.1f, 0.16f)
            )
            seeds.forEach { s ->
                val phase = (dust * s[2] + s[0]) % 1f
                val y = size.height * (s[1] - phase * 0.34f)
                val x = size.width * s[0] + sin((dust * 6.28f + s[0] * 10f)) * 8f
                val alpha = (1f - phase) * s[3] * 2.2f
                if (alpha > 0f) {
                    drawCircle(
                        color = Color(0xFFBFD8FF).copy(alpha = alpha.coerceIn(0f, 0.42f)),
                        radius = 1.6f + s[3] * 8f,
                        center = Offset(x, y)
                    )
                }
            }
        }
        // 顶部压暗、底部沉色：制造纵深层次
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0x4A010235),
                        0.42f to Color(0x15010235),
                        1f to Color(0x82010235)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .alpha(brandAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // 2) 声波层：两道错峰扩散环
                Canvas(modifier = Modifier.size(200.dp)) {
                    val maxR = size.minDimension / 2f
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    listOf(0f, 0.42f).forEach { offset ->
                        val p = (waveFront + offset) % 1f
                        val r = maxR * (0.42f + p * 0.58f)
                        val a = (1f - p) * 0.34f * brandAlpha
                        if (a > 0f) {
                            drawCircle(
                                color = Color(0xFF8FC2FF).copy(alpha = a),
                                radius = r,
                                center = Offset(cx, cy),
                                style = Stroke(width = 2.2f)
                            )
                        }
                    }
                }
                // 3) 圆章：弹性过冲 + 微旋转 + 光泽扫掠
                Surface(
                    modifier = Modifier
                        .size(88.dp)
                        .scale(brandScale)
                        .graphicsLayer { rotationZ = brandRotation }
                        .clip(CircleShape),
                    shape = CircleShape,
                    color = Color(0xD9FFFFFF),
                    shadowElevation = 18.dp,
                    tonalElevation = 0.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            // 与抽屉、关于页一致，使用 drawable-nodpi 下的品牌 PNG。
                            // 不能直接 painterResource(R.mipmap.ic_muse_launcher) ——
                            // mipmap-anydpi-v26 下的自适应图标是 XML，Compose painterResource
                            // 只接受 VectorDrawable 与位图，会抛 IllegalArgumentException 导致开屏闪退。
                            painter = painterResource(R.drawable.ic_muse_brand_mark),
                            contentDescription = T("Muse 本地音乐"),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // 斜向光泽扫掠：一道柔和高光自上而下掠过圆章
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val travel = (sweep * 2.4f - 0.7f) * size.width
                            val bandW = size.width * 0.55f
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.55f),
                                        Color.Transparent
                                    ),
                                    startX = travel,
                                    endX = travel + bandW
                                ),
                                topLeft = Offset(0f, 0f),
                                size = size
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
            // 4) 文字层：逐行错峰上浮淡入
            StaggeredText(
                text = T("Muse 音乐"),
                visible = titleIn,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            StaggeredText(
                text = T("你的私域音乐库"),
                visible = subtitleIn,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFD9ECFF)
            )
            Spacer(Modifier.height(18.dp))
            StaggeredText(
                text = "MUSE · LOCAL MUSIC",
                visible = captionIn,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xDDF2F7FF)
            )
        }
        // 5) 细节层：脉冲三点 + 状态文案
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
                .alpha(brandAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PulseDots(
                progress = pulse,
                onColor = Color(0xFFD7DCEBFF),
                offColor = Color(0x55D7DCEBFF)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = T("云端声场，正在抵达"),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xD7DCEBFF)
            )
        }
    }
}

/** 逐行错峰上浮淡入：位移 + 透明度组合 */
@Composable
private fun StaggeredText(
    text: String,
    visible: Boolean,
    style: TextStyle,
    color: Color,
    fontWeight: FontWeight? = null
) {
    val offset by animateFloatAsState(
        targetValue = if (visible) 0f else 18f,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "staggerOffset"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "staggerAlpha"
    )
    Text(
        text = text,
        style = style,
        fontWeight = fontWeight,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .graphicsLayer { translationY = offset }
            .alpha(alpha)
    )
}

/** 底部三点呼吸脉冲：三点错峰点亮 */
@Composable
private fun PulseDots(
    progress: Float,
    onColor: Color,
    offColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { index ->
            val phase = ((progress + index * 0.33f) % 1f)
            val intensity = 0.35f + (1f - abs(phase - 0.5f) * 2f) * 0.65f
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(6.dp)
                    .scale(0.75f + intensity * 0.5f)
                    .clip(CircleShape)
                    .background(
                        lerp(offColor, onColor, intensity)
                    )
            )
        }
    }
}

private const val SPLASH_HOLD_MILLIS = 1_650L
