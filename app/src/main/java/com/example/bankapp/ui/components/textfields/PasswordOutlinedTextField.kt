package com.example.bankapp.ui.components.textfields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Password
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.theme.AppSpacing

@Composable
fun PasswordVerificationOutlinedTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordError: FormError?,
    onPasswordVisibleChange: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = "Password",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        TextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
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
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
            ),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = passwordError != null,
            leadingIcon = {
                Icon(Icons.Outlined.Password, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = onPasswordVisibleChange) {
                    if (passwordVisible)
                        Icon(
                            Icons.Outlined.Visibility,
                            contentDescription = "make password visible"
                        )
                    else
                        Icon(
                            Icons.Outlined.VisibilityOff,
                            contentDescription = "make password invisible"
                        )
                }
            },
            singleLine = true
        )

        if (passwordError != null) {
            ErrorTextBuilder(passwordError)
        }
    }
}