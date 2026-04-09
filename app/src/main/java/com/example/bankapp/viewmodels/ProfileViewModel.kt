package com.example.bankapp.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.utilities.emptyTextFieldErrorMessageBuilder
import com.example.bankapp.utilities.invalidUserNameErrorMessageBuilder
import com.example.bankapp.utilities.maxAllowedCharacterErrorMessageBuilder
import com.example.bankapp.utilities.minRequiredCharacterErrorMessageBuilder
import com.example.bankapp.utilities.uiAccNo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered,
    changePasswordState: ChangePasswordState
) : ViewModel() {

    companion object {
        const val USERNAME_MAX_SIZE = 25
        const val USERNAME_MIN_SIZE = 3
    }

    val account = sessionState.account

    private val _editableUser = MutableStateFlow(sessionState.user.value)
    val editableUser = _editableUser.asStateFlow()

    private val _userNameDraft = MutableStateFlow(sessionState.user.value.userName)
    val userNameDraft = _userNameDraft.asStateFlow()

    private val _userNameError = MutableStateFlow<FormError?>(null)
    val userNameError = _userNameError.asStateFlow()

    init {
        changePasswordState.user = _editableUser.value
    }

    var imageUpdateTrigger by mutableStateOf(0)
    var isProcessingImage by mutableStateOf(false)
    var isAccNoVisible by mutableStateOf(false)
    var showLogoutDialog by mutableStateOf(false)
    var showEditSheet by mutableStateOf(false)
    var showEnlargedImage by mutableStateOf(false)

    fun onShowEditSheetChange(newValue: Boolean) {
        showEditSheet = newValue
        if (newValue) {
            _userNameDraft.value = _editableUser.value.userName
            _userNameError.value = null
        } else {
            _userNameError.value = null
        }
    }
    fun onShowLogoutDialogChange(newValue: Boolean) { showLogoutDialog = newValue }
    fun onShowEnlargedImageChange(newValue: Boolean) { showEnlargedImage = newValue }
    fun onAccNoVisibilityChange() { isAccNoVisible = !isAccNoVisible }

    fun getMaskedAccountNo(): String {
        val accNo = account.value.accNo.uiAccNo
        return if (isAccNoVisible) accNo.chunked(4).joinToString(" ") else "**** ${accNo.takeLast(4)}"
    }

    fun startEditing() {
        _userNameDraft.update { _editableUser.value.userName }
        _userNameError.update { null }
    }

    fun onUserNameChange(newName: String) {
        val processed = if (newName.length > USERNAME_MAX_SIZE) newName.substring(0, USERNAME_MAX_SIZE) else newName

        if (processed != " ") {
            val error = when {
                processed.isEmpty() -> processed.emptyTextFieldErrorMessageBuilder(R.string.username_field_name)
                processed.length >= USERNAME_MAX_SIZE -> processed.maxAllowedCharacterErrorMessageBuilder(R.string.username_field_name, USERNAME_MAX_SIZE)
                else -> processed.invalidUserNameErrorMessageBuilder() ?: processed.minRequiredCharacterErrorMessageBuilder(R.string.username_field_name, USERNAME_MIN_SIZE)
            }

            _userNameDraft.update { processed }
            _userNameError.update { error }
        }
    }

    fun isUserNameValid(): Boolean{
       return _userNameDraft.value.isNotEmpty() &&
                userNameDraft.value.minRequiredCharacterErrorMessageBuilder(R.string.username_field_name,
                    com.example.bankapp.utilities.USERNAME_MIN_SIZE
                ) == null &&
                userNameDraft.value.invalidUserNameErrorMessageBuilder() == null
    }

    fun updateUserName() {
        if (_userNameError.value != null) return
        viewModelScope.launch {
            try {
                val updatedUser = _editableUser.value.copy(userName = _userNameDraft.value)
                userRepository.updateUser(updatedUser)
                _editableUser.update { updatedUser }
                onShowEditSheetChange(false)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteProfileImage() {
        viewModelScope.launch {
            isProcessingImage = true
            try {
                _editableUser.value.pfpURL?.let { path -> File(path).apply { if (exists()) delete() } }
                val updatedUser = _editableUser.value.copy(pfpURL = null)
                userRepository.updateUser(updatedUser)
                _editableUser.update { updatedUser }
                imageUpdateTrigger++
            } finally { isProcessingImage = false }
        }
    }

    fun updateProfileImage(bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            isProcessingImage = true
            try {
                _editableUser.value.pfpURL?.let { path -> File(path).apply { if (exists()) delete() } }
                saveBitmapToLocalStorage(bitmap, context)?.let { path ->
                    val updatedUser = _editableUser.value.copy(pfpURL = path)
                    userRepository.updateUser(updatedUser)
                    _editableUser.update { updatedUser }
                    imageUpdateTrigger++
                }
            } finally { isProcessingImage = false }
        }
    }

    private suspend fun saveBitmapToLocalStorage(bitmap: Bitmap, context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val appDir = context.getExternalFilesDir("profile_pictures")?.apply { if (!exists()) mkdirs() } ?: return@withContext null
            val file = File(appDir, "profile_${_editableUser.value.userId}_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
            file.absolutePath
        } catch (e: Exception) { null }
    }
}