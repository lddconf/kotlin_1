package com.example.notes.model

import androidx.lifecycle.LiveData

interface NetworkNoteProvider {
    fun subscribeToNotes(): LiveData<NoteResult>

    fun getNoteById(uid: String): LiveData<NoteResult>

    fun saveNote(note: Note): LiveData<NoteResult>

    fun removeNoteWithId(uid: String): LiveData<NoteResult>
}