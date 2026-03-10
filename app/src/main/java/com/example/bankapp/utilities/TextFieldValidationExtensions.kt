package com.example.bankapp.utilities

import android.util.Patterns
import com.example.bankapp.services.PasswordValidationService
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.PasswordError
import java.math.BigDecimal


fun String.emptyTextFieldErrorMessageBuilder(fieldNameRes: Int) : FormError?{
    return if(this.isBlank())
        FormError.EmptyData(fieldNameRes)
    else
        null
}

fun String.maxAllowedCharacterErrorMessageBuilder(fieldName: Int, characterLimit: Int): FormError?{
    return if(this.length > characterLimit)
        FormError.TooLongData(fieldName, characterLimit)
    else
        null
}

fun String.invalidUserNameErrorMessageBuilder(): FormError? {
    return if (this.any { !it.isLetter() && !it.isWhitespace() })
        FormError.InvalidUsername
    else
        null
}

fun String.invalidEmailErrorMessageBuilder(): FormError? {
    return if(!Patterns.EMAIL_ADDRESS.matcher(this).matches())
        FormError.InvalidEmailFormat
    else
        null
}

fun String.invalidNumericalFieldErrorMessageBuilder(fieldNameRes: Int): FormError? {
    return if(this.any{!it.isDigit()})
        FormError.InvalidNumericalFIeld(fieldNameRes)
    else
        null
}

fun String.invalidAmountErrorMessageBuilder(): FormError? {

    return if(this.toBigDecimalOrNull()==null)
        FormError.InvalidAmount
    else
        null
}

fun String.invalidPasswordErrorMessageBuilder(): List<PasswordError> {
    return PasswordValidationService.validatePassword(this)
}

fun String.invalidConfirmPasswordErrorMessageBuilder(password: String): FormError? {
    return if(this!=password)
        FormError.PasswordDoesntMatch
    else
        null
}

fun BigDecimal.NegativeAmountErrorMessageBuilder(): FormError? {
    return if(this <= BigDecimal.ZERO)
        FormError.NegativeAmount
    else
        null
}
