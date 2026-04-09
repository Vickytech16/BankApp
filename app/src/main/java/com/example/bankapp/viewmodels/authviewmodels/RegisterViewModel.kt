package com.example.bankapp.viewmodels.authviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.R
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.entities.dtos.Country
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.entities.errors.UiError
import com.example.bankapp.repositories.CountryRepository
import com.example.bankapp.repositories.UserRepository
import com.example.bankapp.services.PasswordHashingService
import com.example.bankapp.services.RecoverKeyGenerationService
import com.example.bankapp.utilities.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository,
    countryRepository: CountryRepository
) : ViewModel() {

    data class RegisterUiState(
        val userName: String = "",
        val userNameError: FormError? = null,
        val hasUserNameFocused: Boolean = false,
        val hasUserNameUnFocused: Boolean = false,
        val email: String = "",
        val emailError: FormError? = null,
        val phoneNumber: String = "",
        val phoneNumberError: FormError? = null,
        val hasPhoneFocused: Boolean = false,
        val hasPhoneUnFocused: Boolean = false,
        val password: String = "",
        val passwordError: List<UiError> = emptyList(),
        val isPasswordVisible: Boolean = false,
        val hasPasswordFocused: Boolean = false,
        val hasPasswordUnFocused: Boolean = false,
        val confirmPassword: String = "",
        val confirmPasswordError: FormError? = null,
        val isConfirmPasswordVisible: Boolean = false,
        val selectedCountry: Country? = null,
        val selectedTimezone: String? = null,
        val isCountrySheetVisible: Boolean = false,
        val countrySearchQuery: String = "",
        val isTimezoneSheetVisible: Boolean = false,
        val timezoneSearchQuery: String = "",
        val isTimezoneFieldVisible: Boolean = false,
        val countryError: FormError? = null,
        val timezoneError: FormError? = null,
        val submitError: FormError? = null,
        val isLoading: Boolean = false,
        val isSubmitSuccessful: Boolean = false,
        val recoveryKey: String = "",
        val validationTrigger: Long = 0L,
        val isEmailSelected: Boolean = false,
        val hasEmailUnFocused: Boolean = false
    )


    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()


    val countries: StateFlow<List<Country>> = countryRepository.getCountries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun onUserNameChange(newUserName: String) {
        val processed =
        if (newUserName.length > USERNAME_MAX_SIZE) {
            newUserName.substring(0, USERNAME_MAX_SIZE)
        }
        else {
            newUserName
        }

        if (processed != " ") {
            val error = when {
                processed.isEmpty() ->
                    processed.emptyTextFieldErrorMessageBuilder(R.string.username_field_name)
                processed.length >= USERNAME_MAX_SIZE ->
                    processed.maxAllowedCharacterErrorMessageBuilder(R.string.username_field_name, USERNAME_MAX_SIZE)
                else ->
                    processed.invalidUserNameErrorMessageBuilder() ?:
                    processed.minRequiredCharacterErrorMessageBuilder(R.string.username_field_name, USERNAME_MIN_SIZE)
            }

            if(processed.invalidUserNameErrorMessageBuilder()!=null){
                    _uiState.update { it.copy(userName = processed, userNameError = error, hasUserNameFocused = true, hasUserNameUnFocused = true) }
                }
            _uiState.update { it.copy(userName = processed, userNameError = error) }
            submitErrorReset()
        }
    }


    fun onEmailChange(newEmail: String) {
        val emailValue = if (newEmail.length > EMAIL_MAX_SIZE) {
            newEmail.substring(0, EMAIL_MAX_SIZE).lowercase()
        }
        else {
            newEmail.lowercase()
        }

        val error = when {
            emailValue.isEmpty() ->
                emailValue.emptyTextFieldErrorMessageBuilder(R.string.email_field_name)
            emailValue.length >= EMAIL_MAX_SIZE ->
                emailValue.maxAllowedCharacterErrorMessageBuilder(R.string.email_field_name, EMAIL_MAX_SIZE)
            else ->
                emailValue.invalidEmailErrorMessageBuilder()
        }

        _uiState.update { it.copy(email = emailValue, emailError = error) }
        submitErrorReset()
    }


    fun onPhoneNumberChange(newPhoneNumber: String) {
        val digits = newPhoneNumber.filter { it.isDigit() }
        val processed = if (digits.length > PHONE_NUMBER_MAX_SIZE) {
            digits.substring(0, PHONE_NUMBER_MAX_SIZE)
        }
        else {
            digits
        }

        val iso = _uiState.value.selectedCountry?.countryCode ?: ""
        val error = when {
            processed.isBlank() ->
                processed.emptyTextFieldErrorMessageBuilder(R.string.phone_number_field_name)
            processed.length >= PHONE_NUMBER_MAX_SIZE ->
                processed.maxAllowedCharacterErrorMessageBuilder(R.string.phone_number_field_name, PHONE_NUMBER_MAX_SIZE)
            iso.isNotEmpty() && !PhoneUtils.isValidMobileNumber(processed, iso) ->
                FormError.InvalidPhoneNumber
            else ->
                null
        }

        _uiState.update { it.copy(phoneNumber = processed, phoneNumberError = error) }
        submitErrorReset()
    }


    fun onPasswordChange(newPassword: String) {
        val processed = if (newPassword.length > PASSWORD_MAX_SIZE) {
            newPassword.substring(0, PASSWORD_MAX_SIZE)
        }
        else {
            newPassword
        }

        val pError = listOfNotNull(
            processed.emptyTextFieldErrorMessageBuilder(R.string.password_field_name),
            if (processed.length >= PASSWORD_MAX_SIZE) {
                processed.maxAllowedCharacterErrorMessageBuilder(R.string.password_field_name, PASSWORD_MAX_SIZE)
            }
            else {
                null
            }
        ) + processed.invalidPasswordErrorMessageBuilder()

        _uiState.update { state ->
            val cpError = if (state.confirmPassword.isNotBlank()) {
                state.confirmPassword.invalidConfirmPasswordErrorMessageBuilder(processed)
            }
            else {
                state.confirmPasswordError
            }

            state.copy(password = processed, passwordError = pError, confirmPasswordError = cpError)
        }
        submitErrorReset()
    }


    fun onConfirmPasswordChange(newConfirmPassword: String) {
        val processed = if (newConfirmPassword.length > PASSWORD_MAX_SIZE) {
            newConfirmPassword.substring(0, PASSWORD_MAX_SIZE)
        }
        else {
            newConfirmPassword
        }

        val error = when {
            processed.isEmpty() ->
                processed.emptyTextFieldErrorMessageBuilder(R.string.confirm_password_field_name)
            processed.length >= PASSWORD_MAX_SIZE ->
                processed.maxAllowedCharacterErrorMessageBuilder(R.string.confirm_password_field_name, PASSWORD_MAX_SIZE)
            else ->
                processed.invalidConfirmPasswordErrorMessageBuilder(_uiState.value.password)
        }

        _uiState.update { it.copy(confirmPassword = processed, confirmPasswordError = error) }
        submitErrorReset()
    }


    fun onHasUserNameFocusChange(focused: Boolean) {
        _uiState.update {
            if (focused) {
                it.copy(hasUserNameFocused = true)
            }
            else if (it.hasUserNameFocused) {
                it.copy(hasUserNameUnFocused = true)
            }
            else {
                it
            }
        }
    }


    fun onEmailFocusChange(focused: Boolean) {
        _uiState.update {
            if (focused) {
                it.copy(isEmailSelected = true)
            } else if (it.isEmailSelected) {
                it.copy(isEmailSelected = false, hasEmailUnFocused = true)
            } else {
                it.copy(isEmailSelected = false)
            }
        }
    }

    fun onHasPhoneFocusChange(focused: Boolean) {
        _uiState.update {
            if (focused) {
                it.copy(hasPhoneFocused = true)
            }
            else if (it.hasPhoneFocused) {
                it.copy(hasPhoneUnFocused = true)
            }
            else {
                it
            }
        }
    }


    fun onHasPasswordFocusChange(focused: Boolean) {
        _uiState.update {
            if (focused) {
                it.copy(hasPasswordFocused = true)
            }
            else if (it.hasPasswordFocused) {
                it.copy(hasPasswordUnFocused = true)
            }
            else {
                it
            }
        }
    }


    fun onPasswordVisibleToggle() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }


    fun onConfirmPasswordVisibleToggle() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }


    fun onCountrySearchQueryChange(newQuery: String) {
        if (newQuery.length <= COUNTRY_MAX_SIZE) {
            _uiState.update { it.copy(countrySearchQuery = newQuery) }
        }
    }


    fun onCountrySheetToggle(visible: Boolean) {
        _uiState.update { it.copy(isCountrySheetVisible = visible) }
    }


    fun onTimeZoneSearchQueryChange(newQuery: String) {
        if (newQuery.length <= TIMEZONE_MAX_SIZE) {
            _uiState.update { it.copy(timezoneSearchQuery = newQuery) }
        }
    }


    fun onTimeZoneSheetToggle(visible: Boolean) {
        _uiState.update { it.copy(isTimezoneSheetVisible = visible) }
    }


    fun onCountrySelected(country: Country) {
        _uiState.update { state ->
            val phoneChanged = if (state.selectedCountry?.countryCode != country.countryCode) {
                ""
            }
            else {
                state.phoneNumber
            }

            val phoneErrorChanged = if (state.selectedCountry?.countryCode != country.countryCode) {
                null
            }
            else {
                state.phoneNumberError
            }

            val autoTimezone = if (country.timezones.size == 1) {
                country.timezones.first()
            }
            else {
                null
            }

            val showTzField = country.timezones.size > 1

            state.copy(
                selectedCountry = country,
                phoneNumber = phoneChanged,
                phoneNumberError = phoneErrorChanged,
                countrySearchQuery = "",
                countryError = null,
                selectedTimezone = autoTimezone,
                isTimezoneFieldVisible = showTzField,
                isTimezoneSheetVisible = showTzField,
                timezoneError = null
            )
        }
    }


    fun onTimeZoneSelected(tz: String) {
        _uiState.update { it.copy(selectedTimezone = tz, timezoneError = null) }
    }


    fun resetSubmitStatus() {
        _uiState.update { it.copy(isSubmitSuccessful = false) }
    }


    private fun submitErrorReset() {
        _uiState.update { state ->
            if (state.userNameError == null && state.passwordError.isEmpty() && state.emailError == null &&
                state.phoneNumberError == null && state.confirmPasswordError == null && state.countryError == null) {
                state.copy(submitError = null)
            }
            else {
                state
            }
        }
    }


    fun isUserNameValid(): Boolean = with(_uiState.value) {
        userName.isNotEmpty() &&
                userName.minRequiredCharacterErrorMessageBuilder(R.string.username_field_name, USERNAME_MIN_SIZE) == null &&
                userName.invalidUserNameErrorMessageBuilder() == null
    }


    fun isEmailValid(): Boolean = with(_uiState.value) {
        email.isNotEmpty() && email.invalidEmailErrorMessageBuilder() == null
    }


    fun isPasswordValid(): Boolean = with(_uiState.value) {
        password.isNotEmpty() && password.invalidPasswordErrorMessageBuilder().isEmpty()
    }


    fun isConfirmPasswordValid(): Boolean = with(_uiState.value) {
        confirmPassword.isNotEmpty() && confirmPassword == password
    }


    fun isPhoneValid(): Boolean = with(_uiState.value) {
        selectedCountry != null && PhoneUtils.isValidMobileNumber(phoneNumber, selectedCountry.countryCode)
    }


    fun onSubmit() {
        val state = _uiState.value
        if (state.isLoading) {
            return
        }

        _uiState.update { it.copy(
            isLoading = true,
            isSubmitSuccessful = false,
            submitError = null,
            hasPasswordFocused = true,
            hasPasswordUnFocused = true,
            hasUserNameFocused = true,
            hasUserNameUnFocused = true,
            hasPhoneFocused = true,
            hasPhoneUnFocused = true,
            hasEmailUnFocused = true,
            validationTrigger = BankDateFactory.now().epochMillis
        )}

        onUserNameChange(state.userName)
        onEmailChange(state.email)
        onPhoneNumberChange(state.phoneNumber)
        onPasswordChange(state.password)
        onConfirmPasswordChange(state.confirmPassword)

        if (_uiState.value.selectedCountry == null) {
            _uiState.update { it.copy(countryError = FormError.EmptyData(R.string.country_field_name)) }
        }

        if (_uiState.value.isTimezoneFieldVisible && _uiState.value.selectedTimezone == null) {
            _uiState.update { it.copy(timezoneError = FormError.EmptyData(R.string.timezone_label)) }
        }

        viewModelScope.launch {
            val currentState = _uiState.value
            try {
                if (currentState.userNameError != null || currentState.passwordError.isNotEmpty() ||
                    currentState.emailError != null || currentState.phoneNumberError != null ||
                    currentState.confirmPasswordError != null || currentState.countryError != null || currentState.timezoneError != null) {
                    _uiState.update { it.copy(submitError = FormError.InvalidData) }
                }
                else {
                    if (userRepository.getUserByEmail(currentState.email) != null) {
                        _uiState.update { it.copy(submitError = FormError.UserAlreadyExists(R.string.email_field_name)) }
                    }
                    else if (userRepository.getUserByPhoneNumber(currentState.phoneNumber) != null) {
                        _uiState.update { it.copy(submitError = FormError.UserAlreadyExists(R.string.phone_number_field_name)) }
                    }
                    else {
                        val key = RecoverKeyGenerationService.generateRecoveryKey()
                        userRepository.createNewUser(
                            User(
                                email = currentState.email.trim(),
                                passwordHashed = PasswordHashingService.hash(currentState.password),
                                userName = currentState.userName.trim().replace(Regex("\\s+"), " "),
                                phoneNumber = "${currentState.selectedCountry?.phonePrefix ?: ""}${currentState.phoneNumber.trim()}",
                                countryCode = currentState.selectedCountry?.countryCode ?: "",
                                timeZone = currentState.selectedTimezone ?: "",
                                recoveryKey = PasswordHashingService.hash(key)
                            )
                        )
                        _uiState.update { it.copy(isSubmitSuccessful = true, recoveryKey = key) }
                    }
                }
            }
            catch (e: Exception) {
                _uiState.update { it.copy(submitError = FormError.InvalidData) }
            }
            finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}