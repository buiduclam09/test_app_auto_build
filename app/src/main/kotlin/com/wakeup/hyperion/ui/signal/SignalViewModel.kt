package com.wakeup.hyperion.ui.signal

import com.wakeup.hyperion.common.base.BaseViewModel
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Copyright © 2021 Neolab VN.
 * Created by ThuanPx on 3/1/21.
 */
@HiltViewModel
class SignalViewModel @Inject constructor(
   val sharedPrefsRepository: SharedPrefsRepository
) : BaseViewModel() {
}