package com.example.notes.ui.viewmodel

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.notes.model.Note
import com.example.notes.model.NoteResult
import com.example.notes.model.NotesRepo
import com.example.notes.ui.activities.NoteViewState
import java.sql.Struct

class NoteViewModel(private val repo: NotesRepo = NotesRepo) :
    BaseViewModel<Note?, NoteViewState>() {
    private var currentNote: Note? = null

    fun saveChanges(note: Note) {
        currentNote = note
    }

    override fun onCleared() {
        currentNote?.let { note ->
            repo.saveNote(note)
        }

    }

    fun loadNote(uid: String) {
        repo.getNoteById(uid).observeForever { observer ->
            observer.apply {
                when (this) {
                    is NoteResult.Success<*> ->
                        viewStateLiveData.value = NoteViewState(this.data as? Note)
                    is NoteResult.Error ->
                        viewStateLiveData.value = NoteViewState(error = this.error)
                }
            }
        }
    }
}