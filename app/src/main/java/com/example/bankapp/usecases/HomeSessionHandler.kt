package com.example.bankapp.usecases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.Beneficiary
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
                    CurrentSessionIntent.BENEFICIARY_ADDITION -> AddBeneficiary()
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

        var onActionSuccessPrimaryAction: (() -> Unit)? = null

        var onActionSuccessSecondaryAction: (() ->Unit)? = null

        var onActionFailurePrimaryAction: (() -> Unit)? = null

        var onActionFailureSecondaryAction: (() -> Unit)? = null

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
            resultContent = if (isSuccess) {
                ResultContent(
                    text1 = UiText.StringResource(R.string.transaction_success),
                    text2 = UiText.DynamicString(amount.toPlainString()),
                    text3 = UiText.DynamicString(java.time.LocalDateTime.now().toString()),
                    text4 = null,
                    text5 = null,
                    primaryButton = ResultButton(
                        text = UiText.StringResource(R.string.done),
                        onClick = {
                            onActionSuccessPrimaryAction?.invoke()
                        }
                    ),
                    secondaryButton = null
                )
            }
            else {
                ResultContent(
                    text1 = UiText.StringResource(R.string.transaction_failed),
                    text2 = UiText.DynamicString(reason ?: ""),
                    text3 = null,
                    text4 = null,
                    text5 = null,
                    primaryButton = ResultButton(
                        text = UiText.StringResource(R.string.try_again),
                        onClick = {
                            onActionFailurePrimaryAction?.invoke()
                        }
                    ),
                    secondaryButton = null
                )
            }
        }
    }

    class Deposit: HomeSessionHandler(){

        var transactionId: String? = null

        var userAccNo: Long = 0

        var userId: Long = 0

        var amount: BigDecimal = BigDecimal.ZERO

        var navigationLocked: Boolean = false

        var onActionSuccessPrimaryAction: (() -> Unit)? = null

        var onTransactionSuccessSecondaryAction: (() ->Unit)? = null

        var onActionFailurePrimaryAction: (() -> Unit)? = null

        var onTransactionFailureSecondaryAction: (() -> Unit)? = null

        fun onInitialize(userAccNo: Long, userId: Long, amount: BigDecimal){
            this.userAccNo = userAccNo
            this.userId = userId
            this.amount = amount
        }

        fun onDispose(){

        }

        fun buildResultContent(
            isSuccess: Boolean,
        ) {
            resultContent = if (isSuccess) {
                ResultContent(
                    text1 = UiText.StringResource(R.string.transaction_success),
                    text2 = UiText.DynamicString(amount.toPlainString()),
                    text3 = UiText.DynamicString(java.time.LocalDateTime.now().toString()),
                    text4 = null,
                    text5 = null,
                    primaryButton = ResultButton(
                        text = UiText.StringResource(R.string.done),
                        onClick = {
                            onActionSuccessPrimaryAction?.invoke()
                        }
                    ),
                    secondaryButton = null
                )
            }
            else {
                ResultContent(
                    text1 = UiText.StringResource(R.string.transaction_failed),
                    text2 = UiText.DynamicString(java.time.LocalDateTime.now().toString()),
                    text3 = null,
                    text4 = null,
                    text5 = null,
                    primaryButton = ResultButton(
                        text = UiText.StringResource(R.string.try_again),
                        onClick = {
                            onActionFailurePrimaryAction?.invoke()
                        }
                    ),
                    secondaryButton = null
                )
            }
        }
    }

    class AddBeneficiary: HomeSessionHandler(){
        var currentUserId: Long = 0

        var otherUserId: Long = 0

        var nickname: String = ""

        fun onInitialize(currentUserId: Long, otherUserId: Long, nickname: String){
            this.currentUserId = currentUserId
            this.otherUserId = otherUserId
            this.nickname = nickname
        }

        var onActionSuccessPrimaryAction: (() -> Unit)? = null

        var onTransactionSuccessSecondaryAction: (() ->Unit)? = null

        var onActionFailurePrimaryAction: (() -> Unit)? = null

        var onTransactionFailureSecondaryAction: (() -> Unit)? = null

        fun buildContent(isSuccess: Boolean){
            resultContent =
                if(isSuccess){
                    ResultContent(
                        text1 = UiText.StringResource(R.string.beneficiary_added_successfully),
                        primaryButton = ResultButton(UiText.StringResource(R.string.done),
                            { onActionSuccessPrimaryAction?.invoke() })
                    )
                 }
                else{
                    ResultContent(
                        text1 = UiText.StringResource(R.string.failed_label),
                        primaryButton = ResultButton(UiText.StringResource(R.string.try_again),
                            {onActionFailurePrimaryAction?.invoke()})
                    )
                }
        }
    }
}

enum class ActionState {
    LOADING,
    SUCCESS,
    FAILURE
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