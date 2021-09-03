package com.wakeup.hyperion.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefs
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefsKey
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefsKey.KEY_IS_FIRST_LAUNCHER
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefsKey.KEY_LANGUAGE
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefsKey.KEY_LIST_SOUND
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefsKey.SIGNAL_SOUND
import com.wakeup.hyperion.model.entity.SignalLocalModel
import javax.inject.Inject

interface SharedPrefsRepository {
    fun saveSignal(signal: String)
    fun getSignal(): String
    fun saveListPath(path: MutableList<String>)
    fun getListPath(): MutableList<String>?
    fun clearListPath()
    fun saveSignalSound(signal: SignalLocalModel)
    fun getSignalSound(): SignalLocalModel?
    fun clearSignalSound()
    fun saveIsFirstLauncher(isFirst: Boolean)
    fun getIsFirstLauncher(): Boolean
    fun saveLanguage(language : String?)
    fun getLanguage() : String?
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

    override fun saveListPath(path: MutableList<String>) {
        val type = object : TypeToken<MutableList<String>>() {}.type
        val data = Gson().toJson(path, type)
        sharedPrefsApi.put(KEY_LIST_SOUND, data)
    }

    override fun getListPath(): MutableList<String>? {
        val data: String = sharedPrefsApi.get(KEY_LIST_SOUND, String::class.java)
        if (data.isBlank()) return null
        val type = object : TypeToken<MutableList<String>>() {}.type
        return Gson().fromJson<MutableList<String>>(data, type)
    }

    override fun clearListPath() {
        return sharedPrefsApi.clearKey(KEY_LIST_SOUND)
    }

    override fun saveSignalSound(signal: SignalLocalModel) {
        return sharedPrefsApi.put(SIGNAL_SOUND, signal)
    }

    override fun getSignalSound(): SignalLocalModel {
        return sharedPrefsApi.get(SIGNAL_SOUND, SignalLocalModel::class.java)
    }

    override fun clearSignalSound() {
        sharedPrefsApi.clearKey(SIGNAL_SOUND)
    }

    override fun saveIsFirstLauncher(isFirst: Boolean) {
        sharedPrefsApi.put(KEY_IS_FIRST_LAUNCHER, isFirst)
    }

    override fun getIsFirstLauncher(): Boolean {
        return sharedPrefsApi.get(KEY_IS_FIRST_LAUNCHER, Boolean::class.java)
    }

    override fun saveLanguage(language: String?) {
        sharedPrefsApi.put(KEY_LANGUAGE, language)
    }

    override fun getLanguage(): String? {
       return sharedPrefsApi.get(KEY_LANGUAGE, String::class.java)
    }

}
