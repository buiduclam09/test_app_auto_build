package com.wakeup.hyperion.ui.main

import android.database.ContentObserver
import android.os.Handler

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/11/21.
 */

class SettingsContentObserver(handler: Handler?, private val onChangeListener: () -> Unit) : ContentObserver(handler) {

    override fun deliverSelfNotifications(): Boolean {
        return super.deliverSelfNotifications()
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        onChangeListener.invoke()
    }
}
