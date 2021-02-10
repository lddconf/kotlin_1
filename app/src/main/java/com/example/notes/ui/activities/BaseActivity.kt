package com.example.notes.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.ui.viewmodel.BaseViewModel


abstract class BaseActivity<T, VS : BaseViewState<T>> : AppCompatActivity() {
    abstract val viewModel: BaseViewModel<T, VS>
    abstract val layoutResourceId: Int
    abstract val ui : ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        viewModel.viewState().observe(this) { value ->
            value?.apply {
                data?.let { renderData(it) }
                error?.let { renderError(it) }
            }
        }
    }

    protected abstract fun renderData(data: T)

    protected abstract fun renderError(error: Throwable)
}