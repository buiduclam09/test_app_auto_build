package com.wakeup.hyperion.ui.turnOff

import com.wakeup.hyperion.common.base.BaseViewModel
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import com.wakeup.hyperion.model.entity.SignalLocalModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/9/21.
 */
@HiltViewModel
class TurnOffViewModel @OptIn(ExperimentalTime::class)
@Inject constructor(
    private val sharedPrefsRepository: SharedPrefsRepository
) : BaseViewModel() {

    val signalLocalModel: SignalLocalModel?
        get() = sharedPrefsRepository.getSignalSound()
    val signal: String
        get() = sharedPrefsRepository.getSignal()
}