package com.wakeup.hyperion.ui.main

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.thuanpx.ktext.boolean.isTrue
import com.thuanpx.ktext.view.gone
import com.thuanpx.ktext.view.show
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.base.BaseActivity
import com.wakeup.hyperion.databinding.ActivityMainBinding
import com.wakeup.hyperion.utils.extension.setupWithNavController
import com.wakeup.hyperion.widget.dialogManager.DialogAlert
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(MainViewModel::class) {
    private var currentNavController: LiveData<NavController>? = null

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun initialize() {
        setUpBottomNavigation()
        requestPermission.launch(Manifest.permission.RECORD_AUDIO)
        startService(Intent(this, MainService::class.java).apply {
            putExtra(Constant.EXTRA_SIGNAL, viewModel.signal )
        })
    }

    override fun onSubscribeObserver() {
        super.onSubscribeObserver()
    }

    fun showBottomNavigation() {
        viewBinding.bottomMenu.show()
    }

    fun goneBottomNavigation() {
        viewBinding.bottomMenu.gone()
    }

    private fun setUpBottomNavigation() {
        val bottomNavigationView = viewBinding.bottomMenu
        val navGraphId = listOf(
            R.navigation.navigation_signal,
            R.navigation.navigation_setting,
        )
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphId,
            fragmentManager = supportFragmentManager,
            containerId = R.id.fragmentContainerView,
            intent = intent
        )
        currentNavController = controller
    }
}
