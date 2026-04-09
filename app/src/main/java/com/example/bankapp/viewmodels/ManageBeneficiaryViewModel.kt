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

    var isLoading by mutableStateOf(false)
        private set

    var showDeleteDialog by mutableStateOf(false)
        private set

    var showEditDialog by mutableStateOf(false)
        private set

    var nickname by mutableStateOf<String?>("")
        private set

    var nicknameError by mutableStateOf<FormError?>(null)
        private set

    var selectedFriend by mutableStateOf<BeneficiaryDto?>(null)
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    private val user = sessionState.user
    private val _friends = MutableStateFlow<List<BeneficiaryDto>>(emptyList())
    val friends: StateFlow<List<BeneficiaryDto>> = _friends

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query


    init {
        loadFriends()
    }


    private fun loadFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true
                val allFriends = beneficiaryRepository.getAllBeneficiariesForUser(user.value.userId)
                _friends.value = allFriends
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                isLoading = false
            }
        }
    }

    var showEnlargedImage by mutableStateOf(false)
    private set

    fun onShowEnlargedImageChange(newValue: Boolean) {
        showEnlargedImage = newValue
    }


    fun onShowDeleteDialogChange(newValue: Boolean) {
        showDeleteDialog = newValue
    }


    fun onShowEditDialogChange(newValue: Boolean) {
        showEditDialog = newValue
    }


    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }


    fun onSelectFriend(friend: BeneficiaryDto) {
        selectedFriend = friend
        nickname = friend.beneficiaryName
    }


    fun onNicknameChange(newNickname: String) {
        if (newNickname.length <= USERNAME_MAX_SIZE && newNickname != " ") {
            nickname = newNickname

            nicknameError = if (newNickname.isEmpty()) {
                null
            }
            else {
                newNickname.maxAllowedCharacterErrorMessageBuilder(
                    R.string.nickname_field_name,
                    USERNAME_MAX_SIZE
                ) ?: newNickname.invalidUserNameErrorMessageBuilder()
            }

            submitError = null
        }
    }


    fun resetNicknameState() {
        nickname = ""
        nicknameError = null
        submitError = null
    }


    fun onNicknameSubmit() {
        val currentNickname = nickname ?: ""

        if (nicknameError != null) {
            submitError = FormError.InvalidData
            return
        }

        selectedFriend?.let { friend ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val finalNickname = if (currentNickname.isBlank()) {
                        null
                    }
                    else {
                        currentNickname.trim()
                    }

                    val beneficiary = Beneficiary(
                        beneficiaryId = friend.beneficiaryEntryId,
                        userId = user.value.userId,
                        beneficiaryUserId = friend.beneficiaryUserId,
                        nickname = finalNickname,
                    )

                    beneficiaryRepository.updateBeneficiary(beneficiary)

                    viewModelScope.launch {
                        onShowEditDialogChange(false)
                        resetNicknameState()
                        loadFriends()
                    }
                }
                catch (e: Exception) {
                    submitError = FormError.UnknownError
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
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}