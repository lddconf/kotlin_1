package com.example.notes.ui.activities

import com.example.notes.model.Note

class MainViewState(
    val notes: List<Note>? = null,
    error: Throwable? = null
) : BaseViewState<List<Note>?>(notes, error)