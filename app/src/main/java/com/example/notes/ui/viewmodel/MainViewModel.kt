package com.example.notes.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.model.NotesRepo
import com.example.notes.ui.activities.MainViewState

class MainViewModel : ViewModel() {
    private val viewStateData  = MutableLiveData<MainViewState>()

    init {
        NotesRepo.getNotes().observeForever {
            viewStateData.value = viewStateData.value?.copy(notes = it) ?: MainViewState(it)
        }
    }

    fun viewState() : LiveData<MainViewState> = viewStateData
}