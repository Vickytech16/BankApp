package com.example.bankapp.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.entities.errors.UiError
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.NegativeAmountErrorMessageBuilder
import com.example.bankapp.utilities.invalidNumericalFieldErrorMessageBuilder
import com.example.bankapp.utilities.toDbAccNo
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

class CashTransferViewModel(
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    private val transactionRepository: TransactionRepository)
    : ViewModel() {

    private val account = sessionState.account

    private val user = sessionState.user
    var accountNumber by mutableStateOf<String>("")
        private set

    var accountNumberError by mutableStateOf<FormError?>(null)
        private set

    fun onAccountNumberChange(newAccountNumber: String){
        accountNumber = newAccountNumber
        accountNumberError = accountNumber.emptyTextFieldErrorMessageBuilder(R.string.account_number) ?:
            accountNumber.invalidNumericalFieldErrorMessageBuilder(R.string.account_number)
        onSubmitErrorReset()
    }


    var amount by mutableStateOf<BigDecimal>(BigDecimal.ZERO)
        private set

    var amountError by mutableStateOf<FormError?>(null)
        private set

    fun onAmountChange(newAmount: String){
        amount = newAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        amountError = newAmount.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name) ?:
                amount.NegativeAmountErrorMessageBuilder()
        onSubmitErrorReset()
    }

    var password by mutableStateOf<String>("")
    private set

    var passwordError by mutableStateOf<FormError?>(value = null)
        private set

    var passwordVisible by mutableStateOf(false)
        private set

    fun onPasswordChange(newPassword: String){
        password = newPassword
        passwordError = password.emptyTextFieldErrorMessageBuilder(R.string.password_field_name)
        onSubmitErrorReset()
    }

    fun onPasswordVisibleChange(){
        passwordVisible = !passwordVisible
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    var transactionResult by mutableStateOf<TransactionResult?>(null)
        private set


    private var idempotencyKey = UUID.randomUUID().toString()

    fun onSubmitErrorReset(){
        if(amountError==null && accountNumberError==null && passwordError==null){
            submitError = null
        }
    }

    var isTransactionSuccess by mutableStateOf(false)
        private set

    fun resetData(){
        onAmountChange("")
        onPasswordChange("")
        onAccountNumberChange("")
       // submitError = null
    }

    var isLoading by mutableStateOf(false)
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmit(){

        if(isLoading)
            return

        isLoading = true

        onAmountChange(amount.toString())
        onPasswordChange(password)
        onAccountNumberChange(accountNumber)

        println("Reached 1")
        onSubmitErrorReset()




            viewModelScope.launch {

                try {
                    if (passwordError != null || amountError != null || accountNumberError != null) {
                        submitError = FormError.InvalidData
                        return@launch
                    }

                    println("Reached 2")
                    if (submitError == null) {
                        if (PasswordHashingService.matches(password, user.passwordHashed)) {

                            println("Reached 3")
                            val receiverAccountNumber = accountNumber.toDbAccNo()
                            transactionResult = transactionRepository.cashTransfer(
                                fromAccountNo = account.accNo,
                                toAccountNo = receiverAccountNumber,
                                amount = amount,
                                idempotencyKey = idempotencyKey
                            )
                            if (transactionResult != TransactionResult.Error.RepeatedTransaction)
                                idempotencyKey = UUID.randomUUID().toString()
                            println(account.balance)
                            println(amount)
                            println("Result is..................$")
                            if (transactionResult is TransactionResult.Success) {
                                isTransactionSuccess = true
                            }
                        } else {
                            submitError = FormError.PasswordDoesntMatch
                        }
                    }

                } catch (e: Exception) {
                    submitError = FormError.UnknownError
                } finally {
                    isLoading = false
                }
            }
    }
}