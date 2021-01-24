package com.example.notes.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.R
import com.example.notes.ui.adapters.NotesRVAdapter
import com.example.notes.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel : MainViewModel
    private lateinit var adapter: NotesRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAppBar()
        initNotesRV()
        initMainViewModel()

    }

    private fun initAppBar() {
        main_toolbar.title = getString(R.string.app_name)
    }

    private fun initNotesRV() {
        notes_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NotesRVAdapter()
        notes_list.adapter = adapter
    }


    private fun initMainViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.viewState().observe(this, { value->
            value?.let { adapter.notes = it.notes }
        })
    }
}