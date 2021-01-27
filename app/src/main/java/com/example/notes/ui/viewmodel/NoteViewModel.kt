package com.example.notes.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.notes.model.Note
import com.example.notes.model.NotesRepo

class NoteViewModel(private val repo : NotesRepo = NotesRepo) : ViewModel() {
    private var currentNote: Note? = null

    fun saveChanges(note: Note) {
        currentNote = note
    }

    override fun onCleared() {
        currentNote?.let {
            repo.saveNote(it)
        }
    }
}