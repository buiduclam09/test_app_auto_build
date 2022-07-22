package com.wakeup.hyperion.dialogManager

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.thuanpx.ktext.view.gone
import com.wakeup.hyperion.databinding.DialogConfirmBinding

class DialogConfirm : DialogFragment() {

    var listener: OnButtonClickedListener? = null
    private var title: String? = ""
    private var message: String? = ""
    private var titleBtnPositive: String? = ""
    private var titleBtnNegative: String? = ""
    private var _viewBinding: DialogConfirmBinding? = null
    protected val viewBinding get() = _viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            title = it.getString(
                TITLE_EXTRA
            )
            message = it.getString(
                MESSAGE_EXTRA
            )
            titleBtnPositive = it.getString(
                TITLE_BUTTON_POSITIVE_EXTRA
            )
            titleBtnNegative = it.getString(
                TITLE_BUTTON_NEGATIVE_EXTRA
            )
        }

        _viewBinding = DialogConfirmBinding.inflate(inflater, container, false)

        return _viewBinding!!.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
            it.setCanceledOnTouchOutside(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            tvTitle.text = title
            tvContent.text = message

            tvTitle.gone(title.isNullOrBlank())

            tvContent.gone(message.isNullOrBlank())

        }
//        val actionPositiveDis = RxView.clicks(btnPositive)
//                .subscribe {
//                    dismiss()
//                    listener?.onPositiveClicked()
//                }
//        compositeDisposable.add(actionPositiveDis)
//
//        val actionNegativeDis = RxView.clicks(btnNegative)
//                .subscribe {
//                    dismiss()
//                    listener?.onNegativeClicked()
//                }
//        compositeDisposable.add(actionNegativeDis)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        private const val TITLE_EXTRA = "TITLE_EXTRA"
        private const val MESSAGE_EXTRA = "MESSAGE_EXTRA"
        private const val TITLE_BUTTON_POSITIVE_EXTRA = "TITLE_BUTTON_POSITIVE_EXTRA"
        private const val TITLE_BUTTON_NEGATIVE_EXTRA = "TITLE_BUTTON_NEGATIVE_EXTRA"

        fun newInstance(
            title: String?,
            message: String?,
            titleBtnPositive: String,
            titleBtnNegative: String,
            listener: OnButtonClickedListener?
        ): DialogConfirm {
            return DialogConfirm().apply {
                arguments = Bundle().apply {
                    putString(
                        TITLE_EXTRA, title
                    )
                    putString(
                        MESSAGE_EXTRA, message
                    )
                    putString(
                        TITLE_BUTTON_POSITIVE_EXTRA, titleBtnPositive
                    )
                    putString(
                        TITLE_BUTTON_NEGATIVE_EXTRA, titleBtnNegative
                    )
                }
                this.listener = listener
            }
        }
    }

    interface OnButtonClickedListener {
        fun onPositiveClicked()
        fun onNegativeClicked()
    }
}
