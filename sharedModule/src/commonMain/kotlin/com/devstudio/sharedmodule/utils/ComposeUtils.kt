package com.devstudio.sharedmodule.utils

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devstudio.designSystem.appColors

/**
 * @Author: Kavin
 * @Date: 16/07/24
 */
@Composable
fun ShimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition()
        val translateAnimation = transition.animateFloat(
            initialValue = 0f, targetValue = targetValue, animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Reverse
            )
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun Modifier.shimmerEffect(showShimmer: Boolean = true, targetValue: Float = 1000f) = composed {
    background(ShimmerBrush(showShimmer, targetValue))
}

@Composable
fun LabelAndValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = appColors.material.onPrimaryContainer,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        RegularText(label)
        RegularText(value, color = color)
    }
}

@Composable
fun RegularText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = appColors.material.onPrimaryContainer,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(text = text, fontSize = 16.sp, modifier = modifier, color = color, style = style)
}


@Composable
fun DefaultDivider() {
    HorizontalDivider(
        thickness = 5.dp
    )
}

@Composable
fun SubHeading(text: String) {
    Text(
        text, fontSize = 16.sp, style = MaterialTheme.typography.labelMedium
    )
}