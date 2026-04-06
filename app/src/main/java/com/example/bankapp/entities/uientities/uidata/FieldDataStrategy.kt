package com.example.bankapp.entities.uientities.uidata


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.utilities.EMAIL_MAX_SIZE
import com.example.bankapp.utilities.PASSWORD_MAX_SIZE
import com.example.bankapp.utilities.PHONE_NUMBER_MAX_SIZE
import com.example.bankapp.utilities.USERNAME_MAX_SIZE

interface FieldTypeStrategy {
    fun getKeyboardOptions(): KeyboardOptions? = null

    fun getVisualTransformation(): VisualTransformation? = null

    fun validateValue(value: String): String? = null

    @Composable
    fun buildTrailingIcon(): @Composable (() -> Unit)? = null

    fun getLetterSpacing(): Int? = null

    fun getMaxLength(): Int = 250

    fun getLeadingIcon(): ImageVector? = null
}

object GenericFieldStrategy : FieldTypeStrategy

object UserNameFieldStrategy: FieldTypeStrategy {
    override fun getMaxLength(): Int = USERNAME_MAX_SIZE

    override fun getLeadingIcon(): ImageVector = Icons.Outlined.Person
  }

    object EmailFieldStrategy : FieldTypeStrategy {
        override fun getKeyboardOptions(): KeyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Email)

        override fun getMaxLength(): Int = EMAIL_MAX_SIZE

        override fun getLeadingIcon(): ImageVector = Icons.Outlined.Email
    }

    object PhoneNumberFieldStrategy : FieldTypeStrategy {
        override fun getKeyboardOptions(): KeyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.NumberPassword)

        override fun getLetterSpacing(): Int = 2

        override fun getMaxLength(): Int = PHONE_NUMBER_MAX_SIZE

        override fun getLeadingIcon(): ImageVector = Icons.Outlined.Phone
    }

    data class PasswordFieldStrategy(
        val passwordVisible: Boolean,
        val onPasswordVisibleChange: () -> Unit
    ) : FieldTypeStrategy {

        override fun getKeyboardOptions(): KeyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Password)

        override fun getVisualTransformation(): VisualTransformation =
            if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()

        @Composable
        override fun buildTrailingIcon(): @Composable (() -> Unit) = {
            IconButton(onClick = onPasswordVisibleChange) {
                Icon(
                    if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        }

        override fun getMaxLength(): Int = PASSWORD_MAX_SIZE

        override fun getLeadingIcon(): ImageVector = Icons.Outlined.Password
    }




    data class AmountFieldStrategy(
        val transactionType: TransactionType
    ) : FieldTypeStrategy {

        override fun getKeyboardOptions(): KeyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal)

        override fun getLeadingIcon(): ImageVector = Icons.Outlined.Money
    }





















