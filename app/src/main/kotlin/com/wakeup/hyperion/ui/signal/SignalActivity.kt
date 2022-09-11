package com.wakeup.hyperion.ui.signal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.thuanpx.ktext.boolean.isTrue
import com.thuanpx.ktext.context.startActivityAtRoot
import com.thuanpx.ktext.view.gone
import com.wakeup.hyperion.BuildConfig
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.Constant.EXTRA_IS_UPDATE
import com.wakeup.hyperion.common.base.BaseActivity
import com.wakeup.hyperion.databinding.FragmentSignalBinding
import com.wakeup.hyperion.ui.main.MainActivity
import com.wakeup.hyperion.ui.main.MainService
import com.wakeup.hyperion.utils.extension.clicks
import com.wakeup.hyperion.utils.extension.setStatusBarColor
import com.wakeup.hyperion.dialogManager.DialogAlert
import com.wakeup.hyperion.utils.ads.InterstitialAdManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * Copyright © 2021 Neolab VN.
 */

@AndroidEntryPoint
class SignalActivity :
    BaseActivity<SignalViewModel, FragmentSignalBinding>(SignalViewModel::class) {
    private val isUpdateSignal: Boolean?
        get() = intent?.getBundleExtra("ktext_extra_args")?.getBoolean(EXTRA_IS_UPDATE, false)
    private val isUpdateLanguage: Boolean
        get() = intent.getBooleanExtra("EXTRA_IS_UPDATE_LANGUAGE", false)
    private var interstitialAd: InterstitialAd? = null
    override fun inflateViewBinding(inflater: LayoutInflater): FragmentSignalBinding {
        return FragmentSignalBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initialize() {
        setStatusBarColor(R.color.C_E6EBEF)
        if (isUpdateSignal.isTrue() || isUpdateLanguage.isTrue()) {
            viewBinding.inclToolbar.tvTitle.setText(R.string.edit_signal)
            viewBinding.btCreateOrUpdate.setText(R.string.update)
        } else {
            viewBinding.inclToolbar.tvTitle.setText(R.string.new_signal)
            viewBinding.inclToolbar.ivBack.gone()
        }

        viewBinding.run {
            etSignal.setText(viewModel.signal)
            inclToolbar.ivBack.clicks {
                finish()
            }
            btCreateOrUpdate.clicks {
                val signal = etSignal.text.toString()
                if (signal.isBlank()) {
                    showAlertDialog(message = getString(R.string.signal_is_not_empty))
                    return@clicks
                }

                viewModel.sharedPrefsRepository.saveSignal(signal)

                if (isUpdateSignal.isTrue() || isUpdateLanguage.isTrue()) {
                    stopService(Intent(this@SignalActivity, MainService::class.java))
                    startService(Intent(this@SignalActivity, MainService::class.java).apply {
                        putExtra(Constant.EXTRA_SIGNAL_MODEL, viewModel.signalLocalModel)
                        putExtra(Constant.EXTRA_SIGNAL, signal)
                        putExtra(Constant.EXTRA_LANGUAGE, sharedPrefsRepository.getLanguage())
                    })
                    showAlertDialog(message = getString(R.string.update_signal_success),
                        listener = object : DialogAlert.Companion.OnButtonClickedListener {
                            override fun onPositiveClicked() {
                                if (isUpdateLanguage.isTrue()) {
                                    startActivityAtRoot(MainActivity::class)
                                } else {
                                    finish()
                                }
                            }
                        })
                } else {
                    if (!viewModel.isShowCreateSignal) {
                        showAds()
                    }
                    showAlertDialog(
                        message = getString(R.string.create_signal_success),
                        listener = object : DialogAlert.Companion.OnButtonClickedListener {
                            override fun onPositiveClicked() {
                                startActivityAtRoot(MainActivity::class)
                            }
                        })
                }
            }
        }
    }

    private fun showAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            BuildConfig.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    viewModel.isShowCreateSignal = true
                    this@SignalActivity.interstitialAd = interstitialAd
                    this@SignalActivity.interstitialAd?.show(this@SignalActivity)
                }
            })
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }
        }
    }
}