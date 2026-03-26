package com.example.bankapp.utilities.mappers

import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.uimodels.AccountUiModel

fun Account.toUiModel(): AccountUiModel {
    return AccountUiModel(
        accNo = this.accNo,
        userId = this.userId,
        ifscCode = this.ifscCode,
        accountType = this.accountType,
        balance = this.balance,
        createdAt = BankDateFactory.fromMillis(this.createdAt),
        updatedAt = BankDateFactory.fromMillis(this.updatedAt)
    )
}

fun AccountUiModel.toDbModel(): Account {
    return Account(
        accNo = this.accNo,
        userId = this.userId,
        ifscCode = this.ifscCode,
        accountType = this.accountType,
        balance = this.balance,
        createdAt = this.createdAt.epochMillis,
        updatedAt = this.updatedAt.epochMillis
    )
}