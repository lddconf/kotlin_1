package com.example.notes.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.ui.viewmodel.BaseViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext


abstract class BaseActivity<T> : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }

    private lateinit var dataJob: Job
    private lateinit var errorJob: Job


    abstract val viewModel: BaseViewModel<T>
    abstract val layoutResourceId: Int
    abstract val ui: ViewBinding

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
    }

    override fun onStart() {
        super.onStart()

        dataJob = launch {
            viewModel.viewState().consumeEach {
                renderData(it)
            }
        }

        errorJob = launch {
            viewModel.getErrorChannel().consumeEach {
                renderError(it)
            }
        }
    }

    override fun onStop() {
        dataJob.cancel()
        errorJob.cancel()
        super.onStop()
    }

    override fun onDestroy() {

        coroutineContext.cancel()
        super.onDestroy()
    }

    protected abstract fun renderData(data: T)

    protected open fun renderError(error: Throwable) {
        Snackbar.make(
            ui.root,
            error.message.toString(),
            Snackbar.LENGTH_LONG
        ).show()
    }
}