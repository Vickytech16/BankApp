package com.example.bankapp.viewmodels

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.entities.uimodels.AccountUiModel
import com.example.bankapp.repositories.AccountRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.ui.components.AlertButtonConfig
import com.example.bankapp.ui.components.AlertDialogBox
import com.example.bankapp.usecases.SharedPreferenceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LoggedInSessionViewModel(
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository
): ViewModel(){

    private var failedPasswordAttempts = 0

    var shouldLogoutOnPasswordFailure by mutableStateOf(false)
        private set

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private var _currentUser = MutableStateFlow<User?>(value = null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private var _account = MutableStateFlow<AccountUiModel?>(value = null)
    val account: StateFlow<AccountUiModel?> = _account.asStateFlow()


    fun restoreSession(){

            viewModelScope.launch {
                val userFromPrefs = sharedPreferenceHelper.getUserFromSharedPreferences()

                if (userFromPrefs == null) {
                    _sessionState.value = SessionState.UnAuthenticated
                    return@launch
                }

                BankDateFactory.initialize(userFromPrefs.timeZone)

                val userStateFlow = userRepository.getUserAsFlowByUserId(userFromPrefs.userId)
                    .filterNotNull()
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.Eagerly,
                        initialValue = userFromPrefs
                    )

                val initialAccount = accountRepository.getAccountByUserId(userFromPrefs.userId).firstOrNull()

                _sessionState.value = if (initialAccount == null) {
                    SessionState.Authenticated.AccountNotRegistered(userFromPrefs)
                } else {
                    val accountFlow = accountRepository.getAccountAsFlowByUserId(userFromPrefs.userId)
                        .filterNotNull()
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.Eagerly,
                            initialValue = initialAccount
                        )
                    SessionState.Authenticated.AccountRegistered(
                        user = userStateFlow,
                        account = accountFlow
                    )
                }
            }

    }

    init {
        restoreSession()
    }

    fun logout(){
        sharedPreferenceHelper.clearSession()
        _sessionState.value= SessionState.UnAuthenticated
        BankDateFactory.initialize("UTC")
        restoreSession()
    }

    fun handlePasswordAttempt(isSuccess: Boolean) {
        if (isSuccess) {
            failedPasswordAttempts = 0
        } else {
            failedPasswordAttempts++

            if (failedPasswordAttempts >= 3) {
                failedPasswordAttempts = 0
                shouldLogoutOnPasswordFailure = true
            }
        }
    }
}