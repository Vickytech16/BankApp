package com.example.bankapp.ui.components.profileitems

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
import com.example.bankapp.ui.theme.AppSpacing

@Composable
fun SimpleCropPreview(
    bitmap: Bitmap,
    onCropConfirmed: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        offset += pan
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = maxOf(.5f, minOf(3f, scale)),
                        scaleY = maxOf(.5f, minOf(3f, scale)),
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )


            Canvas(modifier = Modifier.fillMaxSize()) {
                val circleRadius = size.minDimension / 3
                val path = Path().apply {
                    addOval(Rect(center, circleRadius))
                }
                drawContext.canvas.nativeCanvas.apply {
                    val checkpoint = saveLayer(null, null)
                    drawRect(Color.Black.copy(alpha = 0.6f))
                    // Clear the circle area
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
                // We create a new bitmap that represents the 'Visual' crop
                val cropped = renderCroppedBitmap(bitmap, scale, offset)
                onCropConfirmed(cropped)
            }) {
                Text("Apply Crop")
            }
        }
    }
}

private fun renderCroppedBitmap(source: Bitmap, scale: Float, offset: Offset): Bitmap {
    val size = 500 // Your target size
    val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(output)

    val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    val matrix = Matrix()
    matrix.postTranslate(-source.width / 2f, -source.height / 2f)
    matrix.postScale(scale, scale)
    matrix.postTranslate(size / 2f + offset.x, size / 2f + offset.y)

    canvas.drawBitmap(source, matrix, paint)
    return output
}