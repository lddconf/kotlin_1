package com.example.notes.ui.adapters

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.databinding.NotePreviewLayoutBinding
import com.example.notes.model.Note
import com.example.notes.toColorResId
import java.text.SimpleDateFormat
import java.util.*

interface OnItemActionListener {
    fun onItemClick(note: Note)
    fun onItemDelete(note: Note)
}

class NotesRVAdapter(
    var onItemActionListener: OnItemActionListener? = null
) : RecyclerView.Adapter<NotesRVAdapter.NoteRVHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteRVHolder {
        return NoteRVHolder(
            NotePreviewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteRVHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteRVHolder(val ui: NotePreviewLayoutBinding) : RecyclerView.ViewHolder(ui.root) {
        fun bind(note: Note) = with(ui) {
            noteTitle.text = note.title
            noteDate.text = SimpleDateFormat(
                ui.root.context.getString(R.string.date_format),
                Locale.getDefault()
            ).format(note.lastChanged)
            noteBody.text = note.text

            root.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    root.resources,
                    note.color.toColorResId(),
                    null
                )
            );
            root.setOnClickListener {
                onItemActionListener?.onItemClick(note)
            }
        }
    }

    inner class NoteRVSwipeToDelete(
        private val adapter: NotesRVAdapter,
        private val icon: Drawable
    ) : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT
    ) {
        private val background = ColorDrawable(Color.RED);

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
            val backgroundCornerOffset = 20
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
            adapter.onItemActionListener?.apply {
                notifyItemRemoved(position)
                onItemDelete(notes[position])
            }
        }
    }
}