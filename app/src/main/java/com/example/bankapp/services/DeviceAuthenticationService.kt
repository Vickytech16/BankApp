package com.example.bankapp.services


/*
   TODO - THIS IS TODO, PLEASE IGNORE
 */

//import android.hardware.biometrics.BiometricManager
//import android.hardware.biometrics.BiometricPrompt
//import android.os.Build
//import android.security.keystore.KeyGenParameterSpec
//import android.security.keystore.KeyProperties
//import androidx.annotation.RequiresApi
//import java.security.KeyStore
//import javax.crypto.KeyGenerator
//import javax.crypto.SecretKey
//import javax.crypto.Cipher
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.FragmentActivity
//import androidx.biometric.BiometricManager
//import androidx.biometric.BiometricPrompt
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.FragmentActivity
//
//object ModernAuthService {
//    private const val KEY_ALIAS = "bank_app_modern_key"
//    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
//
//    @RequiresApi(Build.VERSION_CODES.R)
//    fun generateModernKey() {
//        val keyGenerator = KeyGenerator.getInstance(
//            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
//        )
//
//        // API 30+ Builder
//        val builder = KeyGenParameterSpec.Builder(
//            KEY_ALIAS,
//            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//        )
//            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
//            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//            .setUserAuthenticationRequired(true)
//            // This is the API 30+ specific call
//            .setUserAuthenticationParameters(
//                10, // Validity duration in seconds
//                KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
//            )
//            .setInvalidatedByBiometricEnrollment(true)
//            .build()
//
//        keyGenerator.init(builder)
//        keyGenerator.generateKey()
//    }
//
//    fun getInitializedCipher(): Cipher {
//        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
//        val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
//        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
//        return cipher
//    }
//}
//
//
//
//
//// Internal to package
//class ModernBioPrompt(private val activity: FragmentActivity) {
//
//    @RequiresApi(Build.VERSION_CODES.P)
//    fun showPrompt(
//        cipher: javax.crypto.Cipher,
//        onSuccess: () -> Unit,
//        onFail: () -> Unit
//    ) {
//        val executor = ContextCompat.getMainExecutor(activity)
//
//        val biometricPrompt = BiometricPrompt(activity, executor,
//            object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                    onSuccess()
//                }
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    onFail()
//                }
//            })
//
//        val promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Bank Secure Login")
//            .setSubtitle("Use biometrics to access your account")
//            // MODERN API 30+ WAY:
//            .setAllowedAuthenticators(
//                BiometricManager.Authenticators.BIOMETRIC_STRONG or
//                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
//            )
//            .build()
//
//        // Pass the CryptoObject to link the UI to the Hardware Key
//        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
//    }
//}