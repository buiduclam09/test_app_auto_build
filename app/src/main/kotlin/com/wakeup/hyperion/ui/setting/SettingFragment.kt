package com.wakeup.hyperion.ui.setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.slider.Slider
import com.thuanpx.ktext.view.hide
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentSettingBinding


/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/1/21.
 */
class SettingFragment :
    BaseFragment<SettingViewModel, FragmentSettingBinding>(SettingViewModel::class) {

//    private val sharedViewModel by activityViewModels<MainViewModel>()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

    override fun initialize() {
        viewBinding.run {
            toolbar.ivBack.hide()
            toolbar.tvTitle.text = getString(R.string.setting)

            spinner.setItems("English")
            spinner.setOnItemSelectedListener { view, position, id, item -> }

            val am = requireContext().getSystemService(AUDIO_SERVICE) as AudioManager
            val volumeRingtone = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolumeLevel: Int = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            sliderRingtone.valueTo = maxVolumeLevel.toFloat()
            sliderRingtone.value = volumeRingtone.toFloat()

            sliderVibration.addOnSliderTouchListener(object: Slider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: Slider) {
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    val v = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(
                            VibrationEffect.createOneShot(
                                500,
                                slider.value.toInt()
                            )
                        )
                    } else {
                        v.vibrate(500)
                    }
                }

            })

        }

    }

    override fun onSubscribeObserver() {
        super.onSubscribeObserver()
//        sharedViewModel.updateVolume.observe(viewLifecycleOwner) {
//            updateRingtoneVolume()
//        }
    }

    fun updateRingtoneVolume() {
        val am = requireContext().getSystemService(AUDIO_SERVICE) as AudioManager
        val volumeRingtone = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        viewBinding.sliderRingtone.value = volumeRingtone.toFloat()
    }
}
