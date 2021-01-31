package com.example.notes.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.ui.viewmodel.BaseViewModel


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