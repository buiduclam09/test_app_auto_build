package com.wakeup.hyperion.utils.extension


inline fun <T : Any> T?.notNull(f: (it: T) -> Unit) {
    if (this != null) f(this)
}