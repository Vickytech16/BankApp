package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dbtables.Beneficiary
import com.example.bankapp.entities.dtos.BeneficiaryDto
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.repositories.BeneficiaryRepository
import com.example.bankapp.utilities.USERNAME_MAX_SIZE
import com.example.bankapp.utilities.invalidUserNameErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManageBeneficiaryViewModel(
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
                val allFriends = beneficiaryRepository.getAllBeneficiariesForUser(user.value.userId)
                _friends.value = allFriends
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    var showDeleteDialog by mutableStateOf(false)
        private set

    fun onShowDeleteDialogChange(newValue: Boolean) {
        showDeleteDialog = newValue
    }

    var showEditDialog by mutableStateOf(false)
        private set

    fun onShowEditDialogChange(newValue: Boolean) {
        showEditDialog = newValue
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    var nickname by mutableStateOf<String?>("")
        private set

    var nicknameError by mutableStateOf<FormError?>(null)
        private set

    var selectedFriend by mutableStateOf<BeneficiaryDto?>(null)
        private set

    fun onSelectFriend(friend: BeneficiaryDto) {
        selectedFriend = friend
        nickname = friend.beneficiaryName
    }

    fun onNicknameChange(newNickname: String) {
        if (newNickname.length <= USERNAME_MAX_SIZE) {
            nickname = newNickname
        }
        nicknameError = newNickname.maxAllowedCharacterErrorMessageBuilder(
            R.string.nickname_field_name,
            USERNAME_MAX_SIZE
        ) ?: newNickname.invalidUserNameErrorMessageBuilder()
    }

    fun resetNicknameState() {
        nickname = ""
        nicknameError = null
        submitError = null
    }

    var submitError by mutableStateOf<FormError?>(null)
        private set

    fun onNicknameSubmit() {
        onNicknameChange(nickname ?: "")

        if (nicknameError != null) {
            submitError = FormError.InvalidData
            return
        }

        selectedFriend?.let { friend ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val finalNickname = if (nickname.isNullOrBlank()) null else nickname

                    val beneficiary = Beneficiary(
                        beneficiaryId = friend.beneficiaryEntryId,
                        userId = user.value.userId,
                        beneficiaryUserId = friend.beneficiaryUserId,
                        nickname = finalNickname,
                        isFavorite = false
                    )
                    beneficiaryRepository.updateBeneficiary(beneficiary)

                    viewModelScope.launch {
                        onShowEditDialogChange(false)
                        resetNicknameState()
                        loadFriends()
                    }
                } catch (e: Exception) {
                    submitError = FormError.UnknownError
                    e.printStackTrace()
                }
            }
        }
    }

    fun onDelete() {
        selectedFriend?.let { friend ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    beneficiaryRepository.removeBeneficiary(user.value.userId, friend.beneficiaryUserId)
                    viewModelScope.launch {
                        onShowDeleteDialogChange(false)
                        loadFriends()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}