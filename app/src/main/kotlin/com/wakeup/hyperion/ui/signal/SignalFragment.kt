package com.wakeup.hyperion.ui.signal

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.thuanpx.ktext.boolean.isNotTrue
import com.thuanpx.ktext.context.withArgs
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentSignalBinding
import com.wakeup.hyperion.ui.main.MainActivity
import com.wakeup.hyperion.ui.main.MainViewModel
import com.wakeup.hyperion.utils.extension.clicks
import dagger.hilt.android.AndroidEntryPoint

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/1/21.
 */

@AndroidEntryPoint
class SignalFragment: BaseFragment<SignalViewModel, FragmentSignalBinding>(SignalViewModel::class) {

    companion object {
        private const val EXTRA_IS_CREATE = "EXTRA_IS_CREATE"

        fun newInstance(isCreate: Boolean = false) = SignalFragment().withArgs {
            putBoolean(EXTRA_IS_CREATE, isCreate)
        }
    }

    private val sharedViewModel by activityViewModels<MainViewModel>()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignalBinding {
        return FragmentSignalBinding.inflate(inflater, container, false)
    }

    override fun onDetach() {
        (activity as? MainActivity)?.run {
            showBottomNavigation()
        }
        super.onDetach()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as? MainActivity)?.run {
            goneBottomNavigation()
        }
    }

    override fun initialize() {
        if (arguments?.getBoolean(EXTRA_IS_CREATE).isNotTrue()) {
            viewBinding.btCreateOrUpdate.setText(R.string.update)
        }

        viewBinding.run {
            etSignal.setText(sharedViewModel.signal)
            inclToolbar.ivBack.clicks {
                findNavController().popBackStack()
            }
            btCreateOrUpdate.clicks {
                val signal = etSignal.text.toString()
                if (signal.isBlank()) {
                    // TODO change message
                    showAlertDialog(message = "Signal is not empty!")
                    return@clicks
                }
                showAlertDialog(message = "Update Signal success!")
                viewModel.sharedPrefsRepository.saveSignal(signal)
            }
        }
    }
}