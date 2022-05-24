package com.wakeup.hyperion.utils.ads

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.LoadAdError
import java.lang.Exception
import javax.inject.Inject
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import timber.log.Timber
import kotlin.time.*


private class LoadAdErrorException(val error: LoadAdError) : Exception()
private sealed interface AdAction {
    object Load : AdAction
    object Cancel : AdAction
}

private typealias LoadAdErrorBannerAds = Either<LoadAdError, InterstitialAd>

@ExperimentalCoroutinesApi
@OptIn(ExperimentalTime::class)
class BannerAdsManager
@Inject constructor(
    private val activity: AppCompatActivity,
    private val timeSource: TimeSource
) {
    private val scope = activity.lifecycleScope
    private val loadAdActionSharedFlow = MutableSharedFlow<AdAction>(extraBufferCapacity = 1)
    private var lastShowAdTime: TimeMark? = null
    private val interstitialAdStateFlow: StateFlow<LoadAdErrorBannerAds?> =
        loadAdActionSharedFlow.filterIsInstance<AdAction.Load>()
            .flatMapLatest {
                ::
            }


    private suspend fun loadAdInternal() {
        suspendCancellableCoroutine<LoadAdErrorBannerAds> { count ->
            InterstitialAd.load(
                activity,
                INTERSTITIAL_AD_UNIT_ID,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(error: LoadAdError) = count.resume(error.left())

                    override fun onAdLoaded(ads: InterstitialAd) = count.resume(ads.right())
                })
        }
    }

    private inline fun canShowAd(): Boolean {
        val mark = lastShowAdTime
        return mark === null || (mark + ADS_TIMEOUT_DURATION).hasPassedNow()
    }

    private suspend fun showInternal() {
        if (!canShowAd()) {
            Timber.tag(LOG_TAG)
                .d("showInternal: ignore because the timeout is not passed, lastShowAdTime=$lastShowAdTime")
            return
        }
        val either = withTimeoutOrNull(WAITING_TIMEOUT_DURATION) {
            interstitialAdStateFlow.first { it != null }
        }
        val ads = either?.orNull() ?: kotlin.run {
            Timber.tag(LOG_TAG).d("showInternal: ignore because timeout or error, either=$either")
            return
        }
        val isSuccess = suspendCancellableCoroutine<Boolean> { continuation ->
            ads.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Timber.tag(LOG_TAG).d("showInternal: callback: The ad was dismissed.")
                    ads.fullScreenContentCallback = null
                    continuation.resume(true)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Timber.tag(LOG_TAG)
                        .d("showInternal: callback: The ad failed to show, adError=$adError")

                    ads.fullScreenContentCallback = null
                    continuation.resume(false)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    scope.launch(start = CoroutineStart.UNDISPATCHED) {
                        loadAdActionSharedFlow.emit(AdAction.Cancel)
                        yield()
                        loadAdActionSharedFlow.emit(AdAction.Load)
                        Timber.tag(LOG_TAG).d("showInternal: callback: Dispatched actions.")
                    }
                    Timber.tag(LOG_TAG).d("showInternal: callback: The ad was shown.")
                }
            }
            ads.show(activity)
        }
        if (isSuccess) {
            lastShowAdTime = timeSource.markNow()
        }
        Timber.tag(LOG_TAG).d("showInternal: done $ads")
    }

    fun show(onComplete: () -> Unit = EmptyComplete): Job =
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            showInternal()
            onComplete()
        }

    companion object {
        private fun Flow<BannerAdsManager>.log() =
            onStart {
                Timber
                    .tag(LOG_TAG)
                    .d("interstitialAdStateFlow: loadAdInternal: starting...")
            }
                .onEach {
                    Timber
                        .tag(LOG_TAG)
                        .d("interstitialAdStateFlow: loadAdInternal: original either=$it")
                }

        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        private const val LOG_TAG = "InterstitialAdManager"
        private const val MAX_RETRIES = 3

        private val WAITING_TIMEOUT_DURATION = 5.seconds
        private val ADS_TIMEOUT_DURATION = 1.minutes
    }
}

object EmptyComplete : () -> Unit {
    override fun invoke() = Unit
}