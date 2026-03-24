package com.example.bankapp.temp

/*
object AccountNumberFieldStrategy : FieldTypeStrategy {

    override fun getKeyboardOptions(): KeyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.NumberPassword)

    override fun getLetterSpacing(): Int = 2

    override fun validateValue(value: String): String {
        val digitsOnly = value.replace(" ", "")
        return if (digitsOnly.length <= ACCOUNT_NUMBER_SIZE) {
            formatAccountNumber(digitsOnly)
        } else {
            formatAccountNumber(digitsOnly.take(ACCOUNT_NUMBER_SIZE))
        }
    }

   fun formatAndPositionCursor(
        currentText: String,
        cursorPosition: Int
    ): Pair<String, Int> {
        val digitsOnly = currentText.replace(" ", "")

        if (digitsOnly.length <= ACCOUNT_NUMBER_SIZE) {
            val formatted = formatAccountNumber(digitsOnly)

            val digitsBeforeCursor = formatted
                .take(cursorPosition)
                .replace(" ", "")
                .length

            var digitCount = 0
            for (i in formatted.indices) {
                if (formatted[i].isDigit()) {
                    digitCount++
                    if (digitCount == digitsBeforeCursor + 1) {
                        return Pair(formatted, i + 1)
                    }
                }
            }

            return Pair(formatted, minOf(cursorPosition + 1, formatted.length))
        }

        val formatted = formatAccountNumber(digitsOnly.take(ACCOUNT_NUMBER_SIZE))
        return Pair(formatted, minOf(cursorPosition, formatted.length))
    }

    private fun formatAccountNumber(accountNumber: String): String {
        return accountNumber
            .take(ACCOUNT_NUMBER_SIZE)
            .chunked(4)
            .joinToString(" ")
    }

    override fun getMaxLength(): Int = ACCOUNT_NUMBER_SIZE + 2

    override fun getLeadingIcon(): ImageVector = Icons.Outlined.AccountBox
}
 */