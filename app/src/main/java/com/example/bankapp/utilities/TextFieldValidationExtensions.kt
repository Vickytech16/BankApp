package com.example.bankapp.utilities

import android.util.Patterns
import com.example.bankapp.services.PasswordValidationService
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.PasswordError
import com.example.bankapp.ui.theme.userNameWithSpacesRegex
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

fun String.minRequiredCharacterErrorMessageBuilder(fieldName: Int, characterLimit: Int): FormError?{
    return if(this.length < characterLimit)
        FormError.TooShortData(fieldName, characterLimit)
    else
        null

}

fun String.invalidUserNameErrorMessageBuilder(): FormError? {
    return if (this.matches(userNameWithSpacesRegex))
        null
    else
        FormError.InvalidUsername
}

fun String.invalidEmailErrorMessageBuilder(): FormError? {
    return if(Patterns.EMAIL_ADDRESS.matcher(this).matches() && this.substringAfterLast('.').length >= 2)
        null
    else
        FormError.InvalidEmailFormat
}

fun String.invalidNumericalFieldErrorMessageBuilder(fieldNameRes: Int): FormError? {
    return if(this.any{!it.isDigit()})
        FormError.InvalidNumericalFIeld(fieldNameRes)
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

fun String.amountFieldValidator(regex: Regex): FormError? {

    if (this.isBlank())
        return null

    if (!this.matches(regex)) {
        return if(regex.pattern==depositAmountRegex.pattern)
            FormError.InvalidDepositAmountFormat
            else
            FormError.InvalidCashTransferAmountFormat
    }


    val bigDecimal =
        this.toBigDecimalOrNull()
        ?: return FormError.InvalidAmount

    if (bigDecimal <= BigDecimal.ZERO)
        return FormError.NegativeAmount

    return null
}


