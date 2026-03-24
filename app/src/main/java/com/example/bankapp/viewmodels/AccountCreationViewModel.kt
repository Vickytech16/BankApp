package com.example.bankapp.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.entities.types.account.AccountType
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.services.PasswordHashingService

import com.example.bankapp.usecases.SessionUseCase
import com.example.bankapp.utilities.PASSWORD_MAX_SIZE
import com.example.bankapp.utilities.amountFieldValidator
import com.example.bankapp.utilities.cashTransferAmountRegex
import com.example.bankapp.utilities.depositAmountRegex
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder

import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder

import kotlinx.coroutines.launch
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

    var amount by mutableStateOf<String>("")
        private set

    var amountError by mutableStateOf<FormError?>(null)
        private set

    fun onAmountChange(newAmount: String){
        if(newAmount.isEmpty() || newAmount.matches(depositAmountRegex))
            amount = newAmount
        amountError =
            newAmount.emptyTextFieldErrorMessageBuilder(R.string.amount_field_name) ?:
                    newAmount.amountFieldValidator(depositAmountRegex)
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

    var isSubmitSuccessful by mutableStateOf(false)
        private set

    fun onSubmitErrorReset(){
        if(amountError==null && passwordError==null)
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

        onAmountChange(amount.toString())
        onPasswordChange(password)

        onSubmitErrorReset()

        viewModelScope.launch {
            try {
                if (passwordError != null || amountError != null) {
                    submitError = FormError.InvalidData
                    return@launch
                }

                val convertedAmount = amount.toBigDecimalOrNull()
                if(convertedAmount==null){
                    amountError = FormError.InvalidAmount
                    submitError = FormError.InvalidData
                    return@launch
                }

                if (submitError == null) {

                    val user = sessionUseCase.getUserFromSharedPreferences()

                    if (user == null) {
                        submitError = FormError.UnknownError
                    }

                    else {
                        if (PasswordHashingService.matches(password, user.passwordHashed)) {
                            isSubmitSuccessful = true
                            val account = Account(
                                userId = user.userId,
                                accountType = accountType,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now()
                            )

                            val accNo =
                                accountRepository.createAccount(account)
                            val result =
                                transactionRepository.deposit(accNo, convertedAmount, idempotencyKey)

                            if (result is TransactionResult.Error.RepeatedTransaction)
                                idempotencyKey = UUID.randomUUID().toString()
                            println("Result is..................${result.message}")
                        }
                        else {
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