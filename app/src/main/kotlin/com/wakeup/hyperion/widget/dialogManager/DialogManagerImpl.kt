package com.wakeup.hyperion.widget.dialogManager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.internal.managers.ViewComponentManager
import java.lang.ref.WeakReference

class DialogManagerImpl(ctx: Context?) : DialogManager {

    private var context: WeakReference<Context?>? = null
    private var progressDialog: ProgressDialog? = null

    init {
        context = WeakReference(ctx).apply {
            progressDialog = ProgressDialog(this.get()!!) // ktlint-disable
        }
    }

    private fun activityContext(): AppCompatActivity {
        return if (context?.get() is ViewComponentManager.FragmentContextWrapper) {
            (context?.get() as ViewComponentManager.FragmentContextWrapper).baseContext as AppCompatActivity
        } else context?.get() as AppCompatActivity
    }

    override fun showLoading() {
        progressDialog?.show()
    }

    override fun showProcessing() {
        progressDialog?.show()
    }

    override fun hideLoading() {
        progressDialog?.dismiss()
    }

    override fun onRelease() {
        progressDialog = null
    }

    override fun showAlertDialog(
        title: String,
        message: String,
        titleButton: String,
        listener: DialogAlert.Companion.OnButtonClickedListener?
    ) {
        val dialog = DialogAlert.newInstance(
                title, message, titleButton, listener
        )
        val fm = activityContext().supportFragmentManager
        dialog.show(fm, DialogAlert::class.java.simpleName)
    }

    override fun showAlertDialog(
        title: String,
        message: String,
        titleButton: String,
        buttonBgColor: Int,
        buttonColor: Int,
        listener: DialogAlert.Companion.OnButtonClickedListener?
    ) {
        val dialog = DialogAlert.newInstance(
                title, message, titleButton, listener
        )
        val fm = activityContext().supportFragmentManager
        dialog.show(fm, DialogAlert::class.java.simpleName)
    }

    override fun showConfirmDialog(
        title: String?,
        message: String?,
        titleButtonPositive: String,
        titleButtonNegative: String,
        listener: DialogConfirm.OnButtonClickedListener?
    ) {
        val dialog = DialogConfirm.newInstance(
            title, message, titleButtonPositive,
            titleButtonNegative, listener
        )
        val fm = activityContext().supportFragmentManager
        dialog.show(fm, DialogAlert::class.java.simpleName)
    }
}
