import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.AuthorizationIntent
import com.example.bankapp.entities.types.ActionState
import com.example.bankapp.entities.types.ui.ResultButton
import com.example.bankapp.entities.types.ui.ResultContent
import com.example.bankapp.entities.types.ui.ResultUiText
import com.example.bankapp.ui.components.navigators.HOME_ROUTE
import com.example.bankapp.ui.components.navigators.PASSWORD_CONFIRMATION_ROUTE
import com.example.bankapp.ui.components.navigators.TRANSACTION_RESULT_ROUTE
import com.example.bankapp.utilities.CurrencyUtils
import java.math.BigDecimal

class AuthorizationViewModel : ViewModel() {

    var authorizationIntent by mutableStateOf<AuthorizationIntent?>(null)
        private set

    var actionState by mutableStateOf(ActionState.LOADING)

    fun initializeCashTransfer(fromAccNo: Long, toAccNo: Long, amount: BigDecimal, friend: Boolean, countryCode: String) {
        authorizationIntent = AuthorizationIntent.CashTransfer(
            fromAccNo = fromAccNo,
            toAccNo = toAccNo,
            amount = amount,
            isFriend = friend,
            countryCode = countryCode
        )
    }

    fun initializeAddBeneficiary(fromUserId: Long, toUserId: Long, nickname: String) {
        authorizationIntent = AuthorizationIntent.AddBeneficiary(
            fromUserId,
            toUserId,
            nickname
        )
    }

    fun initializeDeposit(accNo: Long, amount: BigDecimal, countryCode: String) {
        authorizationIntent = AuthorizationIntent.Deposit(
            userAccNo = accNo,
            amount = amount,
            countryCode = countryCode
        )
    }

    fun initializeInternationalTransfer(fromAccNo: Long, toAccNo: Long, amount: BigDecimal, baseCurrency: String, targetCurrency: String, rate: BigDecimal) {
        authorizationIntent = AuthorizationIntent.InternationalTransfer(
            fromAccNo,
            toAccNo,
            amount,
            baseCurrency,
            targetCurrency,
            rate
        )
    }

    fun assignTransactionId(transactionId: String){
        when(authorizationIntent) {
          is AuthorizationIntent.CashTransfer -> {
                authorizationIntent = (authorizationIntent as AuthorizationIntent.CashTransfer).copy(transactionId = transactionId)
            }
          is AuthorizationIntent.Deposit -> {
                authorizationIntent = (authorizationIntent as AuthorizationIntent.Deposit).copy(transactionId = transactionId)
          }
          is AuthorizationIntent.InternationalTransfer -> {
              authorizationIntent = (authorizationIntent as AuthorizationIntent.InternationalTransfer).copy(transactionId = transactionId)
          }
            else -> {}
        }
    }

    fun proceedAfterOtp(navController: NavController) {
        when (authorizationIntent) {
            is AuthorizationIntent.CashTransfer  -> {
                if ((authorizationIntent as AuthorizationIntent.CashTransfer).isFriend) {
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

        when(authorizationIntent) {
            is AuthorizationIntent.CashTransfer -> {
                successMessage = R.string.transaction_success
                val  authorizationIntent = authorizationIntent as AuthorizationIntent.CashTransfer
            return if (isSuccess) {
                ResultContent(
                    text1 = ResultUiText.StringResource(successMessage),
                    text2 = ResultUiText.DynamicString(CurrencyUtils.formatCurrency(authorizationIntent.amount, authorizationIntent.countryCode) + " " + CurrencyUtils.getCurrencySymbol(authorizationIntent.countryCode) ),
                    text3 = ResultUiText.DynamicString(BankDateFactory.now().toFullDateTimeDisplay()),
                    primaryButton = ResultButton(ResultUiText.StringResource(R.string.done), onDone)
                )
              }    else {
                ResultContent(
                    text1 = ResultUiText.StringResource(R.string.transaction_failed),
                    text2 = ResultUiText.StringResource(failureReason),
                    text3 = ResultUiText.DynamicString(BankDateFactory.now().toFullDateTimeDisplay()),
                    primaryButton = ResultButton(ResultUiText.StringResource(R.string.try_again), onRetry)
                )
            }
        }
            is AuthorizationIntent.AddBeneficiary -> {
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
            is AuthorizationIntent.Deposit -> {
                val authorizationIntent = authorizationIntent as AuthorizationIntent.Deposit
                successMessage = R.string.transaction_success
               return if (isSuccess) {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.transaction_success),
                        text2 = ResultUiText.DynamicString(CurrencyUtils.formatCurrency(authorizationIntent.amount, authorizationIntent.countryCode) +" "+ CurrencyUtils.getCurrencySymbol(authorizationIntent.countryCode) ),
                        text3 = ResultUiText.DynamicString(BankDateFactory.now().toFullDateTimeDisplay()),
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

            is AuthorizationIntent.InternationalTransfer ->
            {
                val data = authorizationIntent as AuthorizationIntent.InternationalTransfer
                val converted = data.baseAmount.multiply(data.exchangeRate)
                return if (isSuccess) {
                    ResultContent(
                        text1 = ResultUiText.StringResource(R.string.transaction_success),
                        text2 = ResultUiText.DynamicString("${data.baseAmount} ${data.baseCurrency} -> $converted ${data.targetCurrency}"),
                        text3 = ResultUiText.DynamicString(BankDateFactory.now().toFullDateTimeDisplay()),
                        primaryButton = ResultButton(
                            ResultUiText.StringResource(R.string.done),
                            onDone
                        )
                    )
                }
                else{
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

