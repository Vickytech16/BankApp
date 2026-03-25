package com.example.bankapp.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.utilities.uiAccNo
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ProfileViewModel(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val sessionState: SessionState.Authenticated.AccountRegistered
) : ViewModel() {


    val account: StateFlow<Account> = accountRepository
        .getAccountAsFlowByAccountNumber(sessionState.account.accNo).map { it!! }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = sessionState.account
        )

    private var _user: MutableStateFlow<User> = MutableStateFlow<User>(sessionState.user)
    val user: StateFlow<User> = _user.asStateFlow()

    var imageUpdateTrigger by mutableStateOf(0)
        private set

    init {
        viewModelScope.launch {
            userRepository.getUserAsFlowByUserId(sessionState.user.userId)
                .collect { updatedUser ->
                    _user.value = updatedUser!!
                }
        }
    }

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    var isAccNoVisible by mutableStateOf(false)
        private set

    val isDarkMode = mutableStateOf(true)

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
                    _user.value = _user.value.copy(pfpURL = imagePath)
                    userRepository.updateUser(_user.value)
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

                val userId = _user.value.userId
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