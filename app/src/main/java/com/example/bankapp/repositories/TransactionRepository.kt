package com.example.bankapp.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.withTransaction
import com.example.bankapp.core.BankDatabase
import com.example.bankapp.daos.AccountDao
import com.example.bankapp.daos.LedgerDao
import com.example.bankapp.daos.TransactionDao
import com.example.bankapp.entities.Account
import com.example.bankapp.entities.Ledger
import com.example.bankapp.entities.Transaction
import com.example.bankapp.entities.errors.TransactionResult
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.LedgerDirection
import com.example.bankapp.entities.types.TransactionFailureType
import com.example.bankapp.entities.types.TransactionStatus
import com.example.bankapp.entities.types.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class TransactionRepository(
   private val transactionDao: TransactionDao,
   private val ledgerDao: LedgerDao,
   private val accountDao: AccountDao,
   private val database: BankDatabase
){


  fun getAllTransactions(accNo: Long): Flow<List<TransactionHistoryItemDto>> {
        return transactionDao.getAllTransactionsForAccount(accNo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
   suspend fun cashTransfer(
        fromAccountNo: Long,
        toAccountNo: Long,
        amount: BigDecimal,
        idempotencyKey: String
    ): TransactionResult {

       return database.withTransaction {

           if (amount <= BigDecimal.ZERO) {
               return@withTransaction TransactionResult.Error.InvalidAmount
           }

           if (fromAccountNo == toAccountNo)
               return@withTransaction TransactionResult.Error.SameAccountTransfer
           val fromAccount: Account? = accountDao.getAccountByAccNo(fromAccountNo)
           val toAccount: Account? = accountDao.getAccountByAccNo(toAccountNo)

           if (fromAccount == null || toAccount == null) {
               return@withTransaction TransactionResult.Error.AccountNotFound
           }

           val transaction =
               createTransaction(TransactionType.CASH_TRANSFER, TransactionStatus.PENDING, idempotencyKey)
                   ?: return@withTransaction TransactionResult.Error.RepeatedTransaction

               var updatedTransaction =  getUpdatedTransaction(transaction, TransactionStatus.COMPLETED)

           if (fromAccount.balance < amount) {
               val failedTransaction = getUpdatedTransaction(
                   transaction,
                   TransactionStatus.FAILED,
                   TransactionFailureType.INSUFFICIENT_BALANCE
               )
               transactionDao.update(failedTransaction)
               return@withTransaction TransactionResult.Error.InsufficientBalance
           }

               accountDao.withdraw(amount, LocalDateTime.now(), fromAccountNo)

               transactionDao.update(updatedTransaction)
           
               val isDepositSuccessful = accountDao.deposit(amount, LocalDateTime.now(), toAccountNo)
               if(isDepositSuccessful==0){
                   updatedTransaction = getUpdatedTransaction(transaction, TransactionStatus.FAILED,
                       TransactionFailureType.UNKNOWN_ERROR)
                   transactionDao.update(updatedTransaction)
                   return@withTransaction TransactionResult.Error.unKnown
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deposit(
        accountNo: Long,
        amount: BigDecimal,
        idempotencyKey: String
    ): TransactionResult{

        return database.withTransaction {
            if (amount <= BigDecimal.ZERO) {
                return@withTransaction TransactionResult.Error.InvalidAmount
            }

            val account = accountDao.getAccountByAccNo(accNo = accountNo)
                ?: return@withTransaction TransactionResult.Error.AccountNotFound

//            val existingTransaction: Transaction? = transactionDao.getTransactionByIdempotencyKey(idempotencyKey)
//
//            if(existingTransaction!=null) {
//                return@withTransaction TransactionResult.Error.RepeatedTransaction
//            }

            val transaction = createTransaction(
                transactionType = TransactionType.DEPOSIT,
                transactionStatus = TransactionStatus.PENDING,
                idempotencyKey = idempotencyKey
            ) ?: return@withTransaction TransactionResult.Error.RepeatedTransaction

            var updatedTransaction = getUpdatedTransaction(transaction, TransactionStatus.COMPLETED)



                val isDepositSuccessful =
                    accountDao.deposit(amount, LocalDateTime.now(), account.accNo)
                if (isDepositSuccessful == 0){
                    updatedTransaction = getUpdatedTransaction(transaction, TransactionStatus.FAILED,
                        TransactionFailureType.UNKNOWN_ERROR)
                    transactionDao.update(updatedTransaction)
                    return@withTransaction TransactionResult.Error.unKnown
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


//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun getUpdatedAccount(balance: BigDecimal, account: Account): Account{
//        val newAccount: Account = account.copy(
//            userId = account.userId,
//            ifscCode = account.ifscCode,
//            accountType = account.accountType,
//            balance = balance,
//            createdAt = account.createdAt,
//            updatedAt = LocalDateTime.now()
//        )
//        return newAccount
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUpdatedTransaction(transaction: Transaction, transactionStatus: TransactionStatus, failureType: TransactionFailureType? = null): Transaction
    {
       val newTransaction = transaction.copy(
           transactionId = transaction.transactionId,
           referenceNumber = transaction.referenceNumber,
           transactionType = transaction.transactionType,
           transactionStatus = transactionStatus,
           createdAt = transaction.createdAt,
           updatedAt = LocalDateTime.now(),
           failureType = failureType
       )
        return newTransaction
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun createTransaction(transactionType: TransactionType, transactionStatus: TransactionStatus,idempotencyKey: String,  transactionFailureType: TransactionFailureType? = null): Transaction? {
        var tries = 3
        var transaction: Transaction?
        do{
            transaction = Transaction(
                transactionId =UUID.randomUUID().toString(),
                referenceNumber = generateReferenceNumber(),
                transactionType = transactionType,
                transactionStatus = transactionStatus,
                idempotencyKey = idempotencyKey,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
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

