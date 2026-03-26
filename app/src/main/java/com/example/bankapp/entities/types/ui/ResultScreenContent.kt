package com.example.bankapp.entities.types.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class ResultContent(
    val text1: UiText? = null,
    val text2: UiText? = null,
    val text3: UiText? = null,
    val text4: UiText? = null,
    val text5: UiText? = null,
    val primaryButton: ResultButton? = null,
    val secondaryButton: ResultButton? = null
)

data class ResultButton(
    val text: UiText,
    val onClick: () -> Unit
)

sealed class UiText {
    data class StringResource(val resId: Int) : UiText()
    data class DynamicString(val value: String) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is StringResource -> stringResource(resId)
            is DynamicString -> value
        }
    }
}