package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.PasswordError
import com.example.bankapp.entities.errors.UiError
import com.example.bankapp.ui.theme.AppSpacing

@Composable
fun ErrorTextBuilder(error: FormError?) {

        error?.let {

            val message =

                when (error) {

                    is FormError.InvalidAmount,

                    is FormError.InvalidCredentials,

                    is FormError.InvalidData,

                    is FormError.InvalidEmailFormat,

                    is FormError.InvalidUsername,

                    is FormError.NegativeAmount,

                    is FormError.PasswordDoesntMatch,

                    is FormError.AllFieldsAreRequired,

                    is FormError.UnknownError,

                    is FormError.OtpDoesNotMatch,

                    is FormError.OtpExpired,

                    is FormError.InvalidDepositAmountFormat,

                    is FormError.InvalidCashTransferAmountFormat,

                    is FormError.AlreadyYourFriendError,

                    is FormError.UserDoesNotHaveAccountError,

                    is FormError.YouAreTheUser,

                    is FormError.InvalidPhoneNumber,

                    is FormError.RecoveryKeyMustBe10DigitsLong,

                    is FormError.RecoveryKeyDoesNotMatch,

                    is FormError.UserNameCannotStartWithNumber,

                    is FormError.SamePassword
                        ->

                        stringResource(error.message)

                    is FormError.TooLongData ->
                        stringResource(
                            error.message,
                            stringResource(error.fieldNameRes),
                            error.characterLimitRes
                        )

                    is FormError.TooShortData ->
                        stringResource(
                            error.message,
                            stringResource(error.fieldNameRes),
                            error.characterLimitRes
                        )

                    is FormError.UserAlreadyExists,
                        ->
                        stringResource(
                            error.message,
                            stringResource(error.fieldNameRes)
                        )

                    is FormError.InvalidNumericalFIeld ->

                        stringResource(
                            error.message,
                            stringResource(error.fieldNameRes)
                        )

                    is FormError.EmptyData,
                        ->
                        stringResource(
                            error.message,
                            stringResource(error.fieldNameRes)
                        )

                    is FormError.BeneficiaryLimitReached
                       ->
                           stringResource(
                               error.message,
                               error.limit
                           )

                    is FormError.LimitExceeded
                        ->
                           stringResource(
                               error.message,
                               error.data
                           )

                    is FormError.LimitExceededDeposit
                        ->
                        stringResource(
                            error.message,
                            error.data
                        )

                }

            Text(
                message, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
    }
}

@Composable
fun PasswordErrorTextBuilder(errors: List<UiError>) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        errors.forEach { error ->
            val message = getPasswordErrors(error)

            Row(modifier = Modifier.fillMaxWidth()) {
                if (errors.size > 1) {
                    Text(
                        text = "• ",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun getPasswordErrors(error: UiError): String {
    return when (error) {
        is FormError.EmptyData ->
            stringResource(
                error.message,
                stringResource(error.fieldNameRes)
            )

        is FormError.TooLongData ->
            stringResource(
                error.message,
                stringResource(error.fieldNameRes),
                error.characterLimitRes
            )

        else -> stringResource(error.message)
    }
}