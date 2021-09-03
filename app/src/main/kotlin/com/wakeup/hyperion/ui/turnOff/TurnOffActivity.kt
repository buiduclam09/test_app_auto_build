package com.wakeup.hyperion.ui.turnOff

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import android.view.LayoutInflater
import com.google.android.gms.ads.*
import com.thuanpx.ktext.context.startActivityAtRoot
import com.thuanpx.ktext.string.toIntOrZero
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.base.BaseActivity
import com.wakeup.hyperion.databinding.ActivityTurnOffBinding
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.ui.main.MainActivity
import com.wakeup.hyperion.ui.main.MainService
import com.wakeup.hyperion.utils.extension.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/9/21.
 */

@AndroidEntryPoint
class TurnOffActivity :
    BaseActivity<TurnOffViewModel, ActivityTurnOffBinding>(TurnOffViewModel::class) {

    private var interstitialAd: InterstitialAd? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityTurnOffBinding {
        return ActivityTurnOffBinding.inflate(inflater)
    }

    override fun onDestroy() {
        stopSound()
        super.onDestroy()
    }

    override fun initialize() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                this@TurnOffActivity.interstitialAd = interstitialAd
                this@TurnOffActivity.interstitialAd?.show(this@TurnOffActivity)
            }
        })
        interstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }
        }

        stopService(Intent(this, MainService::class.java))
        startSound()
        val signalModel = viewModel.signalLocalModel ?: SignalLocalModel(
            true,
            Constant.DEFAULT_SOUND,
            R.raw.check_sound.toString(),
            false
        )
        if (signalModel.isBasic) {
            viewBinding.tvNameSound.text = signalModel.name
        } else {
            viewBinding.tvNameSound.text = signalModel.disPlayName
        }
        setStatusBarColor(R.color.C_E6EBEF)
        viewBinding.ivLogo.setSwipeOrientationMode(0)
        viewBinding.ivLogo.setOnSwipedListener {
            stopSound()
            startService(Intent(this, MainService::class.java).apply {
                putExtra(Constant.EXTRA_SIGNAL, viewModel.signal)
                putExtra(Constant.EXTRA_SIGNAL_MODEL, viewModel.signalLocalModel)
            })
            startActivityAtRoot(MainActivity::class)
        }
    }

    private fun stopSound() {
        mediaPlayer?.pause()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.cancel()
    }

    private fun startSound() {
        val signalLocalModel = viewModel.signalLocalModel ?: SignalLocalModel(
            true,
            Constant.DEFAULT_SOUND,
            R.raw.check_sound.toString(),
            false
        )
        try {
            mediaPlayer?.release()
            if (signalLocalModel.isBasic) {
                mediaPlayer =
                    MediaPlayer.create(this, signalLocalModel.path.toIntOrZero())
            } else {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(signalLocalModel.name)
                mediaPlayer?.prepare()
            }
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } catch (e: Exception) {
            mediaPlayer = MediaPlayer.create(this, R.raw.check_sound)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }

        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val mVibratePattern = longArrayOf(0, 400, 800, 600, 800, 800, 800, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(
                VibrationEffect.createWaveform(
                    mVibratePattern,
                    3
                )
            )
        } else {
            v.vibrate(mVibratePattern, 3)
        }
    }
}