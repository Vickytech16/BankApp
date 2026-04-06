package com.example.bankapp.entities.dtos

data class BeneficiaryDto(
    val beneficiaryEntryId: Long,
    val userId: Long,
    val beneficiaryName: String,
    val beneficiaryUserId: Long,
    val beneficiaryPfp: String?,
    val beneficiaryPrimaryAccNo: Long
)