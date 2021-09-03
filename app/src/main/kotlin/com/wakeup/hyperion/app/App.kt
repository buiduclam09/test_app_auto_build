package com.wakeup.hyperion.app

import android.app.Application
import com.wakeup.hyperion.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 8/7/20.
 */

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        configTimber()
    }

    private fun configTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
