package com.example.notes.model

import androidx.lifecycle.LiveData
import com.example.notes.model.auth.User

interface NetworkNoteProvider {
    fun subscribeToNotes(): LiveData<NoteResult>

    fun getNoteById(uid: String): LiveData<NoteResult>

    fun saveNote(note: Note): LiveData<NoteResult>

    fun removeNote(uid: String): LiveData<NoteResult>

    fun getCurrentUser() : LiveData<User?>
}