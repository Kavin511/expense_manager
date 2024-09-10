package com.devstudio.designSystem.icons

/**
 * @Author: Kavin
 * @Date: 30/07/24
 */

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Preview
@Composable
private fun VectorPreview() {
    Image(Success, null)
}

private var _Success: ImageVector? = null

public val Success: ImageVector
    get() {
        if (_Success != null) {
            return _Success!!
        }
        _Success = ImageVector.Builder(
            name = "Success",
            defaultWidth = 80.dp,
            defaultHeight = 80.dp,
            viewportWidth = 80f,
            viewportHeight = 80f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFDCEED9)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(80f, 40f)
                curveTo(80f, 62.0914f, 62.0914f, 80f, 40f, 80f)
                curveTo(17.9086f, 80f, 0f, 62.0914f, 0f, 40f)
                curveTo(0f, 17.9086f, 17.9086f, 0f, 40f, 0f)
                curveTo(62.0914f, 0f, 80f, 17.9086f, 80f, 40f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1C9E0B)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(73.3385f, 39.9974f)
                curveTo(73.3385f, 58.4069f, 58.4147f, 73.3307f, 40.0052f, 73.3307f)
                curveTo(21.5957f, 73.3307f, 6.6719f, 58.4069f, 6.6719f, 39.9974f)
                curveTo(6.6719f, 21.5879f, 21.5957f, 6.6641f, 40.0052f, 6.6641f)
                curveTo(58.4147f, 6.6641f, 73.3385f, 21.5879f, 73.3385f, 39.9974f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(55.3305f, 28.659f)
                curveTo(56.2232f, 29.5377f, 56.2232f, 30.9623f, 55.3305f, 31.841f)
                lineTo(35.521f, 51.341f)
                curveTo(34.6284f, 52.2197f, 33.1811f, 52.2197f, 32.2885f, 51.341f)
                lineTo(24.6695f, 43.841f)
                curveTo(23.7768f, 42.9623f, 23.7768f, 41.5377f, 24.6695f, 40.659f)
                curveTo(25.5621f, 39.7803f, 27.0093f, 39.7803f, 27.902f, 40.659f)
                lineTo(33.9048f, 46.568f)
                lineTo(52.098f, 28.659f)
                curveTo(52.9907f, 27.7803f, 54.4379f, 27.7803f, 55.3305f, 28.659f)
                close()
            }
        }.build()
        return _Success!!
    }