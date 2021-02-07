package com.example.notes.model

object NotesRepo {
    private val networkNoteProvider = FireBaseCloudNoteProvider()

    fun getNotes() = networkNoteProvider.subscribeToNotes()

    fun saveNote(note: Note) = networkNoteProvider.saveNote(note)

    fun getNoteById(uid: String) = networkNoteProvider.getNoteById(uid)

    fun removeNoteWithId(uid: String) = networkNoteProvider.removeNoteWithId(uid)

    fun getCurrentUser() = networkNoteProvider.getCurrentUser()

}