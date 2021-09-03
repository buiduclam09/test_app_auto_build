package com.wakeup.hyperion.ui.signal.changeSound

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentChangeSoundBinding
import com.wakeup.hyperion.ui.main.MainActivity
import com.wakeup.hyperion.ui.signal.SignalViewModel
import com.wakeup.hyperion.ui.signal.changeSound.adapter.ChangeSoundTabAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeSoundFragment :
    BaseFragment<SignalViewModel, FragmentChangeSoundBinding>(SignalViewModel::class) {
    private lateinit var tabAdapter: ChangeSoundTabAdapter
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChangeSoundBinding {
        return FragmentChangeSoundBinding.inflate(inflater, container, false)
    }

    override fun initialize() {
        initTabLayout()
    }

    private fun initTabLayout() {
        tabAdapter = ChangeSoundTabAdapter(context, childFragmentManager)
        with(viewBinding) {
            viewPager.adapter = tabAdapter
            viewPagerTab.setupWithViewPager(viewPager)
        }
    }

    override fun onDetach() {
        super.onDetach()
        (activity as? MainActivity)?.run {
            showBottomNavigation()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as? MainActivity)?.run {
            goneBottomNavigation()
        }
    }
}