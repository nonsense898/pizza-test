package com.cpunks.pizzacatalog.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cpunks.pizzacatalog.core.ui.theme.BackgroundBeige
import com.cpunks.pizzacatalog.core.ui.theme.PizzaTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SLICE_COUNT = 8
private const val SLICE_ANIM_MS = 250
private const val STAGGER_MS = 130L
private const val HOLD_MS = 700L
private const val SWEEP_DEG = 360f / SLICE_COUNT

private fun startDeg(index: Int) = -90f + index * SWEEP_DEG

@Composable
fun SplashScreen(
    ready: Boolean,
    onFinished: () -> Unit
) {

    val scales = List(SLICE_COUNT) { remember { Animatable(0f) } }

    val readyState = rememberUpdatedState(ready)

    LaunchedEffect(Unit) {
        while (true) {

            coroutineScope {
                scales.forEachIndexed { i, anim ->
                    launch {
                        delay(i * STAGGER_MS)
                        anim.animateTo(1f, tween(SLICE_ANIM_MS, easing = FastOutSlowInEasing))
                    }
                }
            }
            delay(HOLD_MS)

            if (readyState.value) break

            coroutineScope {
                scales.forEachIndexed { i, anim ->
                    launch {
                        delay(i * STAGGER_MS)
                        anim.animateTo(0f, tween(SLICE_ANIM_MS, easing = FastOutSlowInEasing))
                    }
                }
            }
            delay(HOLD_MS / 3)
        }
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(260.dp)) {
            scales.forEachIndexed { index, anim ->
                PizzaSlice(
                    index = index,
                    scale = anim.value,
                    modifier = Modifier.size(260.dp)
                )
            }
        }
    }
}

@Composable
private fun PizzaSlice(
    index: Int,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val start = startDeg(index)

    Image(
        painter = painterResource(id = R.drawable.pizza_splash),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier

            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.5f)
            }
            .drawWithContent {
                val r = size.minDimension / 2f
                val cx = size.width / 2f
                val cy = size.height / 2f
                val wedge = Path().apply {
                    moveTo(cx, cy)
                    arcTo(
                        rect = Rect(cx - r, cy - r, cx + r, cy + r),
                        startAngleDegrees = start,
                        sweepAngleDegrees = SWEEP_DEG,
                        forceMoveTo = false
                    )
                    close()
                }
                clipPath(wedge) { this@drawWithContent.drawContent() }
            }
    )
}

@Preview(
    name = "Assembled",
    showBackground = true,
    backgroundColor = 0xFFF3E3DA,
    widthDp = 360,
    heightDp = 640
)
@Composable
private fun AssembledPreview() {
    PizzaTheme {
        Box(Modifier
            .fillMaxSize()
            .background(BackgroundBeige), Alignment.Center) {
            Box(Modifier.size(260.dp)) {
                repeat(SLICE_COUNT) { i ->
                    PizzaSlice(i, scale = 1f, modifier = Modifier.size(260.dp))
                }
            }
        }
    }
}

@Preview(
    name = "Step 3 of 8",
    showBackground = true,
    backgroundColor = 0xFFF3E3DA,
    widthDp = 360,
    heightDp = 640
)
@Composable
private fun Step3Preview() {
    PizzaTheme {
        Box(Modifier
            .fillMaxSize()
            .background(BackgroundBeige), Alignment.Center) {
            Box(Modifier.size(260.dp)) {
                repeat(SLICE_COUNT) { i ->
                    PizzaSlice(i, scale = if (i < 3) 1f else 0f, modifier = Modifier.size(260.dp))
                }
            }
        }
    }
}
