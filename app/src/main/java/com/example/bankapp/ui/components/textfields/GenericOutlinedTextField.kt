package com.example.bankapp.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.bankapp.ui.theme.textFieldLabelFontSize

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
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                labelText,
                fontWeight = FontWeight.Normal,
                fontSize = textFieldLabelFontSize
            )
        },
        singleLine = true,
        isError = isError,
        keyboardOptions = keyboardOptions,
        supportingText = supportingText,
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null)
        },
        modifier = modifier,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon
    )
}

@Composable
fun TrialingIconBehaviour(
    onPasswordVisibleChange: () -> Unit,
    passwordVisible: Boolean = false
){
    IconButton(onClick = {
        onPasswordVisibleChange()
    }) {
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

fun passwordHide(isPasswordVisible: Boolean): VisualTransformation{
    if(isPasswordVisible)
        return VisualTransformation.None
    else
        return PasswordVisualTransformation()
}