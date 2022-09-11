package com.wakeup.hyperion.ui.signal

import com.wakeup.hyperion.common.base.BaseViewModel
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import com.wakeup.hyperion.model.entity.SignalLocalModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/1/21.
 */
@HiltViewModel
class SignalViewModel @Inject constructor(
    val sharedPrefsRepository: SharedPrefsRepository
) : BaseViewModel() {
    val signalLocalModel: SignalLocalModel?
        get() = sharedPrefsRepository.getSignalSound()

    val signal: String
        get() = sharedPrefsRepository.getSignal()
    var isShowCusTomTab = false
    var isShowBasicTab = false
    var isShowCreateSignal = false
    fun countDownFlow(
        start: Long,
        delayInSeconds: Long = 2_000L,
    ) = flow {
        var count = start
        while (count >= 0L) {
            emit(count--)
            delay(delayInSeconds)
        }
    }


}