package com.example.bankapp.entities.dtos

data class BeneficiaryDto(
    val beneficiaryId: Long,
    val userId: Long,
    val friendName: String,
    val friendUserId: Long,
    val friendPfp: String?,
    val friendPrimaryAccNo: Long
)