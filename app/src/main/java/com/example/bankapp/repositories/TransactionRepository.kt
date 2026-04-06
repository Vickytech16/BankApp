package com.example.bankapp.repositories

import androidx.room.withTransaction
import com.example.bankapp.core.BankDatabase
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.core.datecompatability.BankDateTime
import com.example.bankapp.daos.TransactionDao
import com.example.bankapp.entities.AccountVelocityStatus
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.dbtables.Ledger
import com.example.bankapp.entities.dbtables.Transaction
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.account.AccountType
import com.example.bankapp.entities.types.transaction.LedgerDirection
import com.example.bankapp.entities.types.transaction.TransactionFailureType
import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.utilities.uiUserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.math.BigDecimal

import java.util.UUID

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val ledgerRepository: LedgerRepository,
    private val accountRepository: AccountRepository,
    private val currencyExchangeRepository: CurrencyExchangeRepository,
    private val userRepository: UserRepository,
    private val database: BankDatabase
){
    // yet to implement
    companion object {
        private val MAX_TRANSFER_LIMIT = BigDecimal("999999.99")
        private val MAX_DEPOSIT_LIMIT = BigDecimal("999999999.99")
    }

  fun getFilteredTransactions(
      accNo: Long,
      searchQuery: String = "",
      types: List<String> = emptyList(),
      typeFilter: Int = 0,
      directions: List<String> = listOf("CREDIT", "DEBIT"),
      directionFilter: Int = 0,
      statuses: List<String> = emptyList(),
      statusFilter: Int = 0,
      sortOrder: String = "NEWEST",
      startDate: Long,
      endDate: Long,
      dateFilter: Int
  ): Flow<List<TransactionHistoryItemDto>> {
        return transactionDao.getFilteredTransactions(
            accNo = accNo,
            searchQuery = searchQuery,
            types = types,
            typeFilter = typeFilter,
            directions = directions,
            directionFilter = directionFilter,
            statuses = statuses,
            statusFilter = statusFilter,
            sortOrder = sortOrder,
            startDate = startDate,
            endDate = endDate,
            dateFilter = dateFilter
        )
    }

    suspend fun getTransactionHistoryItemByTransactionId(transactionId: String, accNo: Long): TransactionHistoryItemDto?{
        return withContext(Dispatchers.IO){
            transactionDao.getTransactionHistoryItemById(transactionId, accNo)
        }
    }

    suspend fun checkIfAccountExists(accNo: Long): Boolean{
        return withContext(Dispatchers.IO){
            accountRepository.getAccountAsFlowByAccNo(accNo).firstOrNull() != null
        }
    }

    private fun getUpdatedTransaction(
        transaction: Transaction,
        transactionStatus: TransactionStatus,
        failureType: TransactionFailureType? = null): Transaction {
        val newTransaction = transaction.copy(
            transactionId = transaction.transactionId,
            referenceNumber = transaction.referenceNumber,
            transactionType = transaction.transactionType,
            transactionStatus = transactionStatus,
            createdAt = transaction.createdAt,
            updatedAt = BankDateFactory.now().epochMillis,
            failureType = failureType
        )
        return newTransaction
    }

    private fun generateReferenceNumber(): String {
        return "TXN" + UUID.randomUUID()
            .toString()
            .substring(0, 8)
            .uppercase()
    }

    private suspend fun getAmountInUsd(amount: BigDecimal, fromCurrency: String): BigDecimal {
        if (fromCurrency == "USD") return amount

        val allRates = currencyExchangeRepository.getLatestRates()?.rates ?: return BigDecimal.ZERO
        val rateToUsd = allRates[fromCurrency] ?: return BigDecimal.ZERO
        val rate = BigDecimal(rateToUsd.toString())

        return amount.divide(rate, 8, java.math.RoundingMode.HALF_UP)
    }

    private suspend fun createTransaction(
        transactionType: TransactionType,
        transactionStatus: TransactionStatus,
        idempotencyKey: String): Transaction? {

        var tries = 3
        var transaction: Transaction?
        do{
            transaction = Transaction(
                transactionId = UUID.randomUUID().toString(),
                referenceNumber = generateReferenceNumber(),
                transactionType = transactionType,
                transactionStatus = transactionStatus,
                idempotencyKey = idempotencyKey,
                createdAt = BankDateFactory.now().epochMillis,
                updatedAt = BankDateFactory.now().epochMillis,
            )

            val isTransactionSuccessful = transactionDao.insert(transaction)

            if(isTransactionSuccessful.toInt() == -1){
                tries--
                transaction = null
            }
            else
                break
        }while(tries>0)
        return transaction
    }

    private suspend fun performVelocityCheck(
        accNo: Long,
        accountType: AccountType,
        transactionType: TransactionType,
        amountInUsd: BigDecimal
    ): TransactionResult? {

        val maxPerTx = accountType.getMaxPerTransaction(transactionType)
        if (amountInUsd > maxPerTx) {
            return TransactionResult.Error.LimitExceeded.SingleTransactionLimitExceeded(maxPerTx)
        }

        val dayInMs = BankDateTime.DAY_IN_MINUTES
        val startTime = BankDateFactory.now().epochMillis - dayInMs

        val spentSoFar = ledgerRepository.getTotalSpentInUsdSince(accNo, startTime) ?: BigDecimal.ZERO
        val countSoFar = ledgerRepository.getTransactionCountSince(accNo, startTime)

        if (spentSoFar.add(amountInUsd) > accountType.dailyTransactionLimit) {
            return TransactionResult.Error.LimitExceeded.DailyLimitExceeded(accountType.dailyTransactionLimit)
        }

        if (countSoFar >= accountType.dailyTransactionCount) {
            return TransactionResult.Error.LimitExceeded.DailyCountExceeded(accountType.dailyTransactionCount)
        }

        return null
    }

   suspend fun cashTransfer(
        fromAccountNo: Long,
        toAccountNo: Long,
        amount: BigDecimal,
        idempotencyKey: String
    ): TransactionResult  = withContext(Dispatchers.IO) {

        return@withContext database.withTransaction {

            if (amount <= BigDecimal.ZERO) {
                return@withTransaction TransactionResult.Error.InvalidAmount
            }
            if (fromAccountNo == toAccountNo) {
                return@withTransaction TransactionResult.Error.SameAccountTransfer
            }

            val fromAccount: Account? =
                accountRepository.getAccountAsFlowByAccNo(fromAccountNo).firstOrNull()
            val toAccount: Account? =
                accountRepository.getAccountAsFlowByAccNo(toAccountNo).firstOrNull()

            if (fromAccount == null || toAccount == null) {
                return@withTransaction TransactionResult.Error.AccountNotFound
            }

            val fromUser = userRepository.getUserByUserId(fromAccount.userId.toString())
                ?: return@withTransaction TransactionResult.Error.UnKnown

            val amountInUsd = getAmountInUsd(amount, CurrencyUtils.getCurrencyCode(fromUser.countryCode))

            if(amountInUsd == BigDecimal.ZERO) {
                return@withTransaction TransactionResult.Error.ExchangeRatesNotFound
            }

            val velocityError = performVelocityCheck(
                accNo = fromAccountNo,
                accountType = fromAccount.accountType,
                transactionType = TransactionType.CASH_TRANSFER,
                amountInUsd = amountInUsd
            )
            if (velocityError != null) {
                return@withTransaction velocityError
            }

            val transaction =
                createTransaction(TransactionType.CASH_TRANSFER, TransactionStatus.PENDING, idempotencyKey)
                    ?: return@withTransaction TransactionResult.Error.RepeatedTransaction
            var updatedTransaction =
                getUpdatedTransaction(transaction, TransactionStatus.COMPLETED)

            if (fromAccount.balance < amount) {
                val failedTransaction = getUpdatedTransaction(
                    transaction,
                    TransactionStatus.FAILED,
                    failureType = TransactionFailureType.INSUFFICIENT_BALANCE
                )
                transactionDao.update(failedTransaction)

                val fromLedger = Ledger(
                    transactionId = transaction.transactionId,
                    accNo = fromAccount.accNo,
                    direction = LedgerDirection.DEBIT,
                    amount = amount,
                    balanceAfter = fromAccount.balance,
                    amountInUsd = amountInUsd
                )

                val toLedger = Ledger(
                    transactionId = transaction.transactionId,
                    accNo = toAccount.accNo,
                    direction = LedgerDirection.CREDIT,
                    amount = amount,
                    balanceAfter = toAccount.balance,
                    amountInUsd = amountInUsd
                )

                ledgerRepository.insertAll(listOf(fromLedger, toLedger))

                return@withTransaction TransactionResult.Error.InsufficientBalance
            }

            accountRepository.withdraw(amount, BankDateFactory.now().epochMillis, fromAccountNo)

            transactionDao.update(updatedTransaction)

            val isDepositSuccessful =
                accountRepository.deposit(amount, BankDateFactory.now().epochMillis, toAccountNo)
            if(isDepositSuccessful==0){
                updatedTransaction = getUpdatedTransaction(transaction, TransactionStatus.FAILED,
                    TransactionFailureType.UNKNOWN_ERROR)
                transactionDao.update(updatedTransaction)
                return@withTransaction TransactionResult.Error.UnKnown
            }

            val fromLedger = Ledger(
                transactionId = transaction.transactionId,
                accNo = fromAccount.accNo,
                direction = LedgerDirection.DEBIT,
                amount = amount,
                balanceAfter = fromAccount.balance - amount,
                amountInUsd = amountInUsd
            )

            val toLedger = Ledger(
                transactionId = transaction.transactionId,
                accNo = toAccount.accNo,
                direction = LedgerDirection.CREDIT,
                amount = amount,
                balanceAfter = toAccount.balance + amount,
                amountInUsd = amountInUsd
            )

            ledgerRepository.insertAll(listOf(fromLedger, toLedger))

            transactionDao.update(updatedTransaction)

            TransactionResult.Success(updatedTransaction.transactionId)
        }

    }

    suspend fun deposit(
        accountNo: Long,
        amount: BigDecimal,
        idempotencyKey: String
    ): TransactionResult  = withContext(Dispatchers.IO){

        return@withContext database.withTransaction {
            if (amount <= BigDecimal.ZERO) {
                return@withTransaction TransactionResult.Error.InvalidAmount
            }

            val account =
                accountRepository.getAccountAsFlowByAccNo(accNo = accountNo).firstOrNull()
                    ?: return@withTransaction TransactionResult.Error.AccountNotFound

            val user = userRepository.getUserByUserId(account.userId.toString())
                ?: return@withTransaction TransactionResult.Error.UnKnown

            val amountInUsd = getAmountInUsd(amount, CurrencyUtils.getCurrencyCode(user.countryCode))

            if(amountInUsd == BigDecimal.ZERO) {
                return@withTransaction TransactionResult.Error.ExchangeRatesNotFound
            }

            val transaction = createTransaction(
                transactionType = TransactionType.DEPOSIT,
                transactionStatus = TransactionStatus.PENDING,
                idempotencyKey = idempotencyKey
            ) ?: return@withTransaction TransactionResult.Error.RepeatedTransaction

            var updatedTransaction =
                getUpdatedTransaction(transaction, TransactionStatus.COMPLETED)

            val isDepositSuccessful =
                accountRepository.deposit(amount, BankDateFactory.now().epochMillis, account.accNo)

            if (isDepositSuccessful == 0){
                updatedTransaction = getUpdatedTransaction(transaction, TransactionStatus.FAILED,
                    TransactionFailureType.UNKNOWN_ERROR)
                transactionDao.update(updatedTransaction)
                return@withTransaction TransactionResult.Error.UnKnown
            }

            val ledger = Ledger(
                transactionId = transaction.transactionId,
                accNo = account.accNo,
                direction = LedgerDirection.CREDIT,
                balanceAfter = account.balance + amount,
                amount = amount,
                amountInUsd = amountInUsd
            )

            ledgerRepository.insertAll(listOf(ledger))

            transactionDao.update(updatedTransaction)

            TransactionResult.Success(updatedTransaction.transactionId)
        }
    }

    suspend fun internationalTransfer(
        fromAccountNo: Long,
        toAccountNo: Long,
        amount: BigDecimal,
        exchangeRate: BigDecimal,
        idempotencyKey: String
    ): TransactionResult = withContext(Dispatchers.IO) {
        return@withContext database.withTransaction {
            val fromAccount = accountRepository.getAccountAsFlowByAccNo(fromAccountNo).firstOrNull()
            val toAccount = accountRepository.getAccountAsFlowByAccNo(toAccountNo).firstOrNull()

            if (fromAccount == null || toAccount == null) {
                return@withTransaction TransactionResult.Error.AccountNotFound
            }

            if (fromAccount.balance < amount) {
                return@withTransaction TransactionResult.Error.InsufficientBalance
            }

            val fromUser = userRepository.getUserByUserId(fromAccount.userId.toString())
                ?: return@withTransaction TransactionResult.Error.UnKnown

            val amountInUsd = getAmountInUsd(amount, CurrencyUtils.getCurrencyCode(fromUser.countryCode))

            if(amountInUsd == BigDecimal.ZERO) {
                return@withTransaction TransactionResult.Error.ExchangeRatesNotFound
            }

            val velocityError = performVelocityCheck(
                accNo = fromAccountNo,
                accountType = fromAccount.accountType,
                transactionType = TransactionType.INTERNATIONAL_TRANSFER,
                amountInUsd = amountInUsd
            )
            if (velocityError != null) {
                return@withTransaction velocityError
            }

            val transaction = createTransaction(
                TransactionType.INTERNATIONAL_TRANSFER,
                TransactionStatus.PENDING,
                idempotencyKey
            ) ?: return@withTransaction TransactionResult.Error.RepeatedTransaction

            val convertedAmount = amount.multiply(exchangeRate)

            accountRepository.withdraw(amount, BankDateFactory.now().epochMillis, fromAccountNo)

            val isDepositSuccessful = accountRepository.deposit(
                convertedAmount,
                BankDateFactory.now().epochMillis,
                toAccountNo
            )

            if (isDepositSuccessful == 0) {
                val failedTransaction = getUpdatedTransaction(transaction, TransactionStatus.FAILED)
                transactionDao.update(failedTransaction)
                return@withTransaction TransactionResult.Error.UnKnown
            }

            val fromLedger = Ledger(
                transactionId = transaction.transactionId,
                accNo = fromAccount.accNo,
                direction = LedgerDirection.DEBIT,
                amount = amount,
                balanceAfter = fromAccount.balance - amount,
                amountInUsd = amountInUsd
            )

            val toLedger = Ledger(
                transactionId = transaction.transactionId,
                accNo = toAccount.accNo,
                direction = LedgerDirection.CREDIT,
                amount = convertedAmount,
                balanceAfter = toAccount.balance + convertedAmount,
                amountInUsd = amountInUsd
            )

            ledgerRepository.insertAll(listOf(fromLedger, toLedger))

            val updatedTransaction = getUpdatedTransaction(transaction, TransactionStatus.COMPLETED)
            transactionDao.update(updatedTransaction)

            TransactionResult.Success(updatedTransaction.transactionId)
        }
    }

    suspend fun addInterest(
        accNo: Long,
        interestAmount: BigDecimal,
        interestDate: Long
    ): TransactionResult = withContext(Dispatchers.IO) {
        return@withContext database.withTransaction {

            val account = accountRepository.getAccountAsFlowByAccNo(accNo).firstOrNull()
                ?: return@withTransaction TransactionResult.Error.AccountNotFound

            val idempotencyKey = "INT_${accNo}_$interestDate"

            val transaction = createTransaction(
                transactionType = TransactionType.INTEREST_ADDITION,
                transactionStatus = TransactionStatus.PENDING,
                idempotencyKey = idempotencyKey
            ) ?: return@withTransaction TransactionResult.Error.RepeatedTransaction

            val isInterestUpdateSuccessful = accountRepository.updateBalanceAndInterestDate(
                newBalance = interestAmount,
                newInterestDate = interestDate,
                accNo =accNo,
                updateAt = BankDateFactory.now().epochMillis
            )

            if (isInterestUpdateSuccessful == 0) {
                val failedTransaction = getUpdatedTransaction(transaction, TransactionStatus.FAILED)
                transactionDao.update(failedTransaction)
                return@withTransaction TransactionResult.Error.UnKnown
            }

            val newBalance = account.balance.add(interestAmount)
            val interestLedger = Ledger(
                transactionId = transaction.transactionId,
                accNo = accNo,
                direction = LedgerDirection.CREDIT,
                amount = interestAmount,
                balanceAfter = newBalance,
                amountInUsd = BigDecimal.ZERO,
                createdAt = interestDate
            )

            ledgerRepository.insertAll(listOf(interestLedger))

            val updatedTransaction = getUpdatedTransaction(transaction, TransactionStatus.COMPLETED)
            transactionDao.update(updatedTransaction)

            TransactionResult.Success(updatedTransaction.transactionId)
        }
    }

    suspend fun getAccountVelocityStatus(accNo: Long, user: User): AccountVelocityStatus? {
        return withContext(Dispatchers.IO) {
            val account = accountRepository.getAccountAsFlowByAccNo(accNo).firstOrNull() ?: return@withContext null


            val dayInMs = BankDateTime.DAY_IN_MINUTES
            val startTime = BankDateFactory.now().epochMillis - dayInMs

            val count = ledgerRepository.getTransactionCountSince(accNo, startTime)

            val spentInUsd = ledgerRepository.getTotalSpentInUsdSince(accNo, startTime) ?: BigDecimal.ZERO

            val rates = currencyExchangeRepository.getLatestRates()?.rates
            val userCurrency = CurrencyUtils.getCurrencyCode(user.countryCode)
            val usdRate = rates?.get("USD") ?: 1.0
            val targetRate = rates?.get(userCurrency) ?: 1.0

            val spentInLocal = (spentInUsd.toDouble() / usdRate) * targetRate

            AccountVelocityStatus(
                moneySpentToday = BigDecimal(spentInLocal),
                dailySpendLimit = account.accountType.dailyTransactionLimit,
                transactionsToday = count,
                maxTransactions = account.accountType.dailyTransactionCount,
                accountType = account.accountType
            )
        }
    }
}





