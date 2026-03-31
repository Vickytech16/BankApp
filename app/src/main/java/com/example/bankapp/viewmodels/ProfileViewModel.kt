package com.example.bankapp.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.bankapp.entities.SessionState
import com.example.bankapp.utilities.uiAccNo

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.usecases.ChangePasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ProfileViewModel(
    private val userRepository: UserRepository,
    sessionState: SessionState.Authenticated.AccountRegistered,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {


    val account = sessionState.account
    var user = sessionState.user

    init {
        changePasswordUseCase.user = user.value
    }

    var imageUpdateTrigger by mutableStateOf(0)
        private set

    init {
//        viewModelScope.launch {
//            userRepository.getUserAsFlowByUserId(user.value.userId)
//                .collect { updatedUser ->
//                  user.value = updatedUser!!
//                }
//        }
    }

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    var isAccNoVisible by mutableStateOf(false)
        private set

    var showLogoutDialog by mutableStateOf(false)
        private set

    fun onShowLogoutDialogChange(newValue: Boolean){
        showLogoutDialog = newValue
    }

    fun onAccNoVisibilityChange() {
        isAccNoVisible = !isAccNoVisible
    }


    fun getMaskedAccountNo(): String {
        val accNo = account.value.accNo.uiAccNo
        return if (isAccNoVisible) {
            accNo
        } else {
            "**** ${accNo.takeLast(4)}"
        }
    }

    fun updateProfileImage(bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            try {
                val imagePath = saveBitmapToLocalStorage(bitmap, context)
                if (imagePath != null) {
                    val currentUser = user.value
                    val updatedUser = currentUser.copy(pfpURL = imagePath)
                    userRepository.updateUser(updatedUser)
                    imageUpdateTrigger++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private suspend fun saveBitmapToLocalStorage(bitmap: Bitmap, context: Context): String? =
        withContext(Dispatchers.IO) {
            return@withContext try {

                val userId = user.value.userId
                val appDir = context.getExternalFilesDir("profile_pictures")
                    ?: return@withContext null

                if (!appDir.exists()) {
                    appDir.mkdirs()
                }

                val fileName = "profile_${userId}.jpg"
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