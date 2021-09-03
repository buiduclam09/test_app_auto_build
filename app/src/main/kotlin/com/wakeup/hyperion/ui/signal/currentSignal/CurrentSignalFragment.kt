package com.wakeup.hyperion.ui.signal.currentSignal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.thuanpx.ktext.context.startActivity
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentCurrentSignalBinding
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.ui.main.MainViewModel
import com.wakeup.hyperion.ui.signal.SignalActivity
import com.wakeup.hyperion.ui.signal.SignalViewModel
import com.wakeup.hyperion.ui.signal.changeSound.ChangeSoundFragment
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

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    fun updateUI() {
        viewBinding.run {
            tvTitleCurrentSignal.text = sharedViewModel.signal
            val signalModel = viewModel.signalLocalModel ?: SignalLocalModel(
                true,
                Constant.DEFAULT_SOUND,
                R.raw.check_sound.toString(),
                false
            )
            if (signalModel.isBasic) {
                tvTitleSound.text = signalModel.name
            } else {
                tvTitleSound.text = signalModel.disPlayName
            }
        }
    }

    override fun initialize() {
        viewBinding.run {
            btnEditVoice.clicks {
                activity?.startActivity(SignalActivity::class, Bundle().apply {
                    putBoolean(Constant.EXTRA_IS_UPDATE, true)
                })
            }
            btnChangeVoice.clicks {
                ChangeSoundFragment.newInstance {
                    updateUI()
                }.show(childFragmentManager, ChangeSoundFragment::class.java.simpleName)
            }
        }
    }
}
