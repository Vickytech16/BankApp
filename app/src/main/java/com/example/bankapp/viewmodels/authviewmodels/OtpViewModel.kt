package com.example.bankapp.viewmodels.authviewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.errors.FormError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpViewModel : ViewModel() {

    var generatedOtp by mutableStateOf<Int?>(null)
        private set

    var otpExpiresAt by mutableIntStateOf(60)
        private set

    var isOtpSent by mutableStateOf(false)
        private set

    var isOtpValid by mutableStateOf<Boolean?>(null)
        private set

    var otpInputs by mutableStateOf(List(6){""})
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    private var otpExpiryJob: Job? = null

    private var isOtpProcessStarted = false

    fun startOtpProcess(onSend: () -> Unit) {
        if (!isOtpProcessStarted) {
            isOtpProcessStarted = true
            onSend()
        }
    }

    fun generateOtp() {
        submitError = null
        isOtpValid = null
        otpExpiryJob?.cancel()
        generatedOtp = (100000..999999).random()
        otpExpiresAt = 60
        isOtpSent = true
        resetOtpInputs()
        otpExpiryJob = viewModelScope.launch {
            while (otpExpiresAt > 0) {
                delay(1000)
                otpExpiresAt--
            }
            expireOtp()
        }
    }

    private fun expireOtp() {
        generatedOtp = null
        isOtpSent = false
        submitError = FormError.OtpExpired
    }

    fun onOtpInputChange(index: Int, value: String) {
        if (value.length <= 1 && (value.isEmpty() || value.all { it.isDigit() })) {
            val newInputs = otpInputs.toMutableList()
            newInputs[index] = value
            otpInputs = newInputs.toList()
            submitError = null
            isOtpValid = null
        }
    }

    fun submitOtp() {
        val fullOtp = otpInputs.joinToString("").toIntOrNull() ?: return
        if (fullOtp == generatedOtp) {
            isOtpValid = true
            submitError = null
        } else {
            isOtpValid = false
            resetOtpInputs()
            submitError = FormError.OtpDoesNotMatch
        }
    }

    fun resetOtpInputs() {
        otpInputs = List(6) { "" }
    }

    fun resetOtpState() {
        otpExpiryJob?.cancel()
        generatedOtp = null
        isOtpSent = false
        isOtpValid = null
        otpExpiresAt = 60
        isOtpProcessStarted = false
        resetOtpInputs()
    }
}