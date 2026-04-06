package com.example.bankapp.utilities

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.NumberParseException

object PhoneUtils {
    private val phoneUtil = PhoneNumberUtil.getInstance()

    fun isValidMobileNumber(number: String, countryIso: String): Boolean {
        return try {
            val numberProto = phoneUtil.parse(number, countryIso)
            phoneUtil.isValidNumber(numberProto) &&
                    phoneUtil.getNumberType(numberProto) == PhoneNumberUtil.PhoneNumberType.MOBILE
        } catch (e: NumberParseException) {
            false
        }
    }

    fun formatAsYouType(number: String, countryIso: String): String {
        val formatter = phoneUtil.getAsYouTypeFormatter(countryIso)
        var result = ""
        number.forEach { char ->
            result = formatter.inputDigit(char)
        }
        return result
    }
}