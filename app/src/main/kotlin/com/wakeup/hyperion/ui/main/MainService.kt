package com.wakeup.hyperion.ui.main

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.NotificationCompat
import com.thuanpx.ktext.string.toIntOrZero
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.ui.turnOff.TurnOffActivity
import com.wakeup.hyperion.utils.LanguageSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random


/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/3/21.
 */
@AndroidEntryPoint
class MainService : Service() {
    @Inject
    lateinit var sharedPrefsRepository: SharedPrefsRepository
    private lateinit var speechRecognizer: SpeechRecognizer
    private var signal = Constant.DEFAULT_SIGNAL
    private var signalLocalModel: SignalLocalModel =
        SignalLocalModel(true, Constant.DEFAULT_SOUND, R.raw.check_sound.toString(), false)
    private val defaultLanguage = "en-US"
    private val currentLanguage: String
    get() = when (sharedPrefsRepository.getLanguage()) {
        LanguageSettings.FRENCH -> "fr"
        else -> {
            "en-US"
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID,
        )
            .setContentTitle("Where is it!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(0)
            .build()

        startForeground(1234, notification)
        val newSignal = intent?.getStringExtra(Constant.EXTRA_SIGNAL)
        if (!newSignal.isNullOrBlank()) {
            signal = newSignal
        }
        intent?.getParcelableExtra<SignalLocalModel>(Constant.EXTRA_SIGNAL_MODEL)?.let {
            signalLocalModel = it
        }
        val amanager = getSystemService(AUDIO_SERVICE) as AudioManager
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true)
        initSpeechRecognizer()
        return START_STICKY
    }

    override fun onDestroy() {
        speechRecognizer.stopListening()
        speechRecognizer.cancel()
        speechRecognizer.destroy()
        super.onDestroy()
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Timber.i("onReadyForSpeech $currentLanguage")
                }

                override fun onBeginningOfSpeech() {
                }

                override fun onRmsChanged(rmsdB: Float) {
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                }

                override fun onEndOfSpeech() {
                    Timber.i("onEndOfSpeech")
                }

                override fun onError(error: Int) {
                    val messageError: String = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> getString(R.string.audio_recording_error)
                        SpeechRecognizer.ERROR_CLIENT -> getString(R.string.client_side_error)
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> getString(R.string.insufficient_permissions)
                        SpeechRecognizer.ERROR_NETWORK -> getString(R.string.network_error)
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> getString(R.string.network_timeout)
                        SpeechRecognizer.ERROR_NO_MATCH -> getString(R.string.no_match)
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                            getString(R.string.recognition_service_busy)
                        }
                        SpeechRecognizer.ERROR_SERVER -> getString(R.string.error_from_server)
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> getString(R.string.no_speech_input)
                        else -> getString(R.string.default_speech_input)
                    }
                    Timber.e(messageError)
                    if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                        speechRecognizer.stopListening()
                        speechRecognizer.cancel()
                        speechRecognizer.destroy()
                        initSpeechRecognizer()
                    } else {
                        startSpeechToText()
                    }
                }

                override fun onResults(results: Bundle) {
                    var str = ""
                    val resultList =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (resultList != null) {
                        str += resultList[0]
                    }
                    Timber.i("onResults $str")
                    Timber.i("Signal $signal")
                    if (str.isNotBlank() && signal.contains(str) ) {
                        Timber.i("start Sound")
                        val intent = Intent(applicationContext, TurnOffActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
//                        startSound()
                    }
                    startSpeechToText()
                }

                override fun onPartialResults(partialResults: Bundle?) {
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                }
            })
            startSpeechToText()
        }
    }

    fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, currentLanguage)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, currentLanguage)
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, currentLanguage)
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,currentLanguage)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, currentLanguage)
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS, currentLanguage)

        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        // This flag is deprecated on Android 10. There is no any documentation related to this issue.
        //intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)

        // Use this to start default Android Speech Recognition activity and parse the result in onActivityResult
        //startActivityForResult(intent, STT_ACTIVITY_RESULT_CODE)
        speechRecognizer.startListening(intent)
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "RWD",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        notificationChannel.apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(false)
        }
        manager.createNotificationChannel(notificationChannel)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "2318201"
    }

}