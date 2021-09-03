package com.wakeup.hyperion.data.repository

import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefs
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefsKey
import javax.inject.Inject

interface SharedPrefsRepository {
    fun saveSignal(signal: String)
    fun getSignal(): String

}

class DefaultSharedPrefsRepository @Inject constructor
    (private val sharedPrefsApi: SharedPrefs) : SharedPrefsRepository {

    override fun saveSignal(signal: String) {
        sharedPrefsApi.put(SharedPrefsKey.SIGNAL, signal)
    }

    override fun getSignal(): String {
        val signal = sharedPrefsApi.get(SharedPrefsKey.SIGNAL, String::class.java)
        return if (signal.isBlank()) Constant.DEFAULT_SIGNAL else signal
    }

}
