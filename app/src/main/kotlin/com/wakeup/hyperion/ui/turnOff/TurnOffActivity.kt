package com.wakeup.hyperion.ui.turnOff

import android.content.Intent
import android.view.LayoutInflater
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.wakeup.hyperion.common.base.BaseActivity
import com.wakeup.hyperion.databinding.ActivityTurnOffBinding
import com.wakeup.hyperion.ui.main.MainService
import dagger.hilt.android.AndroidEntryPoint

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/9/21.
 */

@AndroidEntryPoint
class TurnOffActivity :
    BaseActivity<TurnOffViewModel, ActivityTurnOffBinding>(TurnOffViewModel::class) {

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityTurnOffBinding {
        return ActivityTurnOffBinding.inflate(inflater)
    }

    override fun initialize() {
        MobileAds.initialize(this) {
            viewBinding.adView.loadAd(AdRequest.Builder().build())
        }
        viewBinding.ivLogo.setSwipeOrientationMode(0)
        viewBinding.ivLogo.setOnSwipedListener {
            stopService(Intent(this@TurnOffActivity, MainService::class.java))
            startService(Intent(this@TurnOffActivity, MainService::class.java))
            finish()
        }
    }
}