package com.example.notes.ui.viewmodel

import com.example.notes.model.NotesRepo
import com.example.notes.model.auth.NoAuthException
import com.example.notes.ui.activities.SplashViewState
import kotlinx.coroutines.launch

class SplashViewModel(private val repo: NotesRepo) :
    BaseViewModel<Boolean>() {
    fun requestUser() {
        launch {
            try {
                repo.getCurrentUser()?.let {
                    setData(true)
                } ?: setError(NoAuthException())
            } catch ( e : Throwable ) {
                setError(NoAuthException())
            }
        }
    }
}