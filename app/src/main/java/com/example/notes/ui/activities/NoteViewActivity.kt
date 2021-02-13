package com.example.notes.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.notes.*
import com.example.notes.databinding.ActivityNoteViewBinding
import com.example.notes.model.Note
import com.example.notes.ui.dialogs.ColorSelectionDialog
import com.example.notes.ui.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import com.thebluealliance.spectrum.SpectrumDialog
import java.text.SimpleDateFormat
import java.util.*
import org.koin.android.viewmodel.ext.android.viewModel

class NoteViewActivity : BaseActivity<Note?, NoteViewState>() {
    companion object {
        const val EXTRA_NOTE = "NoteViewActivity.extra.NOTE"
        const val SAVE_DELAY_MS = 3.toLong()
        fun getStartIntent(context: Context, uid: String?): Intent {
            val intent = Intent(context, NoteViewActivity::class.java)
            intent.putExtra(EXTRA_NOTE, uid)
            return intent
        }
    }

    private var note: Note? = null

    override val viewModel: NoteViewModel by viewModel()

    override val layoutResourceId: Int = R.layout.activity_note_view

    override val ui: ActivityNoteViewBinding by lazy {
        ActivityNoteViewBinding.inflate(layoutInflater)
    }

    private val onTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            saveNote()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupActionBar()
        val uid = intent.getStringExtra(EXTRA_NOTE)
        uid?.let { id ->
            viewModel.loadNote(id)
        }
        ui.titleEditorText.addTextChangedListener(onTextChangedListener)
        ui.bodyEditorText.addTextChangedListener(onTextChangedListener)
    }

    private fun initView() {
        note?.let { note ->
            supportActionBar?.apply {
                title =
                    SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()).format(
                        note.lastChanged
                    )
                setBackgroundDrawable(
                    ColorDrawable(
                        ContextCompat.getColor(applicationContext, note.color.toColorResId())
                    )
                )
            }
            ui.titleEditorText.setText(note.title)
            ui.bodyEditorText.setText(note.text)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(ui.noteToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.new_note_title)
            setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(applicationContext, Note().color.toColorResId())
                )
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        R.id.action_color_pick -> showColorPickerDialog().let { true }
        else -> super.onOptionsItemSelected(item)
    }


    private fun showColorPickerDialog() {
        val colorDialog = ColorSelectionDialog()
        colorDialog.onColorSelectedListener = { color ->
            applyNoteColor(color)
        }
        colorDialog.selectedColor =
            note?.let { it.color?.toColor(this) } ?: Note().color.toColor(this)
        colorDialog.show(supportFragmentManager, getString(R.string.color_selection_title))
    }

    fun applyNoteColor(color: Int?) {
        if (note == null) {
            note = Note()
        }
        note?.let { note ->
            color?.let { color ->
                val newColor = colorToPredefinedColor(applicationContext, color, Note().color)
                note.color = newColor
                initView()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        menuInflater.inflate(R.menu.note_editor_menu, menu).let { true }


    private fun saveNote() = ui.titleEditorText.text?.let {
        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(
                title = ui.titleEditorText.text.toString(),
                text = ui.bodyEditorText.text.toString(),
                lastChanged = Date()
            ) ?: Note(
                title = ui.titleEditorText.text.toString(),
                text = ui.bodyEditorText.text.toString(),
                lastChanged = Date(),
            )
            note?.let { note ->
                viewModel.saveChanges(note)
            }
        }, SAVE_DELAY_MS)
    }

    override fun renderData(data: Note?) {
        note = data
        initView()
    }
}