package com.example.bankapp.ui.components.textfields

import androidx.compose.ui.text.input.KeyboardType



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.types.ui.TextFieldType

import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.textFieldFontSize
import com.example.bankapp.utilities.ACCOUNT_NUMBER_SIZE

@Composable
fun UnifiedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    isError: Boolean = false,
    fieldType: TextFieldType = TextFieldType.GENERIC,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier.fillMaxWidth(),
    passwordVisible: Boolean = false,
    onPasswordVisibleChange: (() -> Unit)? = null,
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

        TextField(
            value = value,
            onValueChange = {
                newValue ->
                val processedValue = validateFieldValue(newValue, fieldType, maxLength)
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
            keyboardOptions = getKeyboardOptions(fieldType, keyboardOptions),
            textStyle = TextStyle(
                fontSize = textFieldFontSize,
                fontWeight = FontWeight.Medium,
                letterSpacing = getLetterSpacing(fieldType).sp
            ),
            isError = isError,
            leadingIcon = leadingIcon?.let { icon -> {
                    Icon(icon, contentDescription = null)
                }
            },
            trailingIcon = buildTrailingIcon(
                trailingIcon = trailingIcon,
                fieldType = fieldType,
                passwordVisible = passwordVisible,
                onPasswordVisibleChange = onPasswordVisibleChange
            ),
            visualTransformation = getVisualTransformation(fieldType, passwordVisible),
            singleLine = true
        )
        supportingText?.invoke()
    }
}

private fun validateFieldValue(
    value: String,
    fieldType: TextFieldType,
    maxLength: Int?
): String {
    return when (fieldType) {
        TextFieldType.ACCOUNT_NUMBER -> {
            val digitsOnly = value.replace(" ", "")
            if (digitsOnly.length <= ACCOUNT_NUMBER_SIZE) digitsOnly else digitsOnly.take(ACCOUNT_NUMBER_SIZE)
        }
        TextFieldType.AMOUNT -> {
            value.filter { it.isDigit() || it == '.' }
        }
        else -> {
            maxLength?.let { value.take(it) } ?: value
        }
    }
}

private fun getKeyboardOptions(
    fieldType: TextFieldType,
    defaultOptions: KeyboardOptions
): KeyboardOptions {
    return when (fieldType) {
        TextFieldType.PASSWORD -> KeyboardOptions(keyboardType = KeyboardType.Password)
        TextFieldType.ACCOUNT_NUMBER -> KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        TextFieldType.AMOUNT -> KeyboardOptions(keyboardType = KeyboardType.Decimal)
        TextFieldType.EMAIL -> KeyboardOptions(keyboardType = KeyboardType.Email)
        else -> defaultOptions
    }
}

private fun getVisualTransformation(
    fieldType: TextFieldType,
    passwordVisible: Boolean
): VisualTransformation {
    return when (fieldType) {
        TextFieldType.PASSWORD -> {
            if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        }
        else -> VisualTransformation.None
    }
}

private fun getLetterSpacing(fieldType: TextFieldType): Int {
    return when (fieldType) {
        TextFieldType.ACCOUNT_NUMBER -> 2
        else -> 0
    }
}

@Composable
private fun buildTrailingIcon(
    trailingIcon: @Composable (() -> Unit)?,
    fieldType: TextFieldType,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (() -> Unit)?
): @Composable (() -> Unit)? {
    return when {
        trailingIcon != null -> trailingIcon
        fieldType == TextFieldType.PASSWORD -> {
            {
                IconButton(onClick = {
                    onPasswordVisibleChange?.invoke()
                }) {
                    Icon(
                        if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        }
        fieldType == TextFieldType.AMOUNT -> {
            {
                Text(
                    text = stringResource(R.string.rupee_symbol),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = AppSpacing.md)
                )
            }
        }
        else -> null
    }
}

