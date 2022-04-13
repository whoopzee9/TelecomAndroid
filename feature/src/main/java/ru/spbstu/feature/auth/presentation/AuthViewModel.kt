package ru.spbstu.feature.auth.presentation

import ru.spbstu.common.token.TokenRepository
import ru.spbstu.common.utils.BackViewModel
import ru.spbstu.feature.FeatureRouter

class AuthViewModel(val router: FeatureRouter, private val tokenRepository: TokenRepository) :
    BackViewModel(router) {

    fun getCode(): String {
        return tokenRepository.getAuthCode()
    }

    fun saveCode(code: String) {
        tokenRepository.setAuthCode(code)
    }

    fun openMainFragment() {
        router.openMainFragment()
    }
}