package com.example.notes.model

class NotesRepo(private val networkNoteProvider: NetworkNoteProvider) {
    suspend fun getNotes() = networkNoteProvider.subscribeToNotes()
    suspend fun saveNote(note: Note) = networkNoteProvider.saveNote(note)
    suspend fun getNoteById(uid: String) = networkNoteProvider.getNoteById(uid)
    suspend fun deleteNote(uid: String) = networkNoteProvider.removeNote(uid)
    suspend fun getCurrentUser() = networkNoteProvider.getCurrentUser()
}