package ru.spbstu.feature.auth.presentation

import android.annotation.SuppressLint
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.beautycoder.pflockscreen.views.PFCodeView
import ru.spbstu.common.base.BaseFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extenstions.setDebounceClickListener
import ru.spbstu.common.extenstions.setLightStatusBar
import ru.spbstu.common.extenstions.setStatusBarColor
import ru.spbstu.common.extenstions.viewBinding
import ru.spbstu.feature.R
import ru.spbstu.feature.databinding.FragmentAuthBinding
import ru.spbstu.feature.di.FeatureApi
import ru.spbstu.feature.di.FeatureComponent
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@SuppressLint("NewApi")
class AuthFragment : BaseFragment<AuthViewModel>(
    R.layout.fragment_auth,
) {
    override val binding by viewBinding(FragmentAuthBinding::bind)

    override fun setupViews() {
        super.setupViews()
        requireActivity().setStatusBarColor(R.color.background_secondary)
        requireView().setLightStatusBar()

        binding.frgAuthCodeView.setListener(object : PFCodeView.OnPFCodeListener {
            override fun onCodeCompleted(code: String?) {
                Log.d("qwerty", "code completed: $code")
                //check code then login
                if (viewModel.getCode().isEmpty()) {
                    viewModel.saveCode(code ?: "")
                    viewModel.openMainFragment()
                    Toast.makeText(requireContext(), R.string.successful_login, Toast.LENGTH_SHORT).show()
                } else {
                    val authCode = viewModel.getCode()
                    if (authCode != code) {
                        binding.frgAuthCodeView.clearCode()
                        Toast.makeText(requireContext(), R.string.wrong_code, Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.openMainFragment()
                        Toast.makeText(requireContext(), R.string.successful_login, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCodeNotCompleted(code: String?) {
                binding.frgAuthIvButton.setImageResource(
                    if (code?.length!! > 1 || viewModel.getCode().isEmpty()) R.drawable.ic_backspace_24 else R.drawable.ic_fingerprint_24
                )
            }
        })
        if (viewModel.getCode().isEmpty()) {
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
            binding.frgAuthTvTitle.setText(R.string.create_code)
        } else {
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_fingerprint_24)
            binding.frgAuthTvTitle.setText(R.string.login)
        }
        binding.frgAuthCard0.setDebounceClickListener {
            binding.frgAuthCodeView.input("0")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard1.setDebounceClickListener {
            binding.frgAuthCodeView.input("1")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard2.setDebounceClickListener {
            binding.frgAuthCodeView.input("2")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard3.setDebounceClickListener {
            binding.frgAuthCodeView.input("3")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard4.setDebounceClickListener {
            binding.frgAuthCodeView.input("4")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard5.setDebounceClickListener {
            binding.frgAuthCodeView.input("5")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard6.setDebounceClickListener {
            binding.frgAuthCodeView.input("6")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard7.setDebounceClickListener {
            binding.frgAuthCodeView.input("7")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard8.setDebounceClickListener {
            binding.frgAuthCodeView.input("8")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCard9.setDebounceClickListener {
            binding.frgAuthCodeView.input("9")
            binding.frgAuthIvButton.setImageResource(R.drawable.ic_backspace_24)
        }
        binding.frgAuthCardButton.setDebounceClickListener {
            if (binding.frgAuthCodeView.code.isNotEmpty() || viewModel.getCode().isEmpty()) {
                binding.frgAuthCodeView.delete()
            } else {
                if (isBiometricCompatibleDevice()) {
                    getBiometricPromptHandler().authenticate(
                        getBiometricPrompt(),
                        BiometricPrompt.CryptoObject(getCipher())
                    )
                }
                //fingerprint
            }
        }
        if (isBiometricCompatibleDevice()) {
            generateSecretKey()
        }
    }

    override fun inject() {
        FeatureUtils.getFeature<FeatureComponent>(this, FeatureApi::class.java)
            .authComponentFactory()
            .create(this)
            .inject(this)
    }

    private fun isBiometricCompatibleDevice(): Boolean {
        return getBiometricManager().canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun getBiometricManager(): BiometricManager {
        return BiometricManager.from(requireContext())
    }

    private fun generateSecretKey() {
        var keyGenerator: KeyGenerator? = null
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(false)
            .build()
        keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE
        )
        if (keyGenerator != null) {
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey? {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        var secretKey: Key? = null

        if (keyStore != null) {
            keyStore.load(null)
            secretKey = keyStore.getKey(KEY_NAME, null)
        }
        return secretKey as SecretKey?
    }

    private fun getCipher(): Cipher {
        val cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + FORWARD_SLASH
                    + KeyProperties.BLOCK_MODE_GCM + FORWARD_SLASH
                    + KeyProperties.ENCRYPTION_PADDING_NONE
        )
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        return cipher
    }

    private fun getBiometricPrompt(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Биометрический вход в приложение")
            .setSubtitle("Авторизация с помощью отпечатка пальца")
            .setNegativeButtonText("Отмена")
            .setConfirmationRequired(false)
            .build()
    }

    private fun encryptData(plaintext: String, cipher: Cipher): ByteArray {
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
        return ciphertext
    }

    private fun decryptData(ciphertext: ByteArray, cipher: Cipher): String {
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charset.forName("UTF-8"))
    }

    private fun onBiometricSuccess() {
        //Call the respective API on biometric success
        viewModel.openMainFragment()
        Toast.makeText(requireContext(), R.string.successful_login, Toast.LENGTH_SHORT).show()
        //callLoginApi("userName", "password")
    }

    private fun getBiometricPromptHandler(): BiometricPrompt {
        return BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onBiometricSuccess()
                }
            }
        )
    }

    companion object {
        private const val KEY_NAME = "KeyName"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val FORWARD_SLASH = "/"
    }
}