package com.wakeup.hyperion.ui.splash

import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.thuanpx.ktext.context.startActivityAtRoot
import com.wakeup.hyperion.common.Constant.TIME_DELAY
import com.wakeup.hyperion.common.base.BaseActivity
import com.wakeup.hyperion.databinding.SplashActivityBinding
import com.wakeup.hyperion.ui.introduce.IntroduceActivity
import com.wakeup.hyperion.ui.main.MainActivity
import com.wakeup.hyperion.utils.LanguageSettings.ENGLISH
import com.wakeup.hyperion.utils.LanguageSettings.FRENCH
import com.wakeup.hyperion.utils.LanguageSettings.setLocale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity :
    BaseActivity<SplashViewModel, SplashActivityBinding>(SplashViewModel::class) {
    override fun inflateViewBinding(inflater: LayoutInflater): SplashActivityBinding {
        return SplashActivityBinding.inflate(inflater)
    }

    override fun initialize() {
        setLocateFollowLanguage()
        lifecycleScope.launch {
            delay(TIME_DELAY)
            if (sharedPrefsRepository.getIsFirstLauncher()) {
                startActivityAtRoot(MainActivity::class)
            } else {
                sharedPrefsRepository.saveIsFirstLauncher(true)
                startActivityAtRoot(IntroduceActivity::class)
            }
        }
    }

    private fun setLocateFollowLanguage() {
        when (viewModel.getLanguageLocal()) {
            FRENCH -> {
                setLocale(this, FRENCH)
            }
            else -> {
                setLocale(this, ENGLISH)
            }
        }
    }
}