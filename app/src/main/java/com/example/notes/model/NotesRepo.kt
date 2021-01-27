package com.example.notes.model

import com.example.notes.toRGBColor
import java.util.*

object NotesRepo  {
    val notes = listOf<Note>(
        Note("Title1", "Note 1 body", color = Note.PredefinedColor.ORANGE.toRGBColor() ),
        Note("Title2", "Note 2 body", color = Note.PredefinedColor.GREEN.toRGBColor() ),
        Note("Title3", "Note 3 body", color = Note.PredefinedColor.YELLOW.toRGBColor() ),
        Note("Title4", "Note 4 body", color = Note.PredefinedColor.BLUE.toRGBColor() ),
        Note("Title5", "Note 5 body", color = Note.PredefinedColor.DARK_BLUE.toRGBColor() ),
        Note("Title6", "Note 7 body", color = Note.PredefinedColor.RED.toRGBColor() ),
        Note("Title7", "Note 8 body", color = Note.PredefinedColor.VIOLET.toRGBColor() )
    )
}