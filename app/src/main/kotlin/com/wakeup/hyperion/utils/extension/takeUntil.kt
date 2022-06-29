package com.wakeup.hyperion.utils.extension

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

/**
 * Emits the values emitted by the source [Flow] until a [notifier] [Flow] emits a value or completes.
 *
 * @param notifier The [Flow] whose first emitted value or complete event
 * will cause the output [Flow] of [takeUntil] to stop emitting values from the source [Flow].
 */
public fun <T, R> Flow<T>.takeUntil(notifier: Flow<R>): Flow<T> = flow {
    try {
        coroutineScope {
            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                notifier.take(1).collect()
                throw ClosedException(this@flow)
            }

            collect { emit(it) }
            job.cancel()
        }
    } catch (e: ClosedException) {
        e.checkOwnership(this@flow)
    }
}
internal class ClosedException(val owner: FlowCollector<*>) :
    Exception("Flow was aborted, no more elements needed")

internal fun ClosedException.checkOwnership(owner: FlowCollector<*>) {
    if (this.owner !== owner) throw this
}