package com.wakeup.hyperion.ui.signal.changeSound.childTab

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.thuanpx.ktext.boolean.isNotTrue
import com.thuanpx.ktext.boolean.isTrue
import com.thuanpx.ktext.view.gone
import com.thuanpx.ktext.view.show
import com.wakeup.hyperion.BuildConfig
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.Constant.TYPE_DELETE
import com.wakeup.hyperion.common.Constant.TYPE_SELECTED
import com.wakeup.hyperion.common.base.BaseFragment
import com.wakeup.hyperion.databinding.FragmentCustomSoundBinding
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.ui.main.MainService
import com.wakeup.hyperion.ui.main.MainViewModel
import com.wakeup.hyperion.ui.signal.SignalViewModel
import com.wakeup.hyperion.ui.signal.changeSound.adapter.CustomSoundAdapter
import com.wakeup.hyperion.utils.PermissionUtils.checkPermissionReadStorage
import com.wakeup.hyperion.utils.ResourcesManager
import com.wakeup.hyperion.utils.extension.clicks
import com.wakeup.hyperion.dialogManager.DialogAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@AndroidEntryPoint
class CustomSoundFragment :
    BaseFragment<SignalViewModel, FragmentCustomSoundBinding>(SignalViewModel::class) {
    private val sharedViewModel by activityViewModels<MainViewModel>()
    private var customSoundAdapter: CustomSoundAdapter? = null
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    private var interstitialAd: InterstitialAd? = null

    companion object {
        fun newInstance() = CustomSoundFragment()
        const val REQUEST_CODE = 1006
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCustomSoundBinding {
        return FragmentCustomSoundBinding.inflate(inflater, container, false)
    }

    override fun initialize() {
        initAdapter()
        handleOnclick()
    }

    override fun onSubscribeObserver() {
        super.onSubscribeObserver()
        lifecycleScope.launch {
            sharedViewModel.updateTabFlow.collect {
                if (!it) {
                    removeAllSelected()
                }
            }
        }
        lifecycleScope.launch {
            sharedViewModel.addNewSongFlow.collect {
                initData()
                customSoundAdapter?.notifyDataSetChanged()
                checkSignalSelected()
            }
        }
    }

    private fun initAdapter() {
        customSoundAdapter =
            CustomSoundAdapter() { type: String, signalLocalModel: SignalLocalModel, position: Int ->
                if (type == TYPE_SELECTED) {
                    customSoundAdapter?.getData()?.forEach {
                        it.isSelected = false
                    }
                    signalLocalModel.isSelected = true
                    sharedViewModel.saveSignalSound(signalLocalModel)
                    activity?.stopService(Intent(requireContext(), MainService::class.java))
                    activity?.startService(Intent(requireContext(), MainService::class.java).apply {
                        putExtra(Constant.EXTRA_SIGNAL, sharedViewModel.signal)
                        putExtra(Constant.EXTRA_SIGNAL_MODEL, sharedViewModel.signalLocalModel)
                    })
                } else if (type == TYPE_DELETE) {
                    run checkSignal@{
                        customSoundAdapter?.getData()?.forEachIndexed { index, it ->
                            if (it == signalLocalModel) {
                                customSoundAdapter?.removeItemNoNotify(index)
                                sharedViewModel.listAudio.removeAt(index)
                                sharedViewModel.saveListUriAudio(sharedViewModel.listAudio)
                                if (sharedViewModel.signalLocalModel?.isBasic.isNotTrue() &&
                                    sharedViewModel.signalLocalModel?.path == it.path
                                ) {
                                    sharedViewModel.clearSignalSound()
                                }
                                return@checkSignal
                            }
                        }
                    }
                }
                customSoundAdapter?.notifyDataSetChanged()
            }
        initData()
        with(viewBinding.rvCustomSound) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = customSoundAdapter
        }
        checkSignalSelected()
        if (!viewModel.isShowCusTomTab) {
            showAds()
        }
    }

    private fun showAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            BuildConfig.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    viewModel.isShowCusTomTab = true
                    this@CustomSoundFragment.interstitialAd = interstitialAd
                    this@CustomSoundFragment.interstitialAd?.show(requireActivity())
                }
            })
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }
        }
    }

    private fun checkSignalSelected() {
        Handler(Looper.getMainLooper()).post {
            sharedViewModel.signalLocalModel?.let { signal ->
                if (signal.isBasic.isTrue()) {
                    return@let
                }
                customSoundAdapter?.getData()?.forEach {
                    if (it.name == signal.name) {
                        it.isSelected = true
                        customSoundAdapter?.notifyDataSetChanged()
                        return@let
                    }
                }
            }
        }
    }

    private fun handleOnclick() {
        with(viewBinding) {
            btnAddSound.clicks {
                if (checkPermissionReadStorage(requireContext())) {
                    val intent = Intent(
                        Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        )
                    )
                    getMainActivity()?.startActivityForResult(
                        Intent.createChooser(intent, "Gallery"),
                        REQUEST_CODE
                    )
                    return@clicks
                }
                showAlertDialog(
                    title = "",
                    message = resources.getString(R.string.text_notification_permission_read_file),
                    titleButton = "",
                    listener = object : DialogAlert.Companion.OnButtonClickedListener {
                        override fun onPositiveClicked() {
                            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                )
            }
        }
    }

    private fun removeAllSelected() {
        customSoundAdapter?.getData()?.forEach {
            it.isSelected = false
        }
        customSoundAdapter?.notifyDataSetChanged()
    }

    private fun initData() {
        if (sharedViewModel.listAudio.isNotEmpty()) {
            customSoundAdapter?.replaceData(
                ResourcesManager.createListAudioFromListUri(
                    sharedViewModel.listAudio,
                    requireContext()
                )
            )
            viewBinding.rvCustomSound.show()
            viewBinding.tvNotification.gone()
        } else {
            viewBinding.tvNotification.show()
            viewBinding.rvCustomSound.gone()
        }
    }
}