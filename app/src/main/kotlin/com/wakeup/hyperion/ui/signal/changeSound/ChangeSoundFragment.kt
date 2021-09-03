package com.wakeup.hyperion.ui.signal.changeSound

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.base.BaseBottomSheetFragment
import com.wakeup.hyperion.databinding.FragmentChangeSoundBinding
import com.wakeup.hyperion.ui.main.MainActivity
import com.wakeup.hyperion.ui.signal.SignalViewModel
import com.wakeup.hyperion.ui.signal.changeSound.adapter.ChangeSoundTabAdapter
import com.wakeup.hyperion.utils.extension.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangeSoundFragment :
    BaseBottomSheetFragment<SignalViewModel, FragmentChangeSoundBinding>(SignalViewModel::class) {
    private lateinit var tabAdapter: ChangeSoundTabAdapter
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChangeSoundBinding {
        return FragmentChangeSoundBinding.inflate(inflater, container, false)
    }

    private var listener: (() -> Unit)? = null

    companion object {
        fun newInstance(listener: (() -> Unit)) = ChangeSoundFragment().apply {
            this.listener = listener
        }
    }

    override fun onDestroyView() {
        listener?.invoke()
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val bottomSheet: View = dialog!!.findViewById(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
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
            setStatusBarColor(android.R.color.white)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as? MainActivity)?.run {
            goneBottomNavigation()
            setStatusBarColor(R.color.C_E6EBEF)
        }
    }
}