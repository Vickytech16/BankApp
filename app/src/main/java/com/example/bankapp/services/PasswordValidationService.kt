package com.example.bankapp.services

import com.example.bankapp.entities.errors.PasswordError

object PasswordValidationService {
    fun validatePassword(password: String): List<PasswordError>{

            val passwordErrors = mutableListOf<PasswordError>()

            if(password.length < 8)   passwordErrors.add(PasswordError.PasswordTooShort)
            if(password.none {it.isUpperCase()}) passwordErrors.add(PasswordError.PasswordMissingUppercase)
            if(password.none {it.isLowerCase()})  passwordErrors.add(PasswordError.PasswordMissingLowercase)
            if(password.none {it.isDigit()})  passwordErrors.add(PasswordError.PasswordMissingDigit)
            if(password.none {!it.isLetterOrDigit()})  passwordErrors.add(PasswordError.PasswordMissingSpecialCharacter)
            if(password.any {it.isWhitespace()}) passwordErrors.add(PasswordError.PasswordHasWhitespace)

            return passwordErrors
    }
}