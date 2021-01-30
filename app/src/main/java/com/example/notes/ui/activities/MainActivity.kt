package com.example.notes.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.R
import com.example.notes.model.Note
import com.example.notes.ui.adapters.NotesRVAdapter
import com.example.notes.ui.adapters.OnItemClickListener
import com.example.notes.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NotesRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAppBar()
        initNotesRV()
        initMainViewModel()
    }

    private fun initAppBar() {
        setSupportActionBar(main_toolbar)
        supportActionBar?.title = getString(R.string.app_name)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.action_add -> {
            startNoteEditor(Note("", ""))
            true
        }
        else -> {
            super.onOptionsItemSelected(item);
            false
        }
    }

    private fun initNotesRV() {
        notes_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NotesRVAdapter()
        notes_list.adapter = adapter
        val swipeDelegate =
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_delete_forever_24)?.let {
                    adapter.NoteRVSwipeToDelete(
                            it
                    )
                }
        adapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                startNoteEditor(note)
            }
        }
    }

    private fun initMainViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.viewState().observe(this, { value ->
            value?.let { adapter.notes = it.notes }
        })
    }

    private fun startNoteEditor(note: Note) {
        val intent = NoteViewActivity.getStartIntent(applicationContext, note)
        startActivity(intent)
    }
}