package com.example.bankapp.services
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


object PasswordHashingService {
        private const val ITERATIONS = 100_000
        private const val KEY_LENGTH = 256
        private const val SALT_LENGTH = 16

       suspend fun hash(password: String): String = withContext(Dispatchers.IO) {
            val salt = ByteArray(SALT_LENGTH)
            SecureRandom().nextBytes(salt)

            val spec = PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
            )

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val hash = factory.generateSecret(spec).encoded

           return@withContext "$ITERATIONS:${Base64.encodeToString(salt, Base64.NO_WRAP)}:${
               Base64.encodeToString(hash, Base64.NO_WRAP)
           }"
        }

        suspend fun matches(
            password: String,
            stored: String): Boolean = withContext(Dispatchers.IO) {

            val parts = stored.split(":")
            val iterations = parts[0].toInt()
            val salt = Base64.decode(parts[1], Base64.NO_WRAP)
            val expected = Base64.decode(parts[2], Base64.NO_WRAP)

            val spec = PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                expected.size * 8
            )

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val hash = factory.generateSecret(spec).encoded

            return@withContext MessageDigest.isEqual(hash, expected)
        }

}
