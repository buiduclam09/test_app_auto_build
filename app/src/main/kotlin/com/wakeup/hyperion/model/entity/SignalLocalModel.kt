package com.wakeup.hyperion.model.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SignalLocalModel(
    val isBasic: Boolean = false,
    val name: String? = null,
    val path: String,
    var isPlaying: Boolean? = false,
    val disPlayName : String? = null,
    var isSelected: Boolean = false
): Parcelable