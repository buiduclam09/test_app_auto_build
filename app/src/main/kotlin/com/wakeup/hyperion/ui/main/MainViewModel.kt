package com.wakeup.hyperion.ui.main

import androidx.lifecycle.viewModelScope
import com.wakeup.hyperion.common.base.BaseViewModel
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.utils.ads.BannerAdsManager
import com.wakeup.hyperion.utils.liveData.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 8/8/20.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPrefsRepository: SharedPrefsRepository,
    private val bannerAdsManager: BannerAdsManager
) : BaseViewModel() {
    val listAudio = mutableListOf<String>()
    val updateVolume = SingleLiveData<Unit>()
    val addNewSongFlow = MutableSharedFlow<Boolean>()
    val updateTabFlow = MutableSharedFlow<Boolean>()
    val signalLocalModel: SignalLocalModel?
        get() = sharedPrefsRepository.getSignalSound()

    val signal: String
        get() = sharedPrefsRepository.getSignal()

    fun getListPath() {
        sharedPrefsRepository.getListPath()?.let {
            listAudio.clear()
            listAudio.addAll(it)
        }
    }

    fun saveListUriAudio(list: MutableList<String>) {
        sharedPrefsRepository.clearListPath()
        sharedPrefsRepository.saveListPath(list)
    }

    fun saveSignalSound(signal: SignalLocalModel) {
        sharedPrefsRepository.saveSignalSound(signal)
        viewModelScope.launch {
            updateTabFlow.emit(!signal.isBasic)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun showAds(){
        bannerAdsManager.show {
        }
    }

    fun clearSignalSound() = sharedPrefsRepository.clearSignalSound()
}
