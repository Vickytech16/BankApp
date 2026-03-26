package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.UiError

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

                    is FormError.UserDoesNotHaveAccountError
                        ->

                        stringResource(error.message)

                    is FormError.TooLongData ->
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


                    is FormError.YouAreTheUser ->
                        stringResource(error.message, stringResource(error.fieldNameRes))
                }

            Text(
                message, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
    }
}

@Composable
fun PasswordErrorTextBuilder(errors: List<UiError>){
    val bulletInCharacter =
        if(errors.size > 1)
            "• "
        else
            ""
    Column() {
        errors.forEach { error ->
            val message =
                when (error) {

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

            Text("$bulletInCharacter$message", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}