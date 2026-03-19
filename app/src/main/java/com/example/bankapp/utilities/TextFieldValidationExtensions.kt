package com.example.bankapp.utilities

import android.util.Patterns
import com.example.bankapp.services.PasswordValidationService
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.PasswordError
import java.math.BigDecimal

val amountRegex = Regex("^(0|[1-9]\\d{0,7})(\\.\\d{0,2})?$")

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




fun String.invalidPasswordErrorMessageBuilder(): List<PasswordError> {
    return PasswordValidationService.validatePassword(this)
}

fun String.invalidConfirmPasswordErrorMessageBuilder(password: String): FormError? {
    return if(this!=password)
        FormError.PasswordDoesntMatch
    else
        null
}

fun String.amountFieldValidator(): FormError? {

    if (this.isBlank())
        return null

    if (!this.matches(amountRegex))
        return FormError.InvalidAmountFormat

    val bigDecimal =
        this.toBigDecimalOrNull()
        ?: return FormError.InvalidAmount

    if (bigDecimal <= BigDecimal.ZERO)
        return FormError.NegativeAmount

    return null
}


