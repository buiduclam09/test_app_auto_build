package com.wakeup.hyperion.utils.ads

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.flow.MutableSharedFlow
import java.lang.Exception
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import arrow.core.Either


private class LoadAdErrorException(val error: LoadAdError) : Exception()
private sealed interface AdAction {
    object Load : AdAction
}

private typealias LoadAdErrorBannerAds = Eith

class BannerAdsManager @OptIn(ExperimentalTime::class)
@Inject constructor(
    private val activity: AppCompatActivity,
    private val timeSource: TimeSource
) {
    private val scope = activity.lifecycleScope
    private val loadAdActionSharedFlow = MutableSharedFlow<AdAction>()
}