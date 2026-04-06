package com.example.bankapp.utilities

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyUtils {
    private val formatter = DecimalFormat("#,##0.00")
    fun getCurrencySymbol(countryCode: String): String {
        return try {
            val locale = Locale.Builder()
                .setRegion(countryCode.uppercase())
                .build()
            Currency.getInstance(locale).symbol
        } catch (e: Exception) {
            "$"
        }
    }

    fun getCurrencyCode(countryCode: String): String {
        return try {
            val locale = Locale.Builder()
                .setRegion(countryCode.uppercase())
                .build()
            Currency.getInstance(locale).currencyCode
        } catch (_: Exception) {
            "USD"
        }
    }

    fun formatDecimal(amount: BigDecimal): String {
        val genericFormatter = DecimalFormat("#,##0.00")
        return try {
            genericFormatter.format(amount)
        } catch (e: Exception) {
            amount.toPlainString()
        }
    }

    fun convertCurrency(
        amount: String,
        rates: Map<String, Double>?,
        baseCountryCode: String,
        targetCountryCode: String
    ): String {
        val numericAmount = amount.toDoubleOrNull() ?: 0.0
        if (rates == null || numericAmount == 0.0) return "0.00"

        val baseCurrency = getCurrencyCode(baseCountryCode)
        val targetCurrency = getCurrencyCode(targetCountryCode)

        val baseRate = rates[baseCurrency] ?: 1.0
        val targetRate = rates[targetCurrency] ?: 1.0
        println("Base: $baseCountryCode ($baseRate), Target: $targetCountryCode ($targetRate)")

        val result = (numericAmount / baseRate) * targetRate

        return formatter.format(result)
    }


    fun formatCurrency(amount: BigDecimal, countryCode: String): String {
        val locale = Locale.Builder().setRegion(countryCode.uppercase()).build()
        val formatter = NumberFormat.getCurrencyInstance(locale)

        val decimalFormat = formatter as DecimalFormat
        val symbols = decimalFormat.decimalFormatSymbols
        symbols.currencySymbol = ""
        decimalFormat.decimalFormatSymbols = symbols

        return decimalFormat.format(amount).trim()
    }
}
