package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NotificationViewmodel: ViewModel() {

    var isPermissionDenied by mutableStateOf(false)
        private set

    fun onPermissionDeniedChange(newValue: Boolean){
        isPermissionDenied = newValue
    }

   var isPermissionDeniedByDialogBox by mutableStateOf(false)
       private set

    fun onIsPermissionDeniedByDialogBoxChange(newValue: Boolean){
        isPermissionDeniedByDialogBox = newValue
    }

}