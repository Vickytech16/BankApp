package com.example.bankapp.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.usecases.CurrentTransactionStatus
import com.example.bankapp.usecases.TransactionSessionHolder
import kotlinx.coroutines.launch

class AddBeneficiaryResultViewModel(
    private val transactionSessionHolder: TransactionSessionHolder,
    private val beneficiaryRepository: BeneficiaryRepository
) : ViewModel() {

    init {
        transactionSessionHolder.onCurrentTransactionStatusChange(CurrentTransactionStatus.LOADING)
        addBeneficiary()
    }

    var isBeneficiaryAdded by mutableStateOf(false)
        private set

    fun addBeneficiary() {
        viewModelScope.launch {
            try {
                val currentUser = transactionSessionHolder.currentUser
                val friend = transactionSessionHolder.friend

                if (currentUser == null || friend == null) {
                    transactionSessionHolder.failureMessage = "Missing user information"
                    transactionSessionHolder.onCurrentTransactionStatusChange(CurrentTransactionStatus.FAILURE)
                    return@launch
                }

                beneficiaryRepository.addBeneficiary(
                    userId = currentUser.userId,
                    beneficiaryUserId = friend.userId,
                    nickname = ""
                )

                transactionSessionHolder.successMessage = "${friend.userName} added as beneficiary"
                transactionSessionHolder.onCurrentTransactionStatusChange(CurrentTransactionStatus.SUCCESS)
                isBeneficiaryAdded = true

            } catch (e: Exception) {
                transactionSessionHolder.failureMessage = e.message ?: "Failed to add beneficiary"
                transactionSessionHolder.onCurrentTransactionStatusChange(CurrentTransactionStatus.FAILURE)
            }
        }
    }
}