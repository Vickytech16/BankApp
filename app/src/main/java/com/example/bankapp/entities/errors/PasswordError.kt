package com.example.bankapp.entities.errors

import com.example.bankapp.R

sealed class PasswordError(
    override val message: Int): UiError {

    object PasswordTooShort :
            PasswordError(R.string.password_too_short)

    object PasswordHasWhitespace :
            PasswordError(R.string.password_contains_whitespace)

    object PasswordMissingUppercase :
            PasswordError(R.string.password_missing_uppercase)

    object PasswordMissingLowercase :
            PasswordError(R.string.password_missing_lowercase)

    object PasswordMissingSpecialCharacter :
            PasswordError(R.string.password_missing_special_character)

    object PasswordMissingDigit :
            PasswordError(R.string.password_missing_digit)

}