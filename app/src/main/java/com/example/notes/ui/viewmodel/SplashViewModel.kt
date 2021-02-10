package com.example.notes.ui.viewmodel

import com.example.notes.model.NotesRepo
import com.example.notes.model.auth.NoAuthException
import com.example.notes.ui.activities.SplashViewState

class SplashViewModel(private val repo: NotesRepo) :
    BaseViewModel<Boolean?, SplashViewState>() {
    fun requestUser() {
        repo.getCurrentUser().observeForever { user ->
            viewStateLiveData.value = user?.let {
                SplashViewState(isAuth = true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }
}