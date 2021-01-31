package com.example.notes.ui.activities

import android.os.Bundle
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.notes.R
import com.example.notes.ui.viewmodel.BaseViewModel
import com.example.notes.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity<T, VS : BaseViewState<T>> : AppCompatActivity() {
    abstract val viewModel: BaseViewModel<T, VS>
    abstract val layoutResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)

        viewModel.viewState().observe(this, { value ->
            value?.let {
                if (it.data != null) renderData(it.data)
                if (it.error != null) renderError(it.error)
            }
        })
    }

    abstract fun renderData(data: T)

    abstract fun renderError(error: Throwable)
}