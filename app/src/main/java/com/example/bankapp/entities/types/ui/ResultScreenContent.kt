package com.example.bankapp.entities.types.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class ResultContent(
    val text1: ResultUiText? = null,
    val text2: ResultUiText? = null,
    val text3: ResultUiText? = null,
    val text4: ResultUiText? = null,
    val text5: ResultUiText? = null,
    val primaryButton: ResultButton? = null,
    val secondaryButton: ResultButton? = null
)

data class ResultButton(
    val text: ResultUiText,
    val onClick: () -> Unit
)

sealed class ResultUiText {
    data class StringResource(val resId: Int) : ResultUiText()
    data class DynamicString(val value: String) : ResultUiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is StringResource -> stringResource(resId)
            is DynamicString -> value
        }
    }
}