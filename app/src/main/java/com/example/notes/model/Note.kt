package com.example.notes.model

import com.example.notes.R
import java.util.*

data class Note(
    val title: String,
    val text: String,
    val lastChanged: Date,
    val color: Int = 0x000000 ) {

    enum class PredefinedColor(val code: Int) {
        WHITE(R.color.white),
        RED(R.color.red),
        ORANGE(R.color.orange),
        YELLOW(R.color.yellow),
        GREEN(R.color.green),
        BLUE(R.color.blue),
        DARK_BLUE(R.color.dark_blue),
        VIOLET(R.color.violet)
    }
}