package com.wakeup.hyperion.ui.signal.changeSound.childTab

import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.thuanpx.ktext.boolean.isNotTrue
import com.thuanpx.ktext.boolean.isTrue
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentBasicSoundBinding
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.ui.main.MainService
import com.wakeup.hyperion.ui.main.MainViewModel
import com.wakeup.hyperion.ui.signal.SignalViewModel
import com.wakeup.hyperion.ui.signal.changeSound.adapter.BasicSoundAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasicSoundFragment :
    BaseFragment<SignalViewModel, FragmentBasicSoundBinding>(SignalViewModel::class) {

    private val sharedViewModel by activityViewModels<MainViewModel>()

    private lateinit var basicSoundAdapter: BasicSoundAdapter
    private var mediaPlayer: MediaPlayer? = null

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
        viewModel.showAds()
        initAdapter()
    }

    override fun onSubscribeObserver() {
        super.onSubscribeObserver()
        lifecycleScope.launch {
            sharedViewModel.updateTabFlow.collect {
                if (it) {
                    removeAllSelected()
                }
            }
        }
    }

    private fun initAdapter() {
        basicSoundAdapter =
            BasicSoundAdapter() { type: String, signalLocalModel: SignalLocalModel ->
                if (type == Constant.TYPE_SELECTED) {
                    basicSoundAdapter.getData().forEach {
                        it.isSelected = false
                    }
                    signalLocalModel.isSelected = true
                    sharedViewModel.saveSignalSound(signalLocalModel)
                    activity?.stopService(Intent(requireContext(), MainService::class.java))
                    activity?.startService(Intent(requireContext(), MainService::class.java).apply {
                        putExtra(Constant.EXTRA_SIGNAL, sharedViewModel.signal)
                        putExtra(Constant.EXTRA_SIGNAL_MODEL, sharedViewModel.signalLocalModel)
                    })
                } else {
                    if (signalLocalModel.isPlaying.isTrue()) {
                        stopSound()
                        signalLocalModel.isPlaying = false
                    } else {
                        basicSoundAdapter.getData().forEach {
                            it.isPlaying = false
                        }
                        signalLocalModel.isPlaying = true
                        playSound(signalLocalModel.path.toInt())
                    }
                }
                basicSoundAdapter.notifyDataSetChanged()
            }
        basicSoundAdapter.replaceData(dummyData())
        with(viewBinding.rvBasicSound) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = basicSoundAdapter
        }
        Handler(Looper.getMainLooper()).post {
            sharedViewModel.signalLocalModel?.let { signal ->
                if (signal.isBasic.isNotTrue()) {
                    return@let
                }
                basicSoundAdapter.getData().forEach {
                    if (signal.path == it.path) {
                        it.isSelected = true
                        basicSoundAdapter.notifyDataSetChanged()
                        return@let
                    }
                }
            }
        }
    }

    private fun playSound(path: Int?) {
        stopSound()
        mediaPlayer = path?.let { MediaPlayer.create(requireContext(), it) }
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

    }

    private fun stopSound() {
        try {
            if (mediaPlayer?.isPlaying.isTrue()) {
                mediaPlayer?.pause()
            }
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        stopSound()
        super.onDestroyView()
    }

    private fun removeAllSelected() {
        basicSoundAdapter.getData().forEach {
            it.isSelected = false
        }
        basicSoundAdapter.notifyDataSetChanged()
    }

    private fun dummyData(): ArrayList<SignalLocalModel> {
        val listSound = ArrayList<SignalLocalModel>()
        listSound.add(
            SignalLocalModel(
                true,
                "A Himitsu Adventures",
                R.raw.a_himitsu_adventures.toString(),
                false
            )
        )
        listSound.add(
            SignalLocalModel(
                true,
                "And So It Begins",
                R.raw.and_so_it_begins.toString(),
                false
            )
        )
        listSound.add(
            SignalLocalModel(
                true,
                "Kubbi Up In My Jam",
                R.raw.kubbi_up_in_my_jam.toString(),
                false
            )
        )
        listSound.add(
            SignalLocalModel(
                true,
                "Phone Vibrate",
                R.raw.cell_phone_vibrate.toString(),
                false
            )
        )
        listSound.add(SignalLocalModel(true, "MBB Island", R.raw.mbb_island.toString(), false))
        return listSound
    }
}