package com.example.bankapp.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.utilities.PASSWORD_MAX_SIZE
import com.example.bankapp.utilities.amountFieldValidator
import com.example.bankapp.utilities.amountRegex

import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

class DepositViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered,
    private val transactionRepository: TransactionRepository
): ViewModel() {

    private val account = sessionState.account
    private val user = sessionState.user

    var amount by mutableStateOf<String>("")
        private set

    var amountError by mutableStateOf<FormError?>(null)
        private set

    fun onAmountChange(newAmount: String){
        if(newAmount.isEmpty() || newAmount.matches(amountRegex))
            amount = newAmount
        amountError =
            newAmount.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name) ?:
            newAmount.amountFieldValidator()
        onSubmitErrorReset()
    }

    var password by mutableStateOf("")
        private set

    var passwordError by mutableStateOf<FormError?>(value = null)
        private set

    var passwordVisible by mutableStateOf(false)
        private set

    fun onPasswordChange(newPassword: String){
        if (newPassword.length <= PASSWORD_MAX_SIZE)
            password = newPassword
        passwordError =
                    newPassword.emptyTextFieldErrorMessageBuilder(R.string.password_field_name) ?:
                    newPassword.maxAllowedCharacterErrorMessageBuilder(R.string.password_field_name, PASSWORD_MAX_SIZE)
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
        if(amountError==null && passwordError==null){
            submitError = null
        }
    }

    var isLoading by mutableStateOf(false)
        private set

    fun resetUponSuccess(){
        onAmountChange("")
        onPasswordChange("")
        passwordError = null
        amountError = null
        submitError = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmit() {

        if (isLoading)
            return

        onAmountChange(amount)
        onPasswordChange(password)

        onSubmitErrorReset()

        viewModelScope.launch(Dispatchers.IO) {

            try {
                isLoading = true
                if(amountError!=null || passwordError!=null){
                    submitError = FormError.InvalidData
                    return@launch
                }

                val convertedAmount = amount.toBigDecimalOrNull()
                if(convertedAmount==null){
                    amountError = FormError.InvalidAmount
                    submitError = FormError.InvalidData
                    return@launch
                }

                if(submitError==null){
                    if(PasswordHashingService.matches(password, user.passwordHashed))
                    {
                        transactionResult = transactionRepository.deposit(
                            accountNo = account.accNo,
                            amount = convertedAmount,
                            idempotencyKey = idempotencyKey
                        )
                        if (transactionResult != TransactionResult.Error.RepeatedTransaction)
                            idempotencyKey = UUID.randomUUID().toString()
                        if(transactionResult is TransactionResult.Success)
                            resetUponSuccess()

                    }
                    else{
                        submitError = FormError.PasswordDoesntMatch
                        return@launch
                    }
                }
            }
            catch (e: Exception){
                println("Deposit error: ${e.message}")
                submitError = FormError.UnknownError
            }
            finally {
                isLoading = false
            }

        }
    }
}