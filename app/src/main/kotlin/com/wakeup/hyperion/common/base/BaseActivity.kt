package com.wakeup.hyperion.common.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.observe
import androidx.viewbinding.ViewBinding
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import com.wakeup.hyperion.utils.extension.handleDefaultApiError
import com.wakeup.hyperion.dialogManager.DialogAlert
import com.wakeup.hyperion.dialogManager.DialogConfirm
import com.wakeup.hyperion.dialogManager.DialogManager
import com.wakeup.hyperion.dialogManager.DialogManagerImpl
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 8/5/20.
 *
 * @viewModel -> view model
 * @viewModelClass -> class view model
 * @viewBinding -> class binding
 * @initialize -> init UI, adapter, listener...
 * @onSubscribeObserver -> subscribe observer live data
 *
 */

abstract class BaseActivity<viewModel : BaseViewModel, viewBinding : ViewBinding>(viewModelClass: KClass<viewModel>) :
    AppCompatActivity(), BaseView {

    protected val viewModel by ViewModelLazy(
        viewModelClass = viewModelClass,
        storeProducer = { viewModelStore },
        factoryProducer = { defaultViewModelProviderFactory },
        extrasProducer = { defaultViewModelCreationExtras }
    )
    protected lateinit var viewBinding: viewBinding
    abstract fun inflateViewBinding(inflater: LayoutInflater): viewBinding

    protected abstract fun initialize()

    @Inject
    lateinit var sharedPrefsRepository: SharedPrefsRepository
    private lateinit var dialogManager: DialogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = inflateViewBinding(layoutInflater)
        dialogManager = DialogManagerImpl(this)
        setContentView(viewBinding.root)
        initialize()
        onSubscribeObserver()
    }

    override fun showLoading(isShow: Boolean) {
        if (isShow) showLoading() else hideLoading()
    }

    override fun showLoading() {
        dialogManager.showLoading()
    }

    override fun hideLoading() {
        dialogManager.hideLoading()
    }

    override fun showAlertDialog(
        title: String,
        message: String,
        titleButton: String,
        listener: DialogAlert.Companion.OnButtonClickedListener?
    ) {
        dialogManager.showAlertDialog(title, message, titleButton, listener)
    }

    override fun showConfirmDialog(
        title: String?,
        message: String?,
        titleButtonPositive: String,
        titleButtonNegative: String,
        listener: DialogConfirm.OnButtonClickedListener?
    ) {
        dialogManager.showConfirmDialog(
            title, message, titleButtonPositive, titleButtonNegative, listener
        )
    }

    open fun onSubscribeObserver() {
        viewModel.run {
            isLoading.observe(this@BaseActivity) {
                showLoading(it)
            }
            exception.observe(this@BaseActivity) {
                handleDefaultApiError(it)
            }
        }
    }
}
