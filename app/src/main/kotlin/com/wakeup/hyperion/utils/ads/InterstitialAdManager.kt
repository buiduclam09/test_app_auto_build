package com.wakeup.hyperion.utils.ads

import android.content.Context
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.Either
import arrow.core.getOrHandle
import arrow.core.left
import arrow.core.right
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.hoc081098.flowext.flatMapFirst
import com.hoc081098.flowext.retryWhenWithExponentialBackoff
import com.hoc081098.flowext.takeUntil
import com.wakeup.hyperion.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import timber.log.Timber

private class LoadAdErrorException(val error: LoadAdError) : Exception()

private sealed interface AdAction {
    object Load : AdAction
    object Cancel : AdAction
}

private typealias LoadAdErrorOrInterstitialAd = Either<LoadAdError, InterstitialAd>

@OptIn(FlowPreview::class, ExperimentalTime::class)
@MainThread
@ActivityScoped
class InterstitialAdManager @Inject constructor(
    private val activity: AppCompatActivity,
    private val timeSource: TimeSource,
    @ApplicationContext private val appContext: Context,
) {
    private val scope = activity.lifecycleScope
    private val loadAdActionSharedFlow = MutableSharedFlow<AdAction>(extraBufferCapacity = 1)
    private var lastShowAdTime: TimeMark? = null

    private val interstitialAdStateFlow: StateFlow<LoadAdErrorOrInterstitialAd?> =
        loadAdActionSharedFlow
            .filterIsInstance<AdAction.Load>()
            .flatMapFirst {
                ::loadAdInternal
                    .asFlow()
                    .cancellable()
                    .log()
                    .map { either -> either.getOrHandle { throw LoadAdErrorException(it) } }
                    .retryWhenWithExponentialBackoff(
                        initialDelay = 2.seconds,
                        factor = 2.0,
                        maxDelay = Duration.INFINITE,
                    ) { cause, attempt ->
                        Timber
                            .tag(LOG_TAG)
                            .e(cause,"interstitialAdStateFlow: loadAdInternal: retry attempt=$attempt, cause=$cause")
                        attempt < MAX_RETRIES && cause is LoadAdErrorException
                    }
                    .map {
                        @Suppress("USELESS_CAST")
                        it.right() as LoadAdErrorOrInterstitialAd?
                    }
                    .onStart { emit(null) }
                    .catch { emit((it as LoadAdErrorException).error.left()) }
                    .takeUntil(loadAdActionSharedFlow.filterIsInstance<AdAction.Cancel>())
            }
            .onEach {
                Timber
                    .tag(LOG_TAG)
                    .d("interstitialAdStateFlow: loadAdInternal: final either=$it")
            }
            .stateIn(
                scope,
                SharingStarted.Eagerly,
                null
            )

    private suspend fun loadAdInternal() =
        suspendCancellableCoroutine<LoadAdErrorOrInterstitialAd> { cont ->
            InterstitialAd.load(
                appContext,
                BuildConfig.INTERSTITIAL_AD_UNIT_ID,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(error: LoadAdError) = cont.resume(error.left())
                    override fun onAdLoaded(ad: InterstitialAd) = cont.resume(ad.right())
                }
            )
        }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun canShowAd(): Boolean {
        val mark = lastShowAdTime
        return mark === null || (mark + ADS_TIMEOUT_DURATION).hasPassedNow()
    }

    private suspend fun showInternal() {
        if (!canShowAd()) {
            Timber
                .tag(LOG_TAG)
                .d("showInternal: ignore because the timeout is not passed, lastShowAdTime=$lastShowAdTime")
            return
        }

        val either = withTimeoutOrNull(WAITING_TIMEOUT_DURATION) {
            interstitialAdStateFlow.first { it != null }
        }
        val ad = either?.orNull() ?: kotlin.run {
            Timber.tag(LOG_TAG).d("showInternal: ignore because timeout or error, either=$either")
            return
        }

        val isSuccess = suspendCancellableCoroutine<Boolean> { cont ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Timber.tag(LOG_TAG).d("showInternal: callback: The ad was dismissed.")

                    ad.fullScreenContentCallback = null
                    cont.resume(true)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when fullscreen content failed to show.
                    Timber.tag(LOG_TAG).d("showInternal: callback: The ad failed to show, adError=$adError")

                    ad.fullScreenContentCallback = null
                    cont.resume(false)
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    scope.launch(start = UNDISPATCHED) {
                        loadAdActionSharedFlow.emit(AdAction.Cancel)
                        yield()
                        loadAdActionSharedFlow.emit(AdAction.Load)

                        Timber.tag(LOG_TAG).d("showInternal: callback: Dispatched actions.")
                    }
                    Timber.tag(LOG_TAG).d("showInternal: callback: The ad was shown.")
                }
            }

            ad.show(activity)
        }

        if (isSuccess) {
            lastShowAdTime = timeSource.markNow()
        }
        Timber.tag(LOG_TAG).d("showInternal: done $ad")
    }

    fun show(onComplete: () -> Unit = EmptyComplete): Job = scope.launch(start = UNDISPATCHED) {
        showInternal()
        onComplete()
    }

    fun load(): Job = scope.launch(start = UNDISPATCHED) {
        loadAdActionSharedFlow.emit(AdAction.Load)
    }

    companion object {
        private fun Flow<LoadAdErrorOrInterstitialAd>.log() =
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
        const val LOG_TAG = "InterstitialAdManager"
        private const val MAX_RETRIES = 3

        private val WAITING_TIMEOUT_DURATION = 5.seconds
        private val ADS_TIMEOUT_DURATION = 1.minutes
    }
}

object EmptyComplete : () -> Unit {
    override fun invoke() = Unit
}