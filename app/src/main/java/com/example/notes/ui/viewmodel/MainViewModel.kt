package com.example.notes.ui.viewmodel

import androidx.annotation.VisibleForTesting
import com.example.notes.model.Note
import com.example.notes.model.NoteResult
import com.example.notes.model.NotesRepo
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainViewModel(private val repo: NotesRepo) : BaseViewModel<List<Note>?>() {

    private val notesChannel by lazy {
        runBlocking {
            repo.getNotes()
        }
    }

    init {
        launch {
            notesChannel.consumeEach { noteResult ->
                when (noteResult) {
                    is NoteResult.Success<*> -> setData(noteResult.data as? List<Note>)
                    is NoteResult.Error -> setError(noteResult.error)
                }
            }
        }
    }

    private var recentDeletedNote : Note? = null

    fun deleteNote(uid: String) {
        /*
        (viewStateLiveData.value as MainViewState).apply {
            recentDeletedNote = this.notes?.filter { note ->
                note.uid == uid
            }?.firstOrNull()
        }*/
        launch {
            try {
                repo.deleteNote(uid)
            } catch (e : Throwable) {
                setError(e)
            }
        }
    }

    fun undoLastDeletedNote() {
        recentDeletedNote?.let { note ->
            launch {
                try {
                    repo.saveNote(note)
                    //setData(note)
                } catch (e : Throwable) {
                    setError(IllegalArgumentException())
                }
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onCleared() {
        notesChannel.cancel()
        super.onCleared()
    }
}