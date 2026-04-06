package com.example.bankapp.ui.components.textfields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.textFieldFontSize

@Composable
fun PickerOutlinedBox(
    label: String,
    selectedValue: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingEmoji: @Composable (() -> Unit),
    isError: Boolean = false,
    isValid: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    tickCondition: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(dimensionResource(R.dimen.text_field_corner_radius))
    val shouldShowTick = remember(selectedValue, isError, isValid) {
        !isError && (isValid || selectedValue.isNotEmpty()) && tickCondition
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            )
            Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedVisibility(visible = isError, enter = fadeIn(), exit = fadeOut()) {
                    Icon(Icons.Default.Cancel, null, Modifier.size(16.dp), MaterialTheme.colorScheme.error)
                }
                androidx.compose.animation.AnimatedVisibility(visible = shouldShowTick, enter = fadeIn(), exit = fadeOut()) {
                    Icon(Icons.Default.CheckCircle, null, Modifier.size(16.dp), Color(0xFF4DB89A))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.text_field_height))
                .clip(shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onClick = onClick
                )
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxSize(), // Fill the clickable Box
                shape = shape,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontSize = textFieldFontSize, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    )
                },
                leadingIcon = leadingEmoji,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.Transparent,
                    disabledContainerColor = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                ),
                textStyle = TextStyle(fontSize = textFieldFontSize, fontWeight = FontWeight.SemiBold)
            )
        }

        if (supportingText != null) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.md)
                .padding(top = 4.dp)
            ) {
                supportingText()
            }
        }
    }
}