package com.wakeup.hyperion.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.createViewModelLazy
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wakeup.hyperion.R
import com.wakeup.hyperion.ui.main.MainActivity
import com.wakeup.hyperion.utils.extension.handleDefaultApiError
import com.wakeup.hyperion.dialogManager.DialogAlert
import com.wakeup.hyperion.dialogManager.DialogConfirm
import com.wakeup.hyperion.dialogManager.DialogManager
import com.wakeup.hyperion.dialogManager.DialogManagerImpl
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

abstract class BaseBottomSheetFragment<viewModel : BaseViewModel, viewBinding : ViewBinding>(
    viewModelClass: KClass<viewModel>
) :
    BottomSheetDialogFragment(), BaseView {

    protected val viewModel by createViewModelLazy(viewModelClass, { viewModelStore })
    private var _viewBinding: viewBinding? = null
    protected val viewBinding get() = _viewBinding!! // ktlint-disable

    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): viewBinding
    private var dialogManager: DialogManager? = null

    protected abstract fun initialize()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = inflateViewBinding(inflater, container)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        onSubscribeObserver()
        dialogManager = DialogManagerImpl(context)
        val d = dialog as BottomSheetDialog
        val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet) ?: return
        val from = BottomSheetBehavior.from<View>(bottomSheetInternal)
        from.isFitToContents = true
        from.state = STATE_EXPANDED
    }

    override fun showLoading(isShow: Boolean) {
        if (isShow) showLoading() else hideLoading()
    }

    override fun showLoading() {
        (activity as? BaseActivity<*, *>)?.showLoading()
    }

    override fun hideLoading() {
        (activity as? BaseActivity<*, *>)?.hideLoading()
    }

    override fun showAlertDialog(
        title: String,
        message: String,
        titleButton: String,
        listener: DialogAlert.Companion.OnButtonClickedListener?
    ) {
        dialogManager?.showAlertDialog(title, message, titleButton, listener)
    }

    override fun showConfirmDialog(
        title: String?,
        message: String?,
        titleButtonPositive: String,
        titleButtonNegative: String,
        listener: DialogConfirm.OnButtonClickedListener?
    ) {
        dialogManager?.showConfirmDialog(
            title, message, titleButtonPositive, titleButtonNegative, listener
        )
    }

    override fun onDetach() {
        super.onDetach()
        dialogManager?.onRelease()
    }

    /**
     * Fragments outlive their views. Make sure you clean up any references to
     * the binding class instance in the fragment's onDestroyView() method.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    open fun onSubscribeObserver() {
        viewModel.run {
            isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }
            exception.observe(viewLifecycleOwner) {
                (activity as? BaseActivity<*, *>)?.handleDefaultApiError(it)
            }
        }
    }

    fun getMainActivity() = (activity as? MainActivity)
}
