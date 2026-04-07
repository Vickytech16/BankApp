package com.example.bankapp.ui.components.textfields

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.theme.AppSpacing

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.bankapp.ui.theme.textFieldFontSize
import com.example.bankapp.utilities.ACCOUNT_NUMBER_SIZE

//
//@Composable
//fun AccountNumberOutlinedTextField(
//    accountNumber: String,
//    onAccountNumberChange: (String) -> Unit,
//    accountNumberError: FormError?,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true
//) {
//    val interactionSource = remember { MutableInteractionSource() }
//
//    val formattedDisplay = remember(accountNumber) {
//        formatAccountNumber(accountNumber)
//    }
//    val shouldShowTick = remember(accountNumber, accountNumberError) {
//        accountNumber.length == ACCOUNT_NUMBER_SIZE && accountNumberError == null
//    }
//    val placeholder = "0000 0000 0000"
//    val combinedText = remember(formattedDisplay) {
//        formattedDisplay + placeholder.drop(formattedDisplay.length)
//    }
//
//    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
//
//    Column(
//        modifier = modifier.fillMaxWidth(),
//        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = stringResource(R.string.account_number_label).uppercase(),
//                style = MaterialTheme.typography.labelLarge,
//                fontWeight = FontWeight.Bold,
//                letterSpacing = 1.sp,
//                color = if (accountNumberError != null) MaterialTheme.colorScheme.error
//                else MaterialTheme.colorScheme.secondary
//            )
//
//            Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
//                androidx.compose.animation.AnimatedVisibility(visible = accountNumberError != null, enter = fadeIn(), exit = fadeOut()) {
//                    Icon(Icons.Default.Cancel, null, Modifier.size(16.dp), MaterialTheme.colorScheme.error)
//                }
//                androidx.compose.animation.AnimatedVisibility(visible = shouldShowTick, enter = fadeIn(), exit = fadeOut()) {
//                    Icon(Icons.Default.CheckCircle, null, Modifier.size(16.dp), Color(0xFF4DB89A))
//                }
//            }
//        }
//
//        OutlinedTextField(
//            value = TextFieldValue(
//                text = formattedDisplay,
//                selection = TextRange(formattedDisplay.length)
//            ),
//            onValueChange = { newValue ->
//                val digitsOnly = newValue.text.replace(" ", "").filter { it.isDigit() }
//                if (digitsOnly.length <= ACCOUNT_NUMBER_SIZE) {
//                    onAccountNumberChange(digitsOnly)
//                }
//            },
//            enabled = enabled,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(dimensionResource(R.dimen.text_field_height)),
//            shape = RoundedCornerShape(dimensionResource(R.dimen.text_field_corner_radius)),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedContainerColor = MaterialTheme.colorScheme.surface,
//                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
//                errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Transparent,
//                errorBorderColor = MaterialTheme.colorScheme.error,
//                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
//                disabledBorderColor = Color.Transparent,
//            ),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            textStyle = TextStyle(
//                fontSize = textFieldFontSize,
//                fontWeight = FontWeight.SemiBold,
//                letterSpacing = 2.sp,
//                color = MaterialTheme.colorScheme.onSurface
//            ),
//            isError = accountNumberError != null,
//            singleLine = true,
//            visualTransformation = { text ->
//                val annotatedString = buildAnnotatedString {
//                    append(formattedDisplay)
//                    withStyle(style = SpanStyle(
//                        color = placeholderColor
//                    )
//                    ) {
//                        append(placeholder.drop(formattedDisplay.length))
//                    }
//                }
//                TransformedText(annotatedString, OffsetMapping.Identity)
//            },
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Outlined.AccountBox,
//                    contentDescription = null,
//                    tint = if (accountNumberError != null) MaterialTheme.colorScheme.error
//                    else MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            },
//            supportingText = accountNumberError?.let {
//                {
//                    Box(modifier = Modifier.padding(top = 4.dp)) {
//                        ErrorTextBuilder(it)
//                    }
//                }
//            }
//        )
//    }
//}

@Composable
fun AccountNumberOutlinedTextField(
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    accountNumberError: FormError?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val formattedDisplay = remember(accountNumber) {
        formatAccountNumber(accountNumber)
    }

    val shouldShowTick = remember(accountNumber, accountNumberError) {
        accountNumber.length == ACCOUNT_NUMBER_SIZE && accountNumberError == null
    }

    val placeholder = "0000 0000 0000"

    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)

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
                text = stringResource(R.string.account_number_label).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = if (accountNumberError != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.secondary
            )

            Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedVisibility(visible = accountNumberError != null, enter = fadeIn(), exit = fadeOut()) {
                    Icon(Icons.Default.Cancel, null, Modifier.size(16.dp), MaterialTheme.colorScheme.error)
                }
                androidx.compose.animation.AnimatedVisibility(visible = shouldShowTick, enter = fadeIn(), exit = fadeOut()) {
                    Icon(Icons.Default.CheckCircle, null, Modifier.size(16.dp), Color(0xFF4DB89A))
                }
            }
        }

        OutlinedTextField(
            value = accountNumber,
            onValueChange = { newValue ->
                val digitsOnly = newValue.filter { it.isDigit() }
                if (digitsOnly.length <= ACCOUNT_NUMBER_SIZE) {
                    onAccountNumberChange(digitsOnly)
                }
            },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.text_field_height)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.text_field_corner_radius)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledBorderColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                fontSize = textFieldFontSize,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            isError = accountNumberError != null,
            singleLine = true,
            visualTransformation = { text ->
                val typedDigits = text.text
                val formattedTyped = formatAccountNumber(typedDigits)

                val annotatedString = buildAnnotatedString {
                    append(formattedTyped)
                    withStyle(style = SpanStyle(color = placeholderColor)) {
                        append(placeholder.drop(formattedTyped.length))
                    }
                }

                val offsetMapping = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        if (offset <= 0) return 0
                        // For every 4 digits, we add a space in the transformed string
                        val spaces = (offset - 1) / 4
                        return offset + spaces
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        // Map back to the original digits string length
                        val spaces = offset / 5
                        return (offset - spaces).coerceIn(0, typedDigits.length)
                    }
                }

                TransformedText(annotatedString, offsetMapping)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccountBox,
                    contentDescription = null,
                    tint = if (accountNumberError != null) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = accountNumberError?.let {
                {
                    Box(modifier = Modifier.padding(top = 4.dp)) {
                        ErrorTextBuilder(it)
                    }
                }
            }
        )
    }
}

private fun formatAccountNumber(accountNumber: String): String {
    val digits = accountNumber.take(ACCOUNT_NUMBER_SIZE)
    return digits.chunked(4).joinToString(" ")
}