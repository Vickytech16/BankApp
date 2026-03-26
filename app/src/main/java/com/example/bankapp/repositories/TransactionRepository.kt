package com.example.bankapp.repositories

import androidx.room.withTransaction
import com.example.bankapp.core.BankDatabase
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.daos.AccountDao
import com.example.bankapp.daos.LedgerDao
import com.example.bankapp.daos.TransactionDao
import com.example.bankapp.entities.dbtables.Account
import com.example.bankapp.entities.dbtables.Ledger
import com.example.bankapp.entities.dbtables.Transaction
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.transaction.LedgerDirection
import com.example.bankapp.entities.types.transaction.TransactionFailureType
import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.math.BigDecimal

import java.util.UUID

class TransactionRepository(
   private val transactionDao: TransactionDao,
   private val ledgerDao: LedgerDao,
   private val accountDao: AccountDao,
   private val database: BankDatabase
){
  fun getFilteredTransactions(
      accNo: Long,
      searchQuery: String = "",
      types: List<String> = emptyList(),
      typeFilter: Int = 0,
      directions: List<String> = listOf("CREDIT", "DEBIT"),
      directionFilter: Int = 0,
      statuses: List<String> = emptyList(),
      statusFilter: Int = 0,
      sortOrder: String = "NEWEST"

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
            sortOrder = sortOrder
        )
    }

    suspend fun getTransactionHistoryByTransactionId(transactionId: String, accNo: Long): TransactionHistoryItemDto?{
        return withContext(Dispatchers.IO){
            transactionDao.getTransactionHistoryItemById(transactionId, accNo)
        }
    }

    suspend fun checkIfAccountExists(accNo: Long): Boolean{
        return withContext(Dispatchers.IO){
            accountDao.getAccountAsFlowByAccNo(accNo).firstOrNull() != null
        }
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
                accountDao.getAccountAsFlowByAccNo(fromAccountNo).firstOrNull()
            val toAccount: Account? =
                accountDao.getAccountAsFlowByAccNo(toAccountNo).firstOrNull()

            if (fromAccount == null || toAccount == null) {
                return@withTransaction TransactionResult.Error.AccountNotFound
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
                    TransactionFailureType.INSUFFICIENT_BALANCE
                )
                transactionDao.update(failedTransaction)

                val fromLedger = Ledger(
                    transactionId = transaction.transactionId,
                    accNo = fromAccount.accNo,
                    direction = LedgerDirection.DEBIT,
                    amount = amount,
                    balanceAfter = fromAccount.balance
                )

                val toLedger = Ledger(
                    transactionId = transaction.transactionId,
                    accNo = toAccount.accNo,
                    direction = LedgerDirection.CREDIT,
                    amount = amount,
                    balanceAfter = toAccount.balance
                )

                ledgerDao.insertAll(listOf(fromLedger, toLedger))

                return@withTransaction TransactionResult.Error.InsufficientBalance
            }

            accountDao.withdraw(amount, BankDateFactory.now().epochMillis, fromAccountNo)

            transactionDao.update(updatedTransaction)

            val isDepositSuccessful =
                accountDao.deposit(amount, BankDateFactory.now().epochMillis, toAccountNo)
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
                balanceAfter = fromAccount.balance - amount
            )

            val toLedger = Ledger(
                transactionId = transaction.transactionId,
                accNo = toAccount.accNo,
                direction = LedgerDirection.CREDIT,
                amount = amount,
                balanceAfter = toAccount.balance + amount
            )

            ledgerDao.insertAll(listOf(fromLedger, toLedger))

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
                accountDao.getAccountAsFlowByAccNo(accNo = accountNo).firstOrNull()
                    ?: return@withTransaction TransactionResult.Error.AccountNotFound

            val transaction = createTransaction(
                transactionType = TransactionType.DEPOSIT,
                transactionStatus = TransactionStatus.PENDING,
                idempotencyKey = idempotencyKey
            ) ?: return@withTransaction TransactionResult.Error.RepeatedTransaction

            var updatedTransaction =
                getUpdatedTransaction(transaction, TransactionStatus.COMPLETED)

            val isDepositSuccessful =
                accountDao.deposit(amount, BankDateFactory.now().epochMillis, account.accNo)

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
                amount = amount
            )

            ledgerDao.insertAll(listOf(ledger))

            transactionDao.update(updatedTransaction)

            TransactionResult.Success(updatedTransaction.transactionId)
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

    private suspend fun createTransaction(
        transactionType: TransactionType,
        transactionStatus: TransactionStatus,
        idempotencyKey: String): Transaction? {
        var tries = 3
        var transaction: Transaction?
        do{
            transaction = Transaction(
                transactionId =UUID.randomUUID().toString(),
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
}

private fun generateReferenceNumber(): String {
    return "TXN" + UUID.randomUUID()
        .toString()
        .substring(0, 8)
        .uppercase()
}

