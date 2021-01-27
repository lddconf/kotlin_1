package com.example.notes.model

import com.example.notes.toRGBColor
import java.util.*

data class Note(
    val title: String,
    val text: String,
    val uid : String = UUID.randomUUID().toString(),
    val lastChanged: Date = Date(),
    val color: Int = PredefinedColor.DARK_BLUE.toRGBColor() ) {

    enum class PredefinedColor {
        WHITE,
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        DARK_BLUE,
        VIOLET
    }
}