package com.example.notes.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Note(
    val title: String = "",
    val text: String = "",
    val uid: String = UUID.randomUUID().toString(),
    val lastChanged: Date = Date(),
    var color: PredefinedColor = PredefinedColor.RED
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note
        return this.uid == other.uid
    }

    override fun hashCode(): Int {
        return this.uid.hashCode()
    }

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