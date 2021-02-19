package com.example.notes.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.notes.model.Note
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.CoroutineContext

open class BaseViewModel<T> : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + Job()
    }

    private val viewStateChannel = BroadcastChannel<T>(Channel.CONFLATED)
    private val errorChannel = Channel<Throwable>()

    fun viewState(): ReceiveChannel<T> = viewStateChannel.openSubscription()
    fun getErrorChannel(): ReceiveChannel<Throwable> = errorChannel

    protected fun setError(e: Throwable) {
        launch {
            errorChannel.send(e)
        }
    }

    protected fun setData(data: T) {
        launch {
            viewStateChannel.send(data)
        }
    }

    override fun onCleared() {
        viewStateChannel.close()
        errorChannel.close()
        coroutineContext.cancel()
        super.onCleared()
    }
}