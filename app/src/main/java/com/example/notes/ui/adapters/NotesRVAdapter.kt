package com.example.notes.ui.adapters

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.Note
import kotlinx.android.synthetic.main.note_preview_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

interface OnItemClickListener {
    fun onItemClick(note: Note)
}

class NotesRVAdapter(var onItemClickListener: OnItemClickListener? = null) : RecyclerView.Adapter<NotesRVAdapter.NoteRVHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
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

    inner class NoteRVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: Note) = with(itemView) {
            note_title.text = note.title
            note_date.text = SimpleDateFormat(
                    context.getString(R.string.date_format),
                    Locale.getDefault()
            ).format(note.lastChanged)
            note_body.text = note.text

            this as CardView
            this.setCardBackgroundColor(ResourcesCompat.getColor(resources, note.color, null));
            this.setOnClickListener {
                onItemClickListener?.onItemClick(note)
            }
        }
    }

    //Not yet implemented
    inner class NoteRVSwipeToDelete(val icon: Drawable) : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
    ) {
        val background = ColorDrawable(Color.RED);

        override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            val itemView = viewHolder.itemView
            val backgroundCornerOffset = 20.toInt()
            val iconMargin = (itemView.getHeight() - icon.intrinsicHeight) / 2;
            val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2;
            val iconBottom = iconTop + icon.intrinsicHeight;

            if (dX < 0) { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(
                        itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom
                )
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0)
            }

            background.draw(c)
            icon.draw(c)
        }

        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.getAdapterPosition();
            //adapter.deleteItem(position);
        }
    }

}