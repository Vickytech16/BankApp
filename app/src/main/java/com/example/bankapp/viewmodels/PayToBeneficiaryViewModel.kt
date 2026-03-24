package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dtos.BeneficiaryDto
import com.example.bankapp.repositories.BeneficiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PayToBeneficiaryViewModel(
    sessionState: SessionState.Authenticated.AccountRegistered,
    private val beneficiaryRepository: BeneficiaryRepository
) : ViewModel() {
    private val user = sessionState.user

    private val _friends = MutableStateFlow<List<BeneficiaryDto>>(emptyList())
    val friends: StateFlow<List<BeneficiaryDto>> = _friends

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadFriends()
    }

    private fun loadFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true

                val allFriends = beneficiaryRepository.getAllBeneficiariesForUser(user.userId)
                println("DEBUG: Loaded ${allFriends.size} friends")  // ← Add this
                _friends.value = allFriends



            } catch (e: Exception) {
                println("Error: ${e.message}")
                e.printStackTrace()  // ← Full stack trace
            } finally {
                isLoading = false
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}