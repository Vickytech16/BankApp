package com.example.bankapp.services

import java.security.SecureRandom

object RecoverKeyGenerationService {
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        private val random = SecureRandom()

        fun generateRecoveryKey(): String {
            return (1..10)
                .map { random.nextInt(charPool.size).let { charPool[it] } }
                .joinToString("")
        }
}