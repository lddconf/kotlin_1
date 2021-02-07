package com.example.notes.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.ui.activities.BaseViewState

open class BaseViewModel<T, VS : BaseViewState<T>> : ViewModel() {
    protected val viewStateLiveData = MutableLiveData<VS>()

    open fun viewState(): LiveData<VS> = viewStateLiveData
}