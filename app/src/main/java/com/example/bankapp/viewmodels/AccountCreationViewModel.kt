package com.example.bankapp.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.Account
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.entities.types.AccountType
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.services.PasswordHashingService

import com.example.bankapp.usecases.SessionUseCase
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.invalidAmountErrorMessageBuilder
import com.example.bankapp.utilities.NegativeAmountErrorMessageBuilder
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class AccountCreationViewModel(
    private val accountRepository: AccountRepository,
    private val sessionUseCase: SessionUseCase,
    private val transactionRepository: TransactionRepository
): ViewModel() {

    var accountType by mutableStateOf(AccountType.SAVINGS)
        private set

    fun onAccountTypeChange(newAccountType: AccountType){
        accountType = newAccountType
    }

    var balance by mutableStateOf<BigDecimal>(BigDecimal.ZERO)
        private set

    var balanceError by mutableStateOf<FormError?>(null)
        private set

    fun onBalanceChange(newBalance: String){
        balance = newBalance.toBigDecimalOrNull() ?: (-1).toBigDecimal()
        balanceError = newBalance.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name) ?:
                       newBalance.invalidAmountErrorMessageBuilder() ?: balance.NegativeAmountErrorMessageBuilder()
        onSubmitErrorReset()
    }

    var password by mutableStateOf("")
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

    var isSubmitSuccessful by mutableStateOf(false)
        private set

    fun onSubmitErrorReset(){
        if(balanceError==null && passwordError==null)
            submitError = null
    }

    var isLoading by mutableStateOf(false)
        private set

    private var idempotencyKey = UUID.randomUUID().toString()

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmit(){

        if(isLoading)
            return

        isLoading = true

        onBalanceChange(balance.toString())
        onPasswordChange(password)

        onSubmitErrorReset()


            viewModelScope.launch {
                try {
                    if (passwordError != null || balanceError != null) {
                        submitError = FormError.InvalidData
                        return@launch
                    }
                    if (submitError == null) {

                        val user = sessionUseCase.getUserFromSharedPreferences()
                        if (user == null) {
                            submitError = FormError.UnknownError
                        } else {
                            if (PasswordHashingService.matches(password, user.passwordHashed)) {
                                isSubmitSuccessful = true
                                val account = Account(
                                    userId = user.userId,
                                    accountType = accountType,
                                    createdAt = LocalDateTime.now(),
                                    updatedAt = LocalDateTime.now()
                                )
                                val accNo = accountRepository.createAccount(account)
                                val result =
                                    transactionRepository.deposit(accNo, balance, idempotencyKey)
                                if (result is TransactionResult.Error.RepeatedTransaction)
                                    idempotencyKey = UUID.randomUUID().toString()
                                println("Result is..................${result.message}")
                            } else {
                                passwordError = FormError.PasswordDoesntMatch
                                submitError = FormError.PasswordDoesntMatch
                            }
                        }
                    }

                } catch (_: Exception) {
                    submitError = FormError.UnknownError
                } finally {
                    isLoading = false
                }
            }
    }
}