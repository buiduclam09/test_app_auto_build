package com.wakeup.hyperion.common.base

import com.wakeup.hyperion.utils.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

abstract class BaseRepository(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * Make template code to get DataResult return to ViewModel
     * Support for call api, get data from database
     * Handle exceptions: Convert exception to Result.Error
     * Avoid duplicate code
     *
     * Default CoroutineContext is IO for repository
     */
    protected suspend fun <R> withResultContext(
        context: CoroutineContext = coroutineDispatcher,
        requestBlock: suspend CoroutineScope.() -> R,
        errorBlock: (suspend CoroutineScope.(Exception) -> DataResult.Error)? = null
    ): DataResult<R> = withContext(context) {
        return@withContext try {
            val response = requestBlock()
            DataResult.Success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext errorBlock?.invoke(this, e) ?: DataResult.Error(e)
        }
    }

    protected suspend fun <R> withResultContext(
        context: CoroutineContext = coroutineDispatcher,
        requestBlock: suspend CoroutineScope.() -> R
    ): DataResult<R> = withResultContext(context, requestBlock, null)
}
