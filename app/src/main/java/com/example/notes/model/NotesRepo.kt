package com.example.notes.model

import java.util.*

object NotesRepo  {
    val notes = listOf<Note>(
        Note("Title1", "Note 1 body", Calendar.getInstance().time, Note.PredefinedColor.ORANGE.code ),
        Note("Title2", "Note 2 body", Calendar.getInstance().time, Note.PredefinedColor.GREEN.code ),
        Note("Title3", "Note 3 body", Calendar.getInstance().time, Note.PredefinedColor.YELLOW.code ),
        Note("Title4", "Note 4 body", Calendar.getInstance().time, Note.PredefinedColor.BLUE.code ),
        Note("Title5", "Note 5 body", Calendar.getInstance().time, Note.PredefinedColor.DARK_BLUE.code ),
        Note("Title6", "Note 7 body", Calendar.getInstance().time, Note.PredefinedColor.RED.code ),
        Note("Title7", "Note 8 body", Calendar.getInstance().time, Note.PredefinedColor.VIOLET.code )
    )
}