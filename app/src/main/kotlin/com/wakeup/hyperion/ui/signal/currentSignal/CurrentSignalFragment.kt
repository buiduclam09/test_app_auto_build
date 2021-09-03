package com.wakeup.hyperion.ui.signal.currentSignal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentCurrentSignalBinding
import com.wakeup.hyperion.ui.main.MainViewModel
import com.wakeup.hyperion.ui.signal.SignalViewModel
import com.wakeup.hyperion.utils.extension.clicks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentSignalFragment :
    BaseFragment<SignalViewModel, FragmentCurrentSignalBinding>(SignalViewModel::class) {

    private val sharedViewModel by activityViewModels<MainViewModel>()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCurrentSignalBinding {
        return FragmentCurrentSignalBinding.inflate(inflater, container, false)
    }

    override fun initialize() {
        viewBinding.run {
            tvTitleCurrentSignal.text = sharedViewModel.signal
            btnEditVoice.clicks {
                findNavController().navigate(R.id.actionCurrentSignalFragmentToSignalFragment)
            }
            btnChangeVoice.clicks {
                findNavController().navigate(R.id.actionCurrentSignalFragmentToChangeSoundFragment)
            }
        }
    }
}
