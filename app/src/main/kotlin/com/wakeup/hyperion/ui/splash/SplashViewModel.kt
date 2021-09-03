package com.wakeup.hyperion.ui.splash

import com.wakeup.hyperion.common.base.BaseViewModel
import com.wakeup.hyperion.data.repository.DefaultSharedPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPrefsRepository: DefaultSharedPrefsRepository
) : BaseViewModel() {
    fun getLanguageLocal(): String? {
        return sharedPrefsRepository.getLanguage()
    }

    fun setLanguage(language: String) {
        sharedPrefsRepository.saveLanguage(language)
    }
}