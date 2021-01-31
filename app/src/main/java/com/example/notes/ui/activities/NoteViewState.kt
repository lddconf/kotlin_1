package com.example.notes.ui.activities

import com.example.notes.model.Note

class NoteViewState (
    note: Note? = null,
    error: Throwable? = null
) : BaseViewState<Note?>(note, error)