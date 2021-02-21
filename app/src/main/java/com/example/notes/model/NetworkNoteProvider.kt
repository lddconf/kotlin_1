package com.example.notes.model

import androidx.lifecycle.LiveData
import com.example.notes.model.auth.User
import kotlinx.coroutines.channels.ReceiveChannel

interface NetworkNoteProvider {
    suspend fun subscribeToNotes(): ReceiveChannel<NoteResult>
    suspend fun getNoteById(uid: String): Note
    suspend fun saveNote(note: Note): Note
    suspend fun removeNote(uid: String): Note?
    suspend fun getCurrentUser(): User?
}