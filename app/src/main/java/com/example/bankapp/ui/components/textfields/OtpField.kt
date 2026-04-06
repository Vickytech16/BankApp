package com.example.bankapp.ui.components.textfields

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.ui.theme.LocalDeviceSpec
import kotlinx.coroutines.delay

private val otpFontSize = 24.sp

@Composable
fun OtpInputField(
    otpInputs: List<String>,
    isError: Boolean,
    onOtpChange: (index: Int, value: String) -> Unit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var shakeOffset by remember { mutableFloatStateOf(0f) }
    val deviceSpec = LocalDeviceSpec.current

    LaunchedEffect(isError) {
        if (isError) {
            repeat(3) {
                shakeOffset = 8f
                delay(50)
                shakeOffset = -8f
                delay(50)
            }
            shakeOffset = 0f
        }
    }

    val nextFocusIndex = otpInputs.indexOfFirst { it.isEmpty() }.takeIf { it != -1 } ?: 5

    LaunchedEffect(nextFocusIndex) {
        focusRequesters[nextFocusIndex].requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(deviceSpec.textFieldWidth)
                .offset(x = shakeOffset.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                OtpBox(
                    value = otpInputs[index],
                    isError = isError,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            onOtpChange(index, newValue)
                        }
                    },
                    onBackspace = {
                        if (index > 0) {
                            onOtpChange(index, "")
                            focusRequesters[index - 1].requestFocus()
                        }
                    },
                    focusRequester = focusRequesters[index],
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OtpBox(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (value.isNotEmpty()) 1.05f else 1f,
        label = "boxScale"
    )

    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        value.isNotEmpty() -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    }

    val borderWidth = if (value.isNotEmpty() || isError) 2.dp else 1.5.dp

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) {
                onValueChange(newValue.takeLast(1))
            }
        },
        modifier = modifier
            .scale(scale)
            .aspectRatio(1f)
            .background(
                color = if (value.isEmpty()) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                else Color.Transparent,
                shape = RoundedCornerShape(dimensionResource(R.dimen.otp_box_corner_radius))
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.otp_box_corner_radius))
            )
            .focusRequester(focusRequester)

            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Backspace) {
                    onBackspace()
                    true
                } else {
                    false
                }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(
            fontSize = otpFontSize,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        },
        singleLine = true
    )
}