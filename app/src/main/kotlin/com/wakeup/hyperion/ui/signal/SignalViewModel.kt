package com.wakeup.hyperion.ui.signal

import com.wakeup.hyperion.common.base.BaseViewModel
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.utils.ads.BannerAdsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/1/21.
 */
@HiltViewModel
class SignalViewModel @Inject constructor(
    val sharedPrefsRepository: SharedPrefsRepository,
    private val adsManager: BannerAdsManager
) : BaseViewModel() {
    val signalLocalModel: SignalLocalModel?
        get() = sharedPrefsRepository.getSignalSound()

    val signal: String
        get() = sharedPrefsRepository.getSignal()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun showAds() {
        adsManager.show {

        }
    }
}