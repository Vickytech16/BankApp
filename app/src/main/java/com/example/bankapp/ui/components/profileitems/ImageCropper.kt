package com.example.bankapp.temp



import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.bankapp.ui.theme.AppSpacing
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale

@Composable
fun SimpleCropPreview(
    bitmap: Bitmap,
    onCropConfirmed: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .onGloballyPositioned { boxSize = it.size }
                .pointerInput(boxSize) {
                    if (boxSize.width == 0) return@pointerInput

                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(0.5f, 5f)

                        val fitScale = minOf(
                            boxSize.width.toFloat() / bitmap.width,
                            boxSize.height.toFloat() / bitmap.height
                        )
                        val fittedW = bitmap.width * fitScale
                        val fittedH = bitmap.height * fitScale

                        val circleRadiusPx = minOf(boxSize.width, boxSize.height) / 3f

                        val imgWidthOnScreen = fittedW * newScale
                        val imgHeightOnScreen = fittedH * newScale

                        val maxOffsetX = if (imgWidthOnScreen > circleRadiusPx * 2) {
                            (imgWidthOnScreen / 2f - circleRadiusPx)
                        } else {

                            (circleRadiusPx - imgWidthOnScreen / 2f).coerceAtLeast(0f)
                        }

                        val maxOffsetY = if (imgHeightOnScreen > circleRadiusPx * 2) {
                            (imgHeightOnScreen / 2f - circleRadiusPx)
                        } else {
                            (circleRadiusPx - imgHeightOnScreen / 2f).coerceAtLeast(0f)
                        }

                        scale = newScale
                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                            y = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val circleRadius = size.minDimension / 3f
                drawContext.canvas.nativeCanvas.apply {
                    val checkpoint = saveLayer(null, null)

                    drawRect(
                        color = Color.Black.copy(alpha = 0.7f)
                    )

                    drawCircle(
                        center = center,
                        radius = circleRadius,
                        color = Color.Transparent,
                        blendMode = BlendMode.Clear
                    )

                    restoreToCount(checkpoint)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(AppSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel", color = Color.White)
            }
            Button(onClick = {
                val cropped = renderCroppedBitmap(
                    source = bitmap,
                    scale = scale,
                    offset = offset,
                    boxSize = boxSize,
                    imageSize = boxSize
                )
                onCropConfirmed(cropped)
            }) {
                Text("Apply Crop")
            }
        }
    }
}

private fun renderCroppedBitmap(
    source: Bitmap,
    scale: Float,
    offset: Offset,
    boxSize: IntSize,
    imageSize: IntSize
): Bitmap {
    val outputSize = 1000
    val fitScale = minOf(
        imageSize.width.toFloat() / source.width,
        imageSize.height.toFloat() / source.height
    )

    val fittedW = source.width * fitScale
    val fittedH = source.height * fitScale

    val circleRadiusPx = minOf(boxSize.width, boxSize.height) / 3f
    val circleCenterX = boxSize.width / 2f
    val circleCenterY = boxSize.height / 2f

    val imageCenterX = circleCenterX + offset.x
    val imageCenterY = circleCenterY + offset.y

    val imageTopLeftX = imageCenterX - (fittedW * scale) / 2f
    val imageTopLeftY = imageCenterY - (fittedH * scale) / 2f

    val totalScale = fitScale * scale

    val srcCenterX = (circleCenterX - imageTopLeftX) / totalScale
    val srcCenterY = (circleCenterY - imageTopLeftY) / totalScale

    val srcRadius = circleRadiusPx / totalScale

    val srcLeft = (srcCenterX - srcRadius).toInt().coerceAtLeast(0)
    val srcTop = (srcCenterY - srcRadius).toInt().coerceAtLeast(0)
    val srcRight = (srcCenterX + srcRadius).toInt().coerceAtMost(source.width)
    val srcBottom = (srcCenterY + srcRadius).toInt().coerceAtMost(source.height)

    val srcWidth = srcRight - srcLeft
    val srcHeight = srcBottom - srcTop

    if (srcWidth <= 0 || srcHeight <= 0) {
        return createBitmap(outputSize, outputSize)
    }

    val cropped = Bitmap.createBitmap(source, srcLeft, srcTop, srcWidth, srcHeight)
    val scaled = cropped.scale(outputSize, outputSize)

    val output = createBitmap(outputSize, outputSize)
    val canvas = android.graphics.Canvas(output)

    val circlePath = android.graphics.Path().apply {
        addCircle(outputSize / 2f, outputSize / 2f, outputSize / 2f, android.graphics.Path.Direction.CW)
    }
    canvas.clipPath(circlePath)
    canvas.drawBitmap(scaled, 0f, 0f, null)

    return output
}
