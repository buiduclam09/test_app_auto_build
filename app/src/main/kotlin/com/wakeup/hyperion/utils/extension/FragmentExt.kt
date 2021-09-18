package com.wakeup.hyperion.utils.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.wakeup.hyperion.common.ExtraConstant.EXTRA_ARGS
import kotlin.reflect.KClass

fun <T : Activity> Fragment.goToForResult(
    cls: KClass<T>, bundle: Bundle? = null,
    parcel: Parcelable? = null,
    requestCode: Int
) {
    val intent = Intent(context, cls.java)
    if (bundle != null) intent.putExtra(EXTRA_ARGS, bundle)
    if (parcel != null) intent.putExtra(EXTRA_ARGS, parcel)
    startActivityForResult(intent, requestCode)
}
fun <T : Activity> Fragment.goTo(
    cls: KClass<T>, bundle: Bundle? = null,
    parcel: Parcelable? = null
) {
    val intent = Intent(context, cls.java)
    if (bundle != null) intent.putExtra(EXTRA_ARGS, bundle)
    if (parcel != null) intent.putExtra(EXTRA_ARGS, parcel)
    startActivity(intent)
}