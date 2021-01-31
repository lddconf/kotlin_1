package com.example.notes.model

object NotesRepo  {
/*
    private val notesLiveData = MutableLiveData<List<Note>>()

    private val notes = mutableListOf<Note>(
        Note("Title1", "Note 1 body", color = Note.PredefinedColor.ORANGE.toRGBColor() ),
        Note("Title2", "Note 2 body", color = Note.PredefinedColor.GREEN.toRGBColor() ),
        Note("Title3", "Note 3 body", color = Note.PredefinedColor.YELLOW.toRGBColor() ),
        Note("Title4", "Note 4 body", color = Note.PredefinedColor.BLUE.toRGBColor() ),
        Note("Title5", "Note 5 body", color = Note.PredefinedColor.DARK_BLUE.toRGBColor() ),
        Note("Title6", "Note 7 body", color = Note.PredefinedColor.RED.toRGBColor() ),
        Note("Title7", "Note 8 body", color = Note.PredefinedColor.VIOLET.toRGBColor() )
    )



    fun saveNote(note : Note) {
        addOrReplace(note)
        notesLiveData.value = notes
    }

    fun eraseNote( note: Note ) {
        removeNote(note)
        notesLiveData.value = notes
    }

    private fun addOrReplace(note : Note) {
        for (i in 0 until notes.size) {
            if (notes[i] == note) {
                notes[i] = note
                return
            }
        }
        notes.add(note)
    }

    private fun removeNote(note: Note) {
        for ( i in 0 until notes.size) {
            if (notes[i] == note) {
                notes.removeAt(i)
                return
            }
        }
    }

    init {
        notesLiveData.value = notes
    }
 */
    private val networkNoteProvider = FireBaseCloudNoteProvider()

    fun getNotes() = networkNoteProvider.subscribeToNotes()

    fun saveNote(note: Note) = networkNoteProvider.saveNote(note)

    fun getNoteById(uid :String) = networkNoteProvider.getNoteById(uid)

    fun deleteNoteWithId(uid: String) = networkNoteProvider.deleteNoteWithId(uid)
}