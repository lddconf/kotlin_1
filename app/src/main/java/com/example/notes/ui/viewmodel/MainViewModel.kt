package com.example.notes.ui.viewmodel

import androidx.lifecycle.Observer
import com.example.notes.model.Note
import com.example.notes.model.NoteResult
import com.example.notes.model.NotesRepo
import com.example.notes.ui.activities.MainViewState


class MainViewModel(private val repo: NotesRepo = NotesRepo) : BaseViewModel<List<Note>?, MainViewState>() {
    private val notesObserver = object : Observer<NoteResult> {
        override fun onChanged(t: NoteResult?) {
            t?.apply {
                when (this) {
                    is NoteResult.Success<*> -> {
                        viewStateLiveData.value = MainViewState(notes = this.data as? List<Note>)
                    }
                    is NoteResult.Error -> {
                        viewStateLiveData.value = MainViewState(error = this.error)
                    }
                }
            }
        }
    }

    private val repoNotes = repo.getNotes()

    init {
        viewStateLiveData.value = MainViewState()
        repoNotes.observeForever(notesObserver)
    }

    fun removeNoteWithId(uid: String) {
        repo.removeNoteWithId(uid)
    }

    override fun onCleared() {
        repoNotes.removeObserver(notesObserver)
    }
}