package com.wakeup.hyperion.ui.signal.changeSound.childTab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentBasicSoundBinding
import com.wakeup.hyperion.model.entity.SoundModel
import com.wakeup.hyperion.ui.signal.SignalViewModel
import com.wakeup.hyperion.ui.signal.changeSound.adapter.BasicSoundAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasicSoundFragment :
    BaseFragment<SignalViewModel, FragmentBasicSoundBinding>(SignalViewModel::class) {
    private lateinit var basicSoundAdapter: BasicSoundAdapter

    companion object {
        fun newInstance() =
            BasicSoundFragment()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBasicSoundBinding {
        return FragmentBasicSoundBinding.inflate(inflater, container, false)
    }

    override fun initialize() {
        initAdapter()
    }

    private fun initAdapter() {
        basicSoundAdapter = BasicSoundAdapter()
        basicSoundAdapter.replaceData(numpyData())
        with(viewBinding.rvBasicSound) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = basicSoundAdapter
        }
    }

    private fun numpyData(): ArrayList<SoundModel> {
        val listSound = ArrayList<SoundModel>()
        listSound.add(SoundModel("1", "sound 1"))
        listSound.add(SoundModel("2", "sound 2"))
        listSound.add(SoundModel("3", "sound 3"))
        listSound.add(SoundModel("4", "sound 4"))
        listSound.add(SoundModel("5", "sound 5"))
        listSound.add(SoundModel("6", "sound 6"))
        return listSound
    }
}