package com.wakeup.hyperion.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.webkit.*
import com.thuanpx.ktext.view.gone
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.ExtraConstant.EXTRA_ARGS
import com.wakeup.hyperion.common.ExtraConstant.EXTRA_TITLE
import com.wakeup.hyperion.common.ExtraConstant.EXTRA_URL
import com.wakeup.hyperion.common.base.BaseActivity
import com.wakeup.hyperion.databinding.ActivityWebViewBinding
import com.wakeup.hyperion.utils.extension.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewActivity :
    BaseActivity<EmptyViewModel, ActivityWebViewBinding>(EmptyViewModel::class) {
    override fun inflateViewBinding(inflater: LayoutInflater): ActivityWebViewBinding {
        return ActivityWebViewBinding.inflate(inflater)
    }

    override fun initialize() {
        settingWebView()
        viewBinding.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun settingWebView() {
        with(viewBinding) {
            webView.setInitialScale(1)
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            webSettings.defaultZoom = WebSettings.ZoomDensity.FAR
            webSettings.builtInZoomControls = true
            val headers = HashMap<String, String>()
            webView.clearCache(true)
            clearCookies()
            //Load url
            var url = ""
            var title = ""
            intent?.let {
                intent.getBundleExtra(EXTRA_ARGS)?.let {
                    url = it.getString(EXTRA_URL).toString()
                    title = it.getString(EXTRA_TITLE).toString()
                    title.let {
                        if (title != "") {
                            toolBar.title = it
                        }
                    }
                }
            }
            webView.loadUrl(url, headers)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar.show()
                    super.onPageStarted(view, url, favicon)
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.gone()
                    super.onPageFinished(view, url)
                }
            }
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progressBar.progress = newProgress
                    if (newProgress == 100) {
                        // Hide the progressbar
                        progressBar.gone()
                    }
                    super.onProgressChanged(view, newProgress)
                }
            }
        }
    }

    private fun clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            val cookieSyncMngr = CookieSyncManager.createInstance(this)
            cookieSyncMngr.startSync()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncMngr.stopSync()
            cookieSyncMngr.sync()
        }
    }
}