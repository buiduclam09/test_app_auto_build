package com.wakeup.hyperion.ui.main

import InterstitialAdManager
import android.Manifest
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.thuanpx.ktext.view.gone
import com.thuanpx.ktext.view.show
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.base.BaseActivity
import com.wakeup.hyperion.databinding.ActivityMainBinding
import com.wakeup.hyperion.ui.signal.changeSound.childTab.CustomSoundFragment.Companion.REQUEST_CODE
import com.wakeup.hyperion.utils.FileUtils.getRealPathFromURI
import com.wakeup.hyperion.utils.extension.setStatusBarColor
import com.wakeup.hyperion.utils.extension.setupWithNavController
import com.wakeup.hyperion.dialogManager.DialogAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(MainViewModel::class) {
    private var currentNavController: LiveData<NavController>? = null
    private var settingsContentObserver: SettingsContentObserver? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    @Inject
    lateinit var interstitialAdManager: InterstitialAdManager

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (!it.value) {
                    showAlertDialog(
                        title = "",
                        message = resources.getString(R.string.text_notification_permission_record_audio),
                        "",
                        object : DialogAlert.Companion.OnButtonClickedListener {
                            override fun onPositiveClicked() {
                                finish()
                            }
                        }
                    )
                    return@registerForActivityResult
                }
            }
            stopService(Intent(this, MainService::class.java))
            startService(Intent(this, MainService::class.java).apply {
                putExtra(Constant.EXTRA_SIGNAL, viewModel.signal)
                putExtra(Constant.EXTRA_SIGNAL_MODEL, viewModel.signalLocalModel)
            })
        }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun onDestroy() {
        settingsContentObserver?.let {
            applicationContext.contentResolver.unregisterContentObserver(
                it
            )
        }
        super.onDestroy()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initialize() {
        interstitialAdManager.load()
        interstitialAdManager.show {

        }
        requestPermission.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        viewModel.getListPath()
        setUpBottomNavigation()
    }

    override fun onSubscribeObserver() {
        super.onSubscribeObserver()
        settingsContentObserver = SettingsContentObserver(Handler()) {
            viewModel.updateVolume.postValue(Unit)
        }
        applicationContext.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI, true, settingsContentObserver!!
        )
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
        bottomNavigationView.itemIconTintList = null

        currentNavController?.observe(this) {
            if (it.currentDestination?.label == "SettingFragment") {
                setStatusBarColor(R.color.C_E6EBEF)
            } else {
                setStatusBarColor(android.R.color.white)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && data?.data != null) {
            val audioSelect = data.data
            val pathSelect = getRealPathFromURI(
                this@MainActivity,
                this@MainActivity.contentResolver,
                audioSelect
            )
            //1.check path same trong list neu path khac thi add vao sharePrerence
            with(viewModel) {
                if (listAudio.size == 0) {
                    pathSelect.let {
                        listAudio.add(it)
                        saveListUriAudio(listAudio)
                    }
                } else if (listAudio.size > 0) {
                    pathSelect.let { path ->
                        if (listAudio.contains(path)) {
                            return
                        }
                        listAudio.add(path)
                        saveListUriAudio(listAudio)
                    }
                }
                lifecycleScope.launch {
                    viewModel.addNewSongFlow.emit(true)
                }
            }
        }
    }
}
