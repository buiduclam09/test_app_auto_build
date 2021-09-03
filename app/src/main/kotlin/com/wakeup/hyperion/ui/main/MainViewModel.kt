package com.wakeup.hyperion.ui.main

import com.wakeup.hyperion.common.base.BaseViewModel
import androidx.hilt.lifecycle.ViewModelInject
import com.wakeup.hyperion.data.repository.DefaultSharedPrefsRepository
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import com.wakeup.hyperion.utils.liveData.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 8/8/20.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPrefsRepository: SharedPrefsRepository
) : BaseViewModel() {

    val updateVolume = SingleLiveData<Unit>()
    val signal: String
        get() = sharedPrefsRepository.getSignal()
}
