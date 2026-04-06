package com.example.bankapp.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.ChangePasswordState
import com.example.bankapp.entities.SessionState
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.ui.theme.userNameWithSpacesRegex
import com.example.bankapp.utilities.uiAccNo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileViewModel(
    private val userRepository: UserRepository,
    sessionState: SessionState.Authenticated.AccountRegistered,
    changePasswordState: ChangePasswordState
) : ViewModel() {

    companion object {
        const val USER_NAME_MAX_SIZE = 25
    }

    val account = sessionState.account
    var user = sessionState.user

    init {
        changePasswordState.user = user.value
    }

    var imageUpdateTrigger by mutableStateOf(0)
        private set

    var isProcessingImage by mutableStateOf(false)
        private set

    var isAccNoVisible by mutableStateOf(false)
        private set

    var showLogoutDialog by mutableStateOf(false)
        private set

    var showEditSheet by mutableStateOf(false)
        private set

    var showEnlargedImage by mutableStateOf(false)
        private set

    fun onShowEditSheetChange(newValue: Boolean) {
        showEditSheet = newValue
    }

    fun onShowLogoutDialogChange(newValue: Boolean) {
        showLogoutDialog = newValue
    }

    fun onShowEnlargedImageChange(newValue: Boolean) {
        showEnlargedImage = newValue
    }

    fun onAccNoVisibilityChange() {
        isAccNoVisible = !isAccNoVisible
    }

    fun getMaskedAccountNo(): String {
        val accNo = account.value.accNo.uiAccNo
        return if (isAccNoVisible) {
            accNo.chunked(4).joinToString(" ")
        } else {
            "**** ${accNo.takeLast(4)}"
        }
    }

    fun validateUserName(name: String): Boolean {
        return name.length <= USER_NAME_MAX_SIZE && name.matches(userNameWithSpacesRegex)
    }

    fun updateUserName(newName: String) {
        if (!validateUserName(newName)) return
        viewModelScope.launch {
            try {
                val updatedUser = user.value.copy(userName = newName)
                userRepository.updateUser(updatedUser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteProfileImage() {
        viewModelScope.launch {
            isProcessingImage = true
            try {
                user.value.pfpURL?.let { path ->
                    val file = File(path)
                    if (file.exists()) file.delete()
                }
                val updatedUser = user.value.copy(pfpURL = null)
                userRepository.updateUser(updatedUser)
                imageUpdateTrigger++
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isProcessingImage = false
            }
        }
    }

    fun updateProfileImage(bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            isProcessingImage = true
            try {
                user.value.pfpURL?.let { path ->
                    val oldFile = File(path)
                    if (oldFile.exists()) oldFile.delete()
                }
                val imagePath = saveBitmapToLocalStorage(bitmap, context)
                if (imagePath != null) {
                    userRepository.updateUser(user.value.copy(pfpURL = imagePath))
                    imageUpdateTrigger++
                }
            } finally {
                isProcessingImage = false
            }
        }
    }

    private suspend fun saveBitmapToLocalStorage(bitmap: Bitmap, context: Context): String? =
        withContext(Dispatchers.IO) {
            try {
                val userId = user.value.userId
                val appDir = context.getExternalFilesDir("profile_pictures") ?: return@withContext null
                if (!appDir.exists()) appDir.mkdirs()
                val fileName = "profile_${userId}_${System.currentTimeMillis()}.jpg"
                val file = File(appDir, fileName)
                file.outputStream().use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
                }
                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}