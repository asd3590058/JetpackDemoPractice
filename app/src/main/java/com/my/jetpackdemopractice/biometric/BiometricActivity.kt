package com.my.jetpackdemopractice.biometric

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import com.my.jetpackdemopractice.CIPHERTEXT_WRAPPER
import com.my.jetpackdemopractice.R
import com.my.jetpackdemopractice.SHARED_PREFS_FILENAME
import com.my.jetpackdemopractice.biometric.BiometricUtils.isBiometricReady
import com.my.jetpackdemopractice.databinding.ActivityBiometricBinding
import com.my.jetpackdemopractice.databinding.ActivityMainBinding
import java.security.UnrecoverableKeyException
/**
*@package
*@author https://github.com/asd3590058
*@date
*@description  先需要BiometricLogin，之后再正常login。
*/
class BiometricActivity : AppCompatActivity(), BiometricAuthListener {
    private lateinit var mBinding: ActivityBiometricBinding
    private val cryptographyManager = CryptographyManager()
    private var tag = ""
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityBiometricBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.biometricLoginBtn.setOnClickListener {
            if (ciphertextWrapper != null && isBiometricReady(applicationContext)) {
                ciphertextWrapper?.let {
                    val secretKeyName = getString(R.string.secret_key_name)
                    val cipher = cryptographyManager.getInitializedCipherForDecryption(secretKeyName, it.initializationVector)
                    BiometricUtils.showBiometricPrompt(
                        activity = this,
                        listener = this,
                        cryptoObject = BiometricPrompt.CryptoObject(cipher),
                    )
                }
            }
        }
        mBinding.loginBtn.setOnClickListener {
            tag="login"
            if (isBiometricReady(applicationContext)) {
                try {
                    val secretKeyName = getString(R.string.secret_key_name)
                    val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
                    BiometricUtils.showBiometricPrompt(
                        activity = this,
                        listener = this,
                        cryptoObject = BiometricPrompt.CryptoObject(cipher),
                    )
                    Toast.makeText(this, "navigateToActivity!", Toast.LENGTH_SHORT).show()
                } catch (e: KeyPermanentlyInvalidatedException) {
                    Toast.makeText(this, "指纹库发生变化", Toast.LENGTH_SHORT).show()
                } catch (e: UnrecoverableKeyException) {
                    Toast.makeText(this, "指纹库发生变化", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBiometricAuthenticateError(error: Int, errMsg: String) =
        Toast.makeText(this, "Biometric Access Denied!", Toast.LENGTH_SHORT).show()

    override fun onBiometricAuthenticateSuccess(result: BiometricPrompt.AuthenticationResult) {
        if (tag == "login") {
            result.cryptoObject?.cipher?.apply {
                val encryptedServerTokenWrapper = cryptographyManager.encryptData("123456789", this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    applicationContext,
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }
        } else {
            ciphertextWrapper?.let { textWrapper ->
                result.cryptoObject?.cipher?.let {
                    cryptographyManager.decryptData(textWrapper.ciphertext, it)
                }
            }
        }
        Toast.makeText(this, "navigateToActivity!", Toast.LENGTH_SHORT).show()
    }

    override fun onBiometricAuthenticateFailed() = Toast.makeText(this, "failed!", Toast.LENGTH_SHORT).show()
}