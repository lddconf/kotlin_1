package com.example.notes.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.Note
import kotlinx.android.synthetic.main.note_preview_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotesRVAdapter : RecyclerView.Adapter<NotesRVAdapter.NoteRVHolder>() {

    var notes : List<Note> = listOf()
        set (value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteRVHolder {
        return NoteRVHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.note_preview_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteRVHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    class NoteRVHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: Note) = with(itemView) {
            note_title.text = note.title
            note_date.text = SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault()).format(note.lastChanged)
            note_body.text = note.text

            this as CardView
            this.setCardBackgroundColor(ResourcesCompat.getColor(resources, note.color, null));
        }
    }
}