package com.example.bankapp.ui.theme

val userNameWithSpacesRegex = """^\s*(?![0-9])[a-zA-Z0-9][a-zA-Z0-9 ]*\s*$""".toRegex()

val userNameWithEmojiRegex = "^(?!\\p{N})[\\p{L}\\p{N}\\p{So}](?:[\\p{L}\\p{N}\\p{So} ]{0,18}[\\p{L}\\p{N}\\p{So}])?\$".toRegex()
