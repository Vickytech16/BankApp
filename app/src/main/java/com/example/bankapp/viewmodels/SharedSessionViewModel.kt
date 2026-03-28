import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.types.ActionState
import com.example.bankapp.entities.types.ui.ResultButton
import com.example.bankapp.entities.types.ui.ResultContent
import com.example.bankapp.entities.types.ui.ResultUiText
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PASSWORD_CONFIRMATION_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTION_RESULT_ROUTE
import java.math.BigDecimal


class SharedTransactionViewModel : ViewModel() {

    var flowData by mutableStateOf<FlowData?>(null)
        private set

    var actionState by mutableStateOf(ActionState.LOADING)

    fun initializeCashTransfer(fromAccNo: Long, toAccNo: Long, amount: BigDecimal, friend: Boolean) {
        flowData = FlowData.CashTransfer(
            fromAccNo = fromAccNo,
            toAccNo = toAccNo,
            amount = amount,
            isFriend = friend
        )
    }

    fun initializeAddBeneficiary(fromUserId: Long, toUserId: Long, nickname: String) {
        flowData = FlowData.AddBeneficiary(
            fromUserId,
            toUserId,
            nickname
        )
    }

    fun initializeDeposit(accNo: Long, amount: BigDecimal) {
        flowData = FlowData.Deposit(
            accNo,
            amount
        )
    }

    fun assignTransactionId(transactionId: String){
        when(flowData) {
          is FlowData.CashTransfer -> {
                flowData = (flowData as FlowData.CashTransfer).copy(transactionId = transactionId)
            }
          is FlowData.Deposit -> {
                flowData = (flowData as FlowData.Deposit).copy(transactionId = transactionId)
          }
            else -> {
            }
        }
    }

    fun proceedAfterOtp(navController: NavController) {
        when (flowData) {
            is FlowData.CashTransfer  -> {
                if ((flowData as FlowData.CashTransfer).isFriend) {
                    navController.navigate(TRANSACTION_RESULT_ROUTE)
                } else {
                    navController.navigate("$PASSWORD_CONFIRMATION_ROUTE/$HOME_ROUTE")
                }
            }
            else -> navController.navigate("$PASSWORD_CONFIRMATION_ROUTE/$HOME_ROUTE")
        }
    }

    fun proceedAfterPassword(navController: NavController) {
        navController.navigate(TRANSACTION_RESULT_ROUTE)
    }

    var failureReason by mutableStateOf(R.string.action_failed)

    var successMessage by mutableStateOf(R.string.action_success)

    fun getResultContent(onDone: () -> Unit, onRetry: () -> Unit): ResultContent {
        val isSuccess = actionState == ActionState.SUCCESS

        when(flowData) {
            is FlowData.CashTransfer -> {
                successMessage = R.string.transaction_success
            return if (isSuccess) {
                ResultContent(
                    text1 = ResultUiText.StringResource(successMessage),
                    text2 = ResultUiText.DynamicString((flowData as FlowData.CashTransfer).amount.toString()),
                    text3 = ResultUiText.DynamicString(BankDateFactory.now().toString()),
                    primaryButton = ResultButton(ResultUiText.StringResource(R.string.done), onDone)
                )
              }    else {
                ResultContent(
                    text1 = ResultUiText.StringResource(R.string.transaction_failed),
                    text2 = ResultUiText.StringResource(failureReason),
                    text3 = ResultUiText.DynamicString(BankDateFactory.now().toString()),
                    primaryButton = ResultButton(ResultUiText.StringResource(R.string.try_again), onRetry)
                )
            }
        }
            is FlowData.AddBeneficiary -> {
                successMessage = R.string.beneficiary_added_successfully
             return  if(isSuccess){
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.beneficiary_added_successfully),
                        primaryButton = ResultButton(ResultUiText.StringResource(R.string.done), onClick =  onDone)
                    )
                }   else{
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.failed_label),
                        primaryButton = ResultButton(ResultUiText.StringResource(R.string.try_again), onClick = onRetry)
                    )
                }
            }
            is FlowData.Deposit -> {
                successMessage = R.string.transaction_success
               return if (isSuccess) {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.transaction_success),
                        text2 = ResultUiText.DynamicString((flowData as FlowData.Deposit).amount.toString()),
                        text3 = ResultUiText.DynamicString(BankDateFactory.now().toString()),
                        primaryButton = ResultButton(text = ResultUiText.StringResource(R.string.done), onDone),
                    )
                }
                else {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.transaction_failed),
                        text2 = ResultUiText.StringResource(failureReason),
                        primaryButton = ResultButton(text = ResultUiText.StringResource(R.string.try_again), onClick = onRetry),
                    )
                }
            }
            else ->
           return ResultContent(
                text1 = ResultUiText.StringResource(R.string.transaction_failed),
                text2 = ResultUiText.StringResource(failureReason),
                primaryButton = ResultButton(ResultUiText.StringResource(R.string.try_again), onRetry)
            )
        }
    }
}

sealed interface FlowData {
    data class CashTransfer(
        val fromAccNo: Long,
        val toAccNo: Long,
        val amount: BigDecimal,
        val isFriend: Boolean,
        val transactionId: String? = null
    ) : FlowData

    data class Deposit(
        val userAccNo: Long,
        val amount: BigDecimal,
        val transactionId: String? = null
    ) : FlowData

    data class AddBeneficiary(
        val myUserId: Long,
        val otherUserId: Long,
        val nickname: String
    ) : FlowData

}