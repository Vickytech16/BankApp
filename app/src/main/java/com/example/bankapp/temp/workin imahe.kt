package com.example.bankapp.temp

/*

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


@Composable
fun SimpleCropPreview(
    bitmap: Bitmap,
    onCropConfirmed: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .onGloballyPositioned { coordinates ->
                    boxSize = coordinates.size
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 5f)
                        offset += pan  // simple, no clamping — let it move freely
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
                    .onGloballyPositioned { coordinates ->
                        imageSize = coordinates.size
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val circleRadius = size.minDimension / 3f
                val path = Path().apply {
                    addOval(Rect(center, circleRadius))
                }
                drawContext.canvas.nativeCanvas.apply {
                    val checkpoint = saveLayer(null, null)
                    drawRect(Color.Black.copy(alpha = 0.6f))
                    drawPath(path, Color.Transparent, blendMode = BlendMode.Clear)
                    restoreToCount(checkpoint)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(AppSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween
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
                    imageSize = imageSize
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

    // --- Step 1: Understand how ContentScale.Fit scaled the image on screen ---
    // imageSize is the composable size (fillMaxSize box), but Fit letterboxes
    // the actual image content inside it. Recompute the actual displayed image
    // dimensions after Fit scaling.
    val fitScale = minOf(
        imageSize.width.toFloat() / source.width,
        imageSize.height.toFloat() / source.height
    )
    // Actual rendered image size on screen (before user zoom)
    val fittedW = source.width * fitScale
    val fittedH = source.height * fitScale

    // --- Step 2: The circle on screen ---
    val circleRadiusPx = minOf(boxSize.width, boxSize.height) / 3f
    // Circle center is the center of the box
    val circleCenterX = boxSize.width / 2f
    val circleCenterY = boxSize.height / 2f

    // --- Step 3: Where is the image origin (top-left) on screen right now? ---
    // Image composable is centered in box, then graphicsLayer scale+translate applied.
    // The image center on screen = box center + offset (graphicsLayer translates around center)
    val imageCenterX = circleCenterX + offset.x
    val imageCenterY = circleCenterY + offset.y

    // Image top-left on screen
    val imageTopLeftX = imageCenterX - (fittedW * scale) / 2f
    val imageTopLeftY = imageCenterY - (fittedH * scale) / 2f

    // --- Step 4: Convert circle center from screen coords → source bitmap coords ---
    val totalScale = fitScale * scale  // screen pixels per source pixel

    val srcCenterX = (circleCenterX - imageTopLeftX) / totalScale
    val srcCenterY = (circleCenterY - imageTopLeftY) / totalScale

    // Circle radius in source bitmap pixels
    val srcRadius = circleRadiusPx / totalScale

    // --- Step 5: Extract the square bounding box from source bitmap ---
    val srcLeft = (srcCenterX - srcRadius).toInt().coerceAtLeast(0)
    val srcTop = (srcCenterY - srcRadius).toInt().coerceAtLeast(0)
    val srcRight = (srcCenterX + srcRadius).toInt().coerceAtMost(source.width)
    val srcBottom = (srcCenterY + srcRadius).toInt().coerceAtMost(source.height)

    val srcWidth = srcRight - srcLeft
    val srcHeight = srcBottom - srcTop

    if (srcWidth <= 0 || srcHeight <= 0) {
        // Fallback: return blank bitmap if something went wrong
        return Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
    }

    // --- Step 6: Crop the source and scale to output size ---
    val cropped = Bitmap.createBitmap(source, srcLeft, srcTop, srcWidth, srcHeight)
    val scaled = Bitmap.createScaledBitmap(cropped, outputSize, outputSize, true)

    // --- Step 7: Apply circular mask for transparency outside circle ---
    val output = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(output)

    val circlePath = android.graphics.Path().apply {
        addCircle(outputSize / 2f, outputSize / 2f, outputSize / 2f, android.graphics.Path.Direction.CW)
    }
    canvas.clipPath(circlePath)
    canvas.drawBitmap(scaled, 0f, 0f, null)

    return output
}
 */