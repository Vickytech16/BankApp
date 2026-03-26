package com.example.bankapp.entities.uimodels

data class Beneficiary(
    val beneficiaryId: Long = 0,
    val userId: Long,
    val beneficiaryUserId: Long,
    val nickname: String = "",
    val isFavorite: Boolean = false,
    val addedDate: Long
)
