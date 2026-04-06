package com.example.bankapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bankapp.entities.dbtables.Transaction
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Query(
        """
    SELECT
        t.transactionType AS transaction_type,
        t.createdAt AS transaction_date,
        te.direction AS direction,
        te.amount AS amount,
        
        -- Logic for Counterparty Name vs Nickname
        b.nickname AS counterparty_nickname,
        COALESCE(u2.userName, 'Bank') AS counterparty_name,
        u2.pfpURL AS counterparty_pfp,
        
        t.transactionId AS transaction_id,
        t.referenceNumber AS reference_number,
        t.transactionStatus AS transaction_status,
        t.updatedAt AS updated_at,
        t.failureType AS failure_type,
        te.direction AS ledger_direction,
        te.balanceAfter AS balance_after,
        
        a1.accNo AS my_account_no,
        a1.ifscCode AS my_ifsc_code,
        u1.userName AS my_user_name,
        u1.pfpURL AS my_pfp_url,
        
        a2.accNo AS counterparty_account_no,
        a2.ifscCode AS counterparty_ifsc_code

    FROM ledger_entries te
    JOIN transactions t ON te.transactionId = t.transactionId
    JOIN accounts a1 ON te.accNo = a1.accNo
    JOIN users u1 ON a1.userId = u1.userId
    
    
    LEFT JOIN ledger_entries te2 ON te.transactionId = te2.transactionId AND te.accNo != te2.accNo
    LEFT JOIN accounts a2 ON te2.accNo = a2.accNo
    LEFT JOIN users u2 ON a2.userId = u2.userId
    

    LEFT JOIN beneficiaries b ON u1.userId = b.userId AND u2.userId = b.beneficiaryUserId

    WHERE te.accNo = :accNo
    
    
    AND (:dateFilter = 0 OR t.createdAt BETWEEN :startDate AND :endDate)
    
    
    AND (:searchQuery = '' 
        OR (b.nickname LIKE '%' || :searchQuery || '%')
        OR (u2.userName LIKE '%' || :searchQuery || '%')
        OR (t.transactionType = 'DEPOSIT' AND (
            'Deposit' LIKE '%' || :searchQuery || '%' OR 
            'You' LIKE '%' || :searchQuery || '%' OR 
            u1.userName LIKE '%' || :searchQuery || '%'
        ))
        OR t.referenceNumber LIKE '%' || :searchQuery || '%'
        OR t.transactionId LIKE '%' || :searchQuery || '%'
    )
         
    AND (:typeFilter = 0 OR t.transactionType IN (:types))
    AND (:directionFilter = 0 OR te.direction IN (:directions))
    AND (:statusFilter = 0 OR t.transactionStatus IN (:statuses))
    AND NOT (t.transactionStatus = 'FAILED' AND te.direction = 'CREDIT')
    
    ORDER BY 
        CASE WHEN :sortOrder = 'NEWEST' THEN t.createdAt END DESC,
        CASE WHEN :sortOrder = 'OLDEST' THEN t.createdAt END ASC,
       
        CASE 
            WHEN :searchQuery != '' AND b.nickname LIKE :searchQuery || '%' THEN 1
            WHEN :searchQuery != '' AND u2.userName LIKE :searchQuery || '%' THEN 2
            ELSE 3 
        END ASC
"""
    )
    fun getFilteredTransactions(
        accNo: Long,
        searchQuery: String = "",
        types: List<String>,
        typeFilter: Int,
        directions: List<String>,
        directionFilter: Int,
        statuses: List<String>,
        statusFilter: Int,
        sortOrder: String,
        startDate: Long,
        endDate: Long,
        dateFilter: Int
    ): Flow<List<TransactionHistoryItemDto>>

    @Query(
        """
    SELECT
        t.transactionType AS transaction_type,
        t.createdAt AS transaction_date,
        te.amount AS amount,

        b.nickname AS counterparty_nickname,
        COALESCE(u2.userName, 'Bank') AS counterparty_name,
        u2.pfpURL AS counterparty_pfp,
        
        t.transactionId AS transaction_id,
        t.referenceNumber AS reference_number,
        t.transactionStatus AS transaction_status,
        t.updatedAt AS updated_at,
        t.failureType AS failure_type,
        te.direction AS ledger_direction,
        te.balanceAfter AS balance_after,
        
        a1.accNo AS my_account_no,
        a1.ifscCode AS my_ifsc_code,
        u1.userName AS my_user_name,
        u1.pfpURL AS my_pfp_url,
        
        a2.accNo AS counterparty_account_no,
        a2.ifscCode AS counterparty_ifsc_code

    FROM ledger_entries te
    
    JOIN transactions t 
        ON te.transactionId = t.transactionId
    
    JOIN accounts a1 
        ON te.accNo = a1.accNo
    
    JOIN users u1 
        ON a1.userId = u1.userId
    

    LEFT JOIN ledger_entries te2 
        ON te.transactionId = te2.transactionId 
        AND te.accNo != te2.accNo
    
    LEFT JOIN accounts a2 
        ON te2.accNo = a2.accNo
    
    LEFT JOIN users u2 
        ON a2.userId = u2.userId


    LEFT JOIN beneficiaries b 
        ON u1.userId = b.userId 
        AND u2.userId = b.beneficiaryUserId

    WHERE t.transactionId = :transactionId
    AND te.accNo = :accNo 
    AND NOT (t.transactionStatus = 'FAILED' AND te.direction = 'CREDIT')
"""
    )
    suspend fun getTransactionHistoryItemById(
        transactionId: String,
        accNo: Long
    ): TransactionHistoryItemDto?
}