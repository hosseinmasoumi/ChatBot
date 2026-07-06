package com.example.ui.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FrostedGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val color1 = if (isDark) Color(0xFF1E1B4B) else Color(0xFFEEF2FF)
    val color2 = if (isDark) Color(0xFF311042) else Color(0xFFFAE8FF)
    val baseBg = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color1.copy(alpha = 0.5f), color1.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(-size.width * 0.1f, -size.height * 0.1f),
                    radius = size.width * 0.85f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color2.copy(alpha = 0.4f), color2.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 1.1f, size.height * 0.95f),
                    radius = size.width * 0.95f
                )
            )
        }
        content()
    }
}

@Composable
fun FrostedCard(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 1.dp,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.large,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
    }
    val borderColor = if (isDark) {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
    } else {
        Color.White.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .border(borderWidth, borderColor, shape)
    ) {
        content()
    }
}

@Composable
fun LoadingDotsIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotScales = (0..2).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 600
                    0.3f at index * 100
                    1.0f at index * 100 + 150
                    0.3f at index * 100 + 300
                },
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_scale_$index"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        dotScales.forEach { scale ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Composable
fun UniLogo(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.2f

            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(primaryColor, secondaryColor, tertiaryColor, primaryColor),
                    center = centerOffset
                ),
                radius = radius,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(20f, 15f),
                        phase = rotateAngle * (Math.PI.toFloat() / 180f) * radius
                    )
                ),
                alpha = 0.85f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent),
                    center = centerOffset,
                    radius = radius
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
