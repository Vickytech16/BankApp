package com.example.bankapp.viewmodels

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


class OtpViewModel: ViewModel(){

    var generatedOtp by mutableStateOf<Int?>(null)
        private set

    private var otpExpiryJob: Job? = null

    override fun onCleared() {
        super.onCleared()
        expireOtp()
    }


    var otpExpiresAt by mutableIntStateOf(60)
        private set

    var isOtpSent by mutableStateOf(false)
        private set

    fun generateOtp(){
        generatedOtp = (100000..999999).random()
        otpExpiresAt = 60

        isOtpSent = true
        otpExpiryJob?.cancel()
        otpExpiryJob = viewModelScope.launch {
            while(otpExpiresAt>0){
                delay(1000)
                otpExpiresAt--
            }
            expireOtp()
        }
    }

    private fun expireOtp(){
        generatedOtp = null
        isOtpSent = false
        submitError = FormError.OtpExpired
    }

    var userEnteredOtp by mutableStateOf("")
        private set

    var isOtpValid by mutableStateOf<Boolean?>(null)
        private set

    var submitError by mutableStateOf<FormError?>(null)
        private set

    fun submitOtp() {
        val fullOtp = otpInputs.joinToString("").toIntOrNull() ?: return
        onOtpSubmit(fullOtp)
    }

    var otpInputs by mutableStateOf(List(6) { "" })
        private set


    fun onOtpInputChange(index: Int, value: String) {
        if (value.length <= 1 && (value.isEmpty() || value.all { it.isDigit() })) {
            val newInputs = otpInputs.toMutableList()
            newInputs[index] = value
            otpInputs = newInputs

            submitError = null
            isOtpValid = null
        }
    }

    fun resetOtpInputs() {
        otpInputs = List(6) { "" }
    }

    fun resetOtpState() {
        otpExpiryJob?.cancel()
        otpExpiryJob = null
        generatedOtp = null
        isOtpSent = false
        userEnteredOtp = ""
        isOtpValid = null
        otpExpiresAt = 60
    }

    private fun onOtpSubmit(newOtp: Int){
        if (newOtp==generatedOtp){
            isOtpValid = true
            submitError = null
        }
        else{
            isOtpValid = false
            userEnteredOtp = ""
            resetOtpInputs()
            submitError = FormError.OtpDoesntMatch
        }
    }
}