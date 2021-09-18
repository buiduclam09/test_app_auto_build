package com.wakeup.hyperion.ui.setting

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.google.android.material.slider.Slider
import com.thuanpx.ktext.context.startActivity
import com.thuanpx.ktext.context.startActivityAtRoot
import com.thuanpx.ktext.view.hide
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.ExtraConstant.EXTRA_TITLE
import com.wakeup.hyperion.common.ExtraConstant.EXTRA_URL
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentSettingBinding
import com.wakeup.hyperion.ui.main.MainActivity
import com.wakeup.hyperion.ui.main.MainViewModel
import com.wakeup.hyperion.ui.signal.SignalActivity
import com.wakeup.hyperion.ui.webview.WebViewActivity
import com.wakeup.hyperion.utils.LanguageSettings.ENGLISH
import com.wakeup.hyperion.utils.LanguageSettings.FRENCH
import com.wakeup.hyperion.utils.LanguageSettings.setLocale
import com.wakeup.hyperion.utils.extension.clicks
import com.wakeup.hyperion.utils.extension.goTo
import dagger.hilt.android.AndroidEntryPoint


/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/1/21.
 */
@AndroidEntryPoint
class SettingFragment :
    BaseFragment<SettingViewModel, FragmentSettingBinding>(SettingViewModel::class) {

    private val sharedViewModel by activityViewModels<MainViewModel>()
    private val listLanguage = arrayListOf("English", "French")

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
            spinner.setItems(listLanguage)
            val languageCurrent = viewModel.getLanguageLocal()
            when (languageCurrent) {
                FRENCH -> spinner.selectedIndex = 1
                else -> {
                    spinner.selectedIndex = 0
                }
            }
            spinner.setOnItemSelectedListener { view, position, id, item ->
                when (position) {
                    0 -> {
                        if (languageCurrent != ENGLISH) {
                            viewModel.setLanguage(ENGLISH)
                            setLocale(requireContext(), ENGLISH)
                            activity?.startActivityAtRoot(SignalActivity::class, Bundle().apply {
                                putBoolean(Constant.EXTRA_IS_UPDATE_LANGUAGE, true)
                            })
                        }
                    }
                    1 -> {
                        if (languageCurrent != FRENCH) {
                            viewModel.setLanguage(FRENCH)
                            setLocale(requireContext(), FRENCH)
                            activity?.startActivityAtRoot(SignalActivity::class, Bundle().apply {
                                putBoolean(Constant.EXTRA_IS_UPDATE_LANGUAGE, true)
                            })
                        }
                    }
                }
            }

            val am = requireContext().getSystemService(AUDIO_SERVICE) as AudioManager
            val volumeRingtone = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolumeLevel: Int = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            sliderRingtone.valueTo = maxVolumeLevel.toFloat()
            sliderRingtone.value = volumeRingtone.toFloat()

            sliderRingtone.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, slider.value.toInt(), 0)
                }

            })

            clAboutUs.clicks {
                Bundle().apply {
                    putString(EXTRA_URL, "https://where-is-it.flycricket.io/terms.html")
                    putString(EXTRA_TITLE, "About us")
                    goTo(WebViewActivity::class, this)
                }
            }
        }

    }

    override fun onSubscribeObserver() {
        super.onSubscribeObserver()
        sharedViewModel.updateVolume.observe(viewLifecycleOwner) {
            updateRingtoneVolume()
        }
    }

    private fun updateRingtoneVolume() {
        val am = requireContext().getSystemService(AUDIO_SERVICE) as AudioManager
        val volumeRingtone = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        viewBinding.sliderRingtone.value = volumeRingtone.toFloat()
    }
}
