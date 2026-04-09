package com.example.bankapp.ui.components.textfields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.textFieldFontSize
import com.example.bankapp.entities.uientities.uidata.FieldTypeStrategy
import com.example.bankapp.entities.uientities.uidata.GenericFieldStrategy

@Composable
fun UnifiedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    isError: Boolean = false,
    isValid: Boolean = false,
    strategy: FieldTypeStrategy = GenericFieldStrategy,
    leadingIcon: ImageVector? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    placeholderText: String? = null,
    showTickCondition: ((String) -> Boolean)? = null,
    enabled: Boolean = true,
    backgroundColor: Color? = null,
    maxCharLimit: Int? = null, // New
    showCharCount: Boolean = false // New
) {
    val interactionSource = remember { MutableInteractionSource() }

    val shouldShowTick = remember(value, isError, isValid) {
        if (isError)
        {
            false
        }
        else if (showTickCondition != null)
        {
            showTickCondition(value)
        }
        else
        {
            isValid || value.isNotEmpty()
        }
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
                text = labelText.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = if (isError)
                {
                    MaterialTheme.colorScheme.error
                }
                else
                {
                    MaterialTheme.colorScheme.secondary
                }
            )

            Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = isError,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(Icons.Default.Cancel, null, Modifier.size(16.dp), MaterialTheme.colorScheme.error)
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowTick,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(Icons.Default.CheckCircle, null, Modifier.size(16.dp), Color(0xFF4DB89A))
                }
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (maxCharLimit == null || input.length <= maxCharLimit)
                {
                    onValueChange(input)
                }
            },
            readOnly = readOnly,
            enabled = onClick == null && enabled,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = dimensionResource(R.dimen.text_field_height))
                .let {
                    if (onClick != null)
                    {
                        it.clickable(interactionSource, LocalIndication.current) { onClick() }
                    }
                    else
                    {
                        it
                    }
                },
            shape = RoundedCornerShape(dimensionResource(R.dimen.text_field_corner_radius)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = backgroundColor ?: MaterialTheme.colorScheme.surfaceVariant,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = if (isError)
                {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                }
                else
                {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                disabledBorderColor = if (isError)
                {
                    MaterialTheme.colorScheme.error
                }
                else
                {
                    Color.Transparent
                },
            ),
            keyboardOptions = strategy.getKeyboardOptions() ?: keyboardOptions,
            textStyle = TextStyle(
                fontSize = textFieldFontSize,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (strategy.getLetterSpacing() ?: 0).sp
            ),
            isError = isError,
            leadingIcon = when {
                leadingContent != null -> leadingContent
                leadingIcon != null || strategy.getLeadingIcon() != null -> {
                    {
                        Icon(
                            imageVector = leadingIcon ?: strategy.getLeadingIcon()!!,
                            contentDescription = null,
                            tint = if (isError)
                            {
                                MaterialTheme.colorScheme.error
                            }
                            else
                            {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                else -> null
            },
            prefix = prefix,
            trailingIcon = trailingIcon ?: strategy.buildTrailingIcon(),
            visualTransformation = strategy.getVisualTransformation() ?: VisualTransformation.None,
            singleLine = true,
            maxLines = 1,
            placeholder = placeholderText?.let {
                {
                    Text(
                        it,
                        style = TextStyle(
                            fontSize = textFieldFontSize,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }
            },
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f).padding(top = 4.dp)) {
                        supportingText?.invoke()
                    }

                    if (showCharCount && maxCharLimit != null)
                    {
                        Text(
                            text = "${value.length}/$maxCharLimit",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (value.length >= maxCharLimit)
                            {
                                MaterialTheme.colorScheme.error
                            }
                            else
                            {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                        )
                    }
                }
            }
        )
    }
}