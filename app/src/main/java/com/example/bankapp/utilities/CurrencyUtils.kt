package com.example.bankapp.utilities

import java.math.BigDecimal
import java.math.RoundingMode
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
        amount: BigDecimal,
        rates: Map<String, Double>?,
        baseCountryCode: String,
        targetCountryCode: String
    ): BigDecimal {
        if (rates == null || amount.signum() == 0) return 0.00.toBigDecimal()

        val baseCurrency = getCurrencyCode(baseCountryCode)
        val targetCurrency = getCurrencyCode(targetCountryCode)

        val baseRate = BigDecimal.valueOf(rates[baseCurrency] ?: 1.0)
        val targetRate = BigDecimal.valueOf(rates[targetCurrency] ?: 1.0)

        if (baseRate.signum() == 0) return 0.00.toBigDecimal()
        val amountInBase = amount.divide(baseRate, 8, RoundingMode.HALF_UP)

        val result = amountInBase.multiply(targetRate)
        return result
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
