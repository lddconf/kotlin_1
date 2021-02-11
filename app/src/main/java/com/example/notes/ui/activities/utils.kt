package com.example.notes

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.notes.model.Note

fun Note.PredefinedColor.toColorResId(): Int = when (this) {
    Note.PredefinedColor.WHITE -> R.color.note_white
    Note.PredefinedColor.RED -> R.color.red
    Note.PredefinedColor.ORANGE -> R.color.orange
    Note.PredefinedColor.YELLOW -> R.color.yellow
    Note.PredefinedColor.GREEN -> R.color.green
    Note.PredefinedColor.BLUE -> R.color.blue
    Note.PredefinedColor.DARK_BLUE -> R.color.dark_blue
    Note.PredefinedColor.VIOLET -> R.color.violet
}

fun Note.PredefinedColor.toColor(context: Context): Int =
    ContextCompat.getColor(context, this.toColorResId())

fun colorToPredefinedColor(
    context: Context,
    color: Int,
    defaultColor: Note.PredefinedColor
): Note.PredefinedColor = when (color) {
    Note.PredefinedColor.WHITE.toColor(context) -> Note.PredefinedColor.WHITE
    Note.PredefinedColor.RED.toColor(context) -> Note.PredefinedColor.RED
    Note.PredefinedColor.ORANGE.toColor(context) -> Note.PredefinedColor.ORANGE
    Note.PredefinedColor.YELLOW.toColor(context) -> Note.PredefinedColor.YELLOW
    Note.PredefinedColor.GREEN.toColor(context) -> Note.PredefinedColor.GREEN
    Note.PredefinedColor.BLUE.toColor(context) -> Note.PredefinedColor.BLUE
    Note.PredefinedColor.DARK_BLUE.toColor(context) -> Note.PredefinedColor.DARK_BLUE
    Note.PredefinedColor.VIOLET.toColor(context) -> Note.PredefinedColor.VIOLET
    else -> defaultColor
}

fun Context.dip(value: Int) = (value * resources.displayMetrics.density).toInt()