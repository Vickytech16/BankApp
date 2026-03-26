package com.example.bankapp.usecases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import com.example.bankapp.entities.types.ActionState
import java.math.BigDecimal

sealed class HomeSessionHandler {
    var intent: CurrentSessionIntent? = null

    companion object {
        private val instances = mutableMapOf<CurrentSessionIntent, HomeSessionHandler>()

        fun getInstance(type: CurrentSessionIntent): HomeSessionHandler {
            return instances.getOrPut(type) {
                when (type) {
                    CurrentSessionIntent.CASH_TRANSFER -> CashTransfer()
                    CurrentSessionIntent.DEPOSIT -> Deposit()
                    else -> CashTransfer()
                }
            }
        }
    }

    var actionState by mutableStateOf(ActionState.LOADING)
        private set

    fun onActionStateChange(newValue: ActionState){
        actionState = newValue
    }

    var onOtpSuccess: (() -> Unit) = {}
    var onOtpDismiss: (() -> Unit) = {}
    var onPasswordSuccess: (() -> Unit) = {}

    var onPasswordFailure: (() -> Unit) = {}

    var resultContent by mutableStateOf<ResultContent?>(null)

    class CashTransfer: HomeSessionHandler() {

        var transactionId: String? = null
        var fromAccNo: Long = 0
        var toAccNo: Long = 0

        var amount: BigDecimal = BigDecimal.ZERO

        var navigationLocked: Boolean = false

        var isFriend: Boolean = false

        var onActionSuccessPrimaryAction: (() -> Unit)? = null

        var onActionFailurePrimaryAction: (() -> Unit)? = null

        fun onInitialize(fromAccNo: Long, toAccNo: Long, amount: BigDecimal, isFriend: Boolean) {
            this.fromAccNo = fromAccNo
            this.toAccNo = toAccNo
            this.amount = amount
            this.isFriend = isFriend
            super.intent = CurrentSessionIntent.CASH_TRANSFER

        }

        fun buildResultContent(
            isSuccess: Boolean,
            reason: String? = null
        ) {
            resultContent = if (isSuccess) {
                ResultContent(
                    text1 = UiText.StringResource(R.string.transaction_success),
                    text2 = UiText.DynamicString(amount.toPlainString()),
                    text3 = UiText.DynamicString(java.time.LocalDateTime.now().toString()),
                    primaryButton = ResultButton(
                        text = UiText.StringResource(R.string.done),
                        onClick = {
                            onActionSuccessPrimaryAction?.invoke()
                        }
                    ),
                )
            }
            else {
                ResultContent(
                    text1 = UiText.StringResource(R.string.transaction_failed),
                    text2 = UiText.DynamicString(reason ?: ""),
                    primaryButton = ResultButton(
                        text = UiText.StringResource(R.string.try_again),
                        onClick = {
                            onActionFailurePrimaryAction?.invoke()
                        }
                    ),
                )
            }
        }
    }

    class Deposit: HomeSessionHandler(){

        var transactionId: String? = null

        var userAccNo: Long = 0

        var userId: Long = 0

        var amount: BigDecimal = BigDecimal.ZERO


        var onActionSuccessPrimaryAction: (() -> Unit)? = null

        var onActionFailurePrimaryAction: (() -> Unit)? = null

        fun onInitialize(userAccNo: Long, userId: Long, amount: BigDecimal){
            this.userAccNo = userAccNo
            this.userId = userId
            this.amount = amount
        }

        fun buildResultContent(
            isSuccess: Boolean,
        ) {
            resultContent = if (isSuccess) {
                ResultContent(
                    text1 = UiText.StringResource(R.string.transaction_success),
                    text2 = UiText.DynamicString(amount.toPlainString()),
                    text3 = UiText.DynamicString(java.time.LocalDateTime.now().toString()),
                    primaryButton = ResultButton(
                        text = UiText.StringResource(R.string.done),
                        onClick = {
                            onActionSuccessPrimaryAction?.invoke()
                        }
                    ),
                )
            }
            else {
                ResultContent(
                    text1 = UiText.StringResource(R.string.transaction_failed),
                    text2 = UiText.DynamicString(java.time.LocalDateTime.now().toString()),
                    primaryButton = ResultButton(
                        text = UiText.StringResource(R.string.try_again),
                        onClick = {
                            onActionFailurePrimaryAction?.invoke()
                        }
                    ),
                )
            }
        }
    }

}

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