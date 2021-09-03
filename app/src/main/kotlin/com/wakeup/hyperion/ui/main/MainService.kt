package com.wakeup.hyperion.ui.main

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.NotificationCompat
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.ui.turnOff.TurnOffActivity
import timber.log.Timber
import java.util.*


/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/3/21.
 */
class MainService : Service() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private var mediaPlayer: MediaPlayer? = null
    private var signal = Constant.DEFAULT_SIGNAL

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
            0
        )

        val notification = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle("Where is it!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1234, notification)
        signal = intent?.getStringExtra(Constant.EXTRA_SIGNAL) ?: Constant.DEFAULT_SIGNAL
        initSpeechRecognizer()
        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.pause()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        super.onDestroy()
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Timber.i("onReadyForSpeech")
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
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> getString(R.string.recognition_service_busy)
                        SpeechRecognizer.ERROR_SERVER -> getString(R.string.error_from_server)
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> getString(R.string.no_speech_input)
                        else -> getString(R.string.default_speech_input)
                    }
                    Timber.e(messageError)
                    startSpeechToText()
                }

                override fun onResults(results: Bundle) {
                    var str = ""
                    val resultList =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (resultList != null) {
                        str += resultList[0]
                    }
                    Timber.i("onResults $str")
                    if (str == signal) {
                        startSound()
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

    private fun startSound() {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, TurnOffActivity::class.java),
            0
        )

        val notification = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle("Click to turn off the music!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.notify(1234, notification)

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.check_sound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        // This flag is deprecated on Android 10. There is no any documentation related to this issue.
        //intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)

        // Use this to start default Android Speech Recognition activity and parse the result in onActivityResult
        //startActivityForResult(intent, STT_ACTIVITY_RESULT_CODE)
        speechRecognizer.startListening(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "RWD",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "2318201"
    }

}