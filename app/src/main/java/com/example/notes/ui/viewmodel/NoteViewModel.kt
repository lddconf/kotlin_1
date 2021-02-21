package com.example.notes.ui.viewmodel

import com.example.notes.model.Note
import com.example.notes.model.NotesRepo
import com.example.notes.ui.activities.NoteViewState
import kotlinx.coroutines.launch
import java.sql.Struct

class NoteViewModel(private val repo: NotesRepo) :
    BaseViewModel<Note?>() {

    private val currentNote: Note?
        get() = viewState().poll()


    fun saveChanges(note: Note) {
        launch {
            try {
                setData(repo.saveNote(note))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    fun loadNote(uid: String) {
        launch {
            try {
                setData(repo.getNoteById(uid))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    override fun onCleared() {
        launch {
            currentNote?.let { repo.saveNote(it) }
        }
        super.onCleared()
    }
}