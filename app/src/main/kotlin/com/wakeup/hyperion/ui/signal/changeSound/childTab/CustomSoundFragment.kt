package com.wakeup.hyperion.ui.signal.changeSound.childTab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentCustomSoundBinding
import com.wakeup.hyperion.model.entity.SoundModel
import com.wakeup.hyperion.ui.signal.SignalViewModel
import com.wakeup.hyperion.ui.signal.changeSound.adapter.CustomSoundAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomSoundFragment :
    BaseFragment<SignalViewModel, FragmentCustomSoundBinding>(SignalViewModel::class) {
    private lateinit var customSoundAdapter: CustomSoundAdapter

    companion object {
        fun newInstance() = CustomSoundFragment()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCustomSoundBinding {
        return FragmentCustomSoundBinding.inflate(inflater, container, false)
    }

    override fun initialize() {
        initAdapter()
    }

    private fun initAdapter() {
        customSoundAdapter = CustomSoundAdapter()
        customSoundAdapter.replaceData(numpyData())
        with(viewBinding.rvCustomSound) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = customSoundAdapter
        }
    }

    private fun numpyData(): ArrayList<SoundModel> {
        val listSound = ArrayList<SoundModel>()
        listSound.add(SoundModel("1", "sound 111"))
        listSound.add(SoundModel("2", "sound 2222"))
        listSound.add(SoundModel("3", "sound 3333"))
        listSound.add(SoundModel("4", "sound 4444"))
        listSound.add(SoundModel("5", "sound 555"))
        listSound.add(SoundModel("6", "sound 6555"))
        return listSound
    }
}