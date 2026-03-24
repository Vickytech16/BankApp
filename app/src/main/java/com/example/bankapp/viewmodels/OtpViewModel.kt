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
        println("OtpViewModel CLEARED")


    }

    init {
        println("OtpViewModel CREATED")
    }
    var isInitialOtpSent by mutableStateOf(false)
        private set

    var otpExpiresAt by mutableIntStateOf(60)
        private set

    var isOtpSent by mutableStateOf(false)
        private set

    private var remainingTime by mutableStateOf(60)

    fun generateOtp(){
        otpExpiryJob?.cancel()

        println("generate otp called on config")

        generatedOtp = (100000..999999).random()
        otpExpiresAt = 60
        remainingTime = 60

        isOtpSent = true
        resetOtpInputs()


        otpExpiryJob = viewModelScope.launch {
            while(remainingTime>0){
                delay(1000)
                remainingTime--
                otpExpiresAt = remainingTime
            }
            expireOtp()
        }
    }

    fun onOtpInputChange(index: Int, value: String) {
        if (value.length <= 1 && (value.isEmpty() || value.all { it.isDigit() })) {
            val newInputs = otpInputs.toMutableList()
            newInputs[index] = value
            otpInputs = newInputs.toList()

            if (value.isNotEmpty()) {
                submitError = null
                isOtpValid = null
            }
        }
    }

    fun restoreOtp(otp: Int, timeLeft: Int) {
        otpExpiryJob?.cancel()
        generatedOtp = otp
        isOtpSent = true
        remainingTime = timeLeft
        otpExpiresAt = timeLeft

        otpExpiryJob = viewModelScope.launch {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--
                otpExpiresAt = remainingTime
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


    fun resetOtpInputs() {
        otpInputs = List(6) { "" }
    }

    fun onIsInitialOtpSentChange(newValue: Boolean){
        isInitialOtpSent = newValue
    }

    fun resetOtpState() {
        otpExpiryJob?.cancel()
        otpExpiryJob = null
        generatedOtp = null
        isOtpSent = false
        userEnteredOtp = ""
        isOtpValid = null
        otpExpiresAt = 60
        isInitialOtpSent = false
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
            submitError = FormError.OtpDoesNotMatch
        }
    }
}

//fun onOtpInputChange(index: Int, value: String) {
//    if (value.length <= 1 && (value.isEmpty() || value.all { it.isDigit() })) {
//        val newInputs = otpInputs.toMutableList()
//        newInputs[index] = value
//        otpInputs = newInputs
//
//        submitError = null
//        isOtpValid = null
//    }
//}