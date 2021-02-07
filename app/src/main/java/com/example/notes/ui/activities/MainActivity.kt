package com.example.notes.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.R
import com.example.notes.model.Note
import com.example.notes.ui.adapters.NotesRVAdapter
import com.example.notes.ui.adapters.OnItemActionListener
import com.example.notes.ui.dialogs.LogoutDialog
import com.example.notes.ui.viewmodel.MainViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<List<Note>?, MainViewState>(), LogoutDialog.LogoutListener {
    companion object {
        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override val layoutResourceId: Int = R.layout.activity_main

    private lateinit var adapter: NotesRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppBar()
        initNotesRV()
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
            startNoteEditor()
            true
        }
        R.id.action_logout -> {
            showLogoutDialog()
            true
        }
        else -> {
            super.onOptionsItemSelected(item);
            false
        }
    }

    private fun showLogoutDialog() {
        supportFragmentManager.findFragmentByTag(LogoutDialog.TAG) ?: LogoutDialog.createInstance()
            .show(supportFragmentManager, LogoutDialog.TAG)
    }

    override fun onLogout() {
        AuthUI.getInstance().signOut(this)
            .addOnCompleteListener() {
                startActivity(SplashActivity.getStartIntent(this))
                finish()
            }
    }

    private fun initNotesRV() {
        notes_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NotesRVAdapter()
        notes_list.adapter = adapter
        val swipeItemTouchHelper =
            ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_delete_forever_24)
                ?.let { icon ->
                    ItemTouchHelper(
                        adapter.NoteRVSwipeToDelete(
                            adapter, icon
                        )
                    )
                }
        swipeItemTouchHelper?.attachToRecyclerView(notes_list)
        adapter.onItemActionListener = object : OnItemActionListener {
            override fun onItemClick(note: Note) {
                startNoteEditor(note)
            }

            override fun onItemDelete(note: Note) {
                viewModel.removeNoteWithId(note.uid)
            }
        }
    }

    private fun startNoteEditor(note: Note? = null) {
        startActivity(NoteViewActivity.getStartIntent(applicationContext, note?.uid))
    }

    override fun renderData(data: List<Note>?) {
        data?.apply {
            adapter.notes = this
        }
    }

    override fun renderError(error: Throwable) {
        Snackbar.make(
            findViewById(R.id.appbar_layout),
            error.message ?: "",
            Snackbar.LENGTH_LONG
        ).show()
    }
}