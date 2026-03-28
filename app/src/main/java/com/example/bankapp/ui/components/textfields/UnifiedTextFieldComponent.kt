package com.example.bankapp.ui.components.textfields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.bankapp.R

import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.textFieldFontSize
import com.example.bankapp.utilities.FieldTypeStrategy
import com.example.bankapp.utilities.GenericFieldStrategy


@Composable
fun UnifiedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    isError: Boolean = false,
    strategy: FieldTypeStrategy = GenericFieldStrategy,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    maxLength: Int? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        val maxLength = maxLength ?: strategy.getMaxLength()
        val leadingIcon = leadingIcon ?: strategy.getLeadingIcon()

        TextField(
            value = value,
            onValueChange = {
                newValue ->
                val processedValue = strategy.validateValue(newValue) ?: newValue
                onValueChange(processedValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.text_field_height)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.text_field_corner_radius)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer
            ),
            keyboardOptions = strategy.getKeyboardOptions() ?: keyboardOptions,
            textStyle = TextStyle(
                fontSize = textFieldFontSize,
                fontWeight = FontWeight.Medium,
                letterSpacing = (strategy.getLetterSpacing() ?: 0).sp
            ),
            isError = isError,
            leadingIcon = leadingIcon?.let { icon -> {
                Icon(icon, contentDescription = null)
            }},
            trailingIcon = trailingIcon ?: strategy.buildTrailingIcon(),
            visualTransformation = strategy.getVisualTransformation()
                ?: VisualTransformation.None,
            singleLine = true
        )
        supportingText?.invoke()
    }
}
