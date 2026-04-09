package com.example.bankapp.utilities

import android.util.Patterns
import com.example.bankapp.services.PasswordValidationService
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.PasswordError
import com.example.bankapp.ui.theme.emailRegex
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
    return if(this.length>= 1 && this.first().isDigit())
        FormError.UserNameCannotStartWithNumber
     else if (this.matches(userNameWithSpacesRegex))
        null
    else
        FormError.InvalidUsername
}

fun String.invalidEmailErrorMessageBuilder(): FormError? {
        val email = this.trim()

        if (email.isBlank() || email.length > 320) return FormError.InvalidEmailFormat
        if (!email.contains("@") || !email.contains(".")) return FormError.InvalidEmailFormat

        if (!email.first().isLetterOrDigit()) return FormError.InvalidEmailFormat

        val lastDotIndex = email.lastIndexOf('.')
        val charsAfterDot = email.length - (lastDotIndex + 1)
        if (lastDotIndex == -1 || charsAfterDot < 2) return FormError.InvalidEmailFormat

        val tld = email.substring(lastDotIndex + 1)
        if (!tld.all { it.isLetter() }) return FormError.InvalidEmailFormat

        if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return null
        else
            return FormError.InvalidEmailFormat
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


