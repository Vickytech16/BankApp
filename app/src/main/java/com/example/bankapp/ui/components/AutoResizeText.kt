package com.example.bankapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp


@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    maxLines: Int = 1,
    softWrap: Boolean = false
) {
    var resizedTextStyle by remember(text) { mutableStateOf(style) }
    var shouldDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent { if (shouldDraw) drawContent() },
        softWrap = softWrap,
        style = resizedTextStyle,
        maxLines = maxLines,
        overflow = TextOverflow.Clip,
        onTextLayout = {
            result: TextLayoutResult ->

            val minFontSize = 10.sp
            if (result.didOverflowWidth && resizedTextStyle.fontSize > minFontSize) {
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95
                )
            } else {
                shouldDraw = true
            }
        }
    )
}