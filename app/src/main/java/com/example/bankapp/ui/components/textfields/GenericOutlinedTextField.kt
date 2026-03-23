package com.example.bankapp.ui.components.textfields

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.textFieldFontSize

@Composable
fun GenericOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier.fillMaxWidth(),
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
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
            onValueChange = onValueChange,
            modifier = modifier
                .height(dimensionResource(R.dimen.amount_textfield_height)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.amount_input_corner_radius)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer
            ),
            keyboardOptions = keyboardOptions,
            textStyle = TextStyle(
                fontSize = textFieldFontSize,
                fontWeight = FontWeight.Medium
            ),
            isError = isError,
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null)
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            singleLine = true
        )

        if (supportingText != null) {
            supportingText()
        }
    }
}

@Composable
fun TrialingIconBehaviour(
    onPasswordVisibleChange: () -> Unit,
    passwordVisible: Boolean = false
) {
    IconButton(onClick = onPasswordVisibleChange) {
        if (passwordVisible)
            Icon(
                Icons.Outlined.Visibility,
                contentDescription = "password is visible"
            )
        else
            Icon(
                Icons.Outlined.VisibilityOff,
                contentDescription = "password is not visible"
            )
    }
}

fun passwordHide(isPasswordVisible: Boolean): VisualTransformation {
    return if (isPasswordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }
}