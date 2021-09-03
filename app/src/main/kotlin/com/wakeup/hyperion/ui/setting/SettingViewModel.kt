package com.wakeup.hyperion.ui.setting

import com.wakeup.hyperion.common.base.BaseViewModel
import com.wakeup.hyperion.data.repository.DefaultSharedPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/1/21.
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val sharedPrefsRepository: DefaultSharedPrefsRepository
) : BaseViewModel() {

    fun getLanguageLocal(): String? {
        return sharedPrefsRepository.getLanguage()
    }

    fun setLanguage(language: String) {
        sharedPrefsRepository.saveLanguage(language)
    }
}