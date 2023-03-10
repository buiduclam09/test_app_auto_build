package com.wakeup.hyperion.common.base

import com.wakeup.hyperion.dialogManager.DialogAlert
import com.wakeup.hyperion.dialogManager.DialogConfirm

interface BaseView {

    fun showLoading(isShow: Boolean)
    fun showLoading()
    fun hideLoading()

    fun showAlertDialog(
        title: String = "",
        message: String = "",
        titleButton: String = "",
        listener: DialogAlert.Companion.OnButtonClickedListener? = null
    )

    fun showConfirmDialog(
        title: String? = "",
        message: String? = "",
        titleButtonPositive: String = "",
        titleButtonNegative: String = "",
        listener: DialogConfirm.OnButtonClickedListener? = null
    )
}
