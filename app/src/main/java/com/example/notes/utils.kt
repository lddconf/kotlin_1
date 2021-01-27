package com.example.notes

import com.example.notes.model.Note

fun Note.PredefinedColor.toRGBColor() : Int = when(this) {
            Note.PredefinedColor.WHITE -> R.color.white
            Note.PredefinedColor.RED -> R.color.red
            Note.PredefinedColor.ORANGE -> R.color.orange
            Note.PredefinedColor.YELLOW -> R.color.yellow
            Note.PredefinedColor.GREEN -> R.color.green
            Note.PredefinedColor.BLUE -> R.color.blue
            Note.PredefinedColor.DARK_BLUE -> R.color.dark_blue
            Note.PredefinedColor.VIOLET -> R.color.violet
}