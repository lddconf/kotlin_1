package com.example.notes.model

 class NotesRepo(private val networkNoteProvider : NetworkNoteProvider ) {
    fun getNotes() = networkNoteProvider.subscribeToNotes()

    fun saveNote(note: Note) = networkNoteProvider.saveNote(note)

    fun getNoteById(uid: String) = networkNoteProvider.getNoteById(uid)

    fun deleteNote(uid: String) = networkNoteProvider.removeNote(uid)

    fun getCurrentUser() = networkNoteProvider.getCurrentUser()

}