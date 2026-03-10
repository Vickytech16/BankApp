package com.example.bankapp.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.theme.textFieldLabelFontSize
import com.example.bankapp.entities.errors.FormError

@Composable
fun PasswordVerificationOutlinedTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordError: FormError?,
    onPasswordVisibleChange: () -> Unit
){
    OutlinedTextField(
        modifier = Modifier.padding(bottom = 0.dp).fillMaxWidth(),
        value = password,
        onValueChange = onPasswordChange,
        visualTransformation =
            if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
        label = {
            Text(
                "Password",
                fontSize = textFieldLabelFontSize
            )
        },
        singleLine = true,
        isError = passwordError != null,
        supportingText = {
            ErrorTextBuilder(passwordError)
        },
        leadingIcon = {
            Icon(Icons.Outlined.Password, contentDescription = null)
        },
        trailingIcon = {
            IconButton(
                 onPasswordVisibleChange
            ) {
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
        }
    )
}