package com.example.bankapp.ui.components.textfields

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.graphics.SolidColor
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
import kotlinx.coroutines.delay

import androidx.compose.ui.input.key.Key

private val otpFontSize = 20.sp

@Composable
fun OtpInputField(
    otpInputs: List<String>,
    isError: Boolean,
    onOtpChange: (index: Int, value: String) -> Unit
) {
    val focusRequesters = List(6) { FocusRequester() }
    var shakeOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            repeat(3) {
                shakeOffset = 10f
                delay(50)
                shakeOffset = -10f
                delay(50)
            }
            shakeOffset = 0f
            delay(500)
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
                .widthIn(dimensionResource(R.dimen.otp_field_max_width))
                .offset(x = shakeOffset.dp),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.filter_chip_spacing)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                OtpBox(
                    value = otpInputs[index],
                    isError = isError,
                    onValueChange = { newValue ->
                        onOtpChange(index, newValue)
                    },
                    onBackspace = {
                        if (index > 0) {
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
    val scale = animateFloatAsState(
        targetValue = 1f,
        label = "boxScale"
    )

    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        value.isNotEmpty() -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 1 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
                onValueChange(newValue)
            }
        },
        modifier = modifier
            .scale(scale.value)
            .border(
                width = dimensionResource(R.dimen.otp_box_border),
                color = borderColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.otp_box_corner_radius))
            )
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Backspace && value.isEmpty()) {
                    onBackspace()
                    true
                } else {
                    false
                }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(
            fontSize = otpFontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.otp_box_padding)),
                contentAlignment = Alignment.Center
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "•",
                        style = TextStyle(
                            fontSize = otpFontSize,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                } else {
                    Text(
                        text = "•",
                        style = TextStyle(
                            fontSize = otpFontSize,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                innerTextField()
            }
        },
        singleLine = true
    )
}