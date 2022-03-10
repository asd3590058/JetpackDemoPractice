package com.my.jetpackdemopractice.biometric

import androidx.biometric.BiometricPrompt

interface BiometricAuthListener {

    fun onBiometricAuthenticateError(error: Int,errMsg: String)
    fun onBiometricAuthenticateSuccess(result: BiometricPrompt.AuthenticationResult)
    fun onBiometricAuthenticateFailed()

}