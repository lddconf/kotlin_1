package com.example.notes.ui.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import com.example.notes.model.Note
import com.example.notes.model.NoteResult
import com.example.notes.model.NotesRepo
import com.example.notes.ui.activities.MainViewState


class MainViewModel(private val repo: NotesRepo) : BaseViewModel<List<Note>?, MainViewState>() {
    private val notesObserver = Observer<NoteResult> { t ->
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

    private val repoNotes = repo.getNotes()

    private var recentDeletedNote : Note? = null

    init {
        viewStateLiveData.value = MainViewState()
        repoNotes.observeForever(notesObserver)
    }

    fun deleteNote(uid: String) {
        (viewStateLiveData.value as MainViewState).apply {
            recentDeletedNote = this.notes?.filter { note ->
                note.uid == uid
            }?.firstOrNull()
        }
        repo.deleteNote(uid)
    }

    fun undoLastDeletedNote() {
        recentDeletedNote?.let { note ->
            repo.saveNote(note)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onCleared() {
        repoNotes.removeObserver(notesObserver)
    }
}