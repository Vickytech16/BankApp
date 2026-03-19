package com.example.bankapp.usecases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import java.math.BigDecimal

sealed class HomeSessionHandler {

    var intent: CurrentSessionIntent? = null


    companion object {
        private val instances = mutableMapOf<CurrentSessionIntent, HomeSessionHandler>()

        fun getInstance(type: CurrentSessionIntent): HomeSessionHandler {
            return instances.getOrPut(type) {
                when (type) {
                    CurrentSessionIntent.CASH_TRANSFER -> CashTransfer()
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

        var onNavigate: (() -> Unit)? = null

        var navigationLocked: Boolean = false

        var isFriend: Boolean = false

        var onTransactionSuccessPrimaryAction: (() -> Unit)? = null

        var onTransactionSuccessSecondaryAction: (() ->Unit)? = null

        var onTransactionFailurePrimaryAction: (() -> Unit)? = null

        var onTransactionFailureSecondaryAction: (() -> Unit)? = null

        fun onInitialize(fromAccNo: Long, toAccNo: Long, amount: BigDecimal, isFriend: Boolean) {
            this.fromAccNo = fromAccNo
            this.toAccNo = toAccNo
            this.amount = amount
            this.isFriend = isFriend
            super.intent = CurrentSessionIntent.CASH_TRANSFER

        }

        fun onDispose() {
            onNavigate = null
            onOtpSuccess = {}
            onOtpDismiss = {}
            onPasswordSuccess = {}
            onPasswordFailure = {}
        }

        fun buildResultContent(
            isSuccess: Boolean,
            reason: String? = null
        ) {
            println("DEBUG: buildResultContent called with isSuccess=$isSuccess")
            resultContent = if (isSuccess) {
                val content = ResultContent(
                    messages = listOf(
                        ResultTextContent(
                            text = UiText.StringResource(R.string.transaction_success),
                            textType = TextType.TITLE
                        ),
                        ResultTextContent(
                            text = UiText.DynamicString(amount.toPlainString()),
                            textType = TextType.SUBTITLE
                        ),
                        ResultTextContent(
                            text = UiText.DynamicString(
                                java.time.LocalDateTime.now().toString()
                            ),
                            textType = TextType.MESSAGE
                        )
                    ),
                    buttons = listOf(
                        ResultButton(
                            text = UiText.StringResource(R.string.done),
                            onClick = {
                                onTransactionSuccessPrimaryAction?.invoke()

                            },
                            role = ResultButtonType.PRIMARY
                        )
                    )
                )
                println("DEBUG: SUCCESS content created with ${content.messages.size} messages")
                content
            }
            else {
                ResultContent(
                    messages = listOf(
                        ResultTextContent(
                            text = UiText.StringResource(R.string.transaction_failed),
                            textType = TextType.TITLE
                        ),
                        ResultTextContent(
                            text = UiText.DynamicString(reason ?: ""),
                            textType = TextType.SUBTITLE
                        )
                    ),
                    buttons = listOf(
                        ResultButton(
                            text = UiText.StringResource(R.string.try_again),
                            onClick = {
                                onTransactionFailurePrimaryAction?.invoke()
                            },
                            role = ResultButtonType.PRIMARY
                        )
                    )
                )

            }
            println("DEBUG: resultContent set to $resultContent")
        }
    }
}

enum class ActionState {
    LOADING,
    SUCCESS,
    FAILURE
}

data class ResultContent(
    val messages: List<ResultTextContent>,
    val buttons: List<ResultButton>
)

data class ResultButton(
    val text: UiText,
    val onClick: () -> Unit,
    val role: ResultButtonType
)

data class ResultTextContent(
    val text: UiText,
    val textType: TextType
)

enum class ResultButtonType() {
    PRIMARY,
    SECONDARY,
    TERTIARY
}

enum class TextType {
    TITLE,
    SUBTITLE,
    MESSAGE
}

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