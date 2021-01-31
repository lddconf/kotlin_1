package com.example.notes.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.R
import com.example.notes.model.Note
import com.example.notes.toRGBColor
import com.example.notes.ui.viewmodel.BaseViewModel
import com.example.notes.ui.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_note_view.*
import kotlinx.android.synthetic.main.note_preview_layout.*
import java.text.SimpleDateFormat
import java.util.*

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
    override val viewModel: NoteViewModel by lazy {
        ViewModelProvider(this).get(NoteViewModel::class.java)
    }
    override val layoutResourceId: Int = R.layout.activity_note_view


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
        uid?.let {
            viewModel.loadNote(uid)
        }
        title_editor_text.addTextChangedListener(onTextChangedListener)
        body_editor_text.addTextChangedListener(onTextChangedListener)
    }


    private fun initView() {
        supportActionBar?.title = getString(R.string.new_note_title)
        note?.apply {
            supportActionBar?.title =
                SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()).format(
                    lastChanged
                )
            supportActionBar?.setBackgroundDrawable(ColorDrawable(color.toRGBColor()))
            title_editor_text?.setText(title)
            body_editor_text?.setText(text)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(note_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() = title_editor_text.text?.let {
        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(
                title = title_editor_text.text.toString(),
                text = body_editor_text.text.toString(),
                lastChanged = Date()
            ) ?: Note (
                title = title_editor_text.text.toString(),
                text = body_editor_text.text.toString(),
                lastChanged = Date() )

            note?.let {
                viewModel.saveChanges(it)
            }
        }, SAVE_DELAY_MS)
    }

    override fun renderData(data: Note?) {
        note = data
        initView()
    }

    override fun renderError(error: Throwable) {
        TODO("Not yet implemented")
    }
}