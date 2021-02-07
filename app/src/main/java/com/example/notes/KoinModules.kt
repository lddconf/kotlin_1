package com.example.notes

import com.example.notes.model.FireBaseCloudNoteProvider
import com.example.notes.model.NetworkNoteProvider
import com.example.notes.model.NotesRepo
import com.example.notes.ui.viewmodel.MainViewModel
import com.example.notes.ui.viewmodel.NoteViewModel
import com.example.notes.ui.viewmodel.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FireBaseCloudNoteProvider(get(), get()) } bind NetworkNoteProvider::class
    single { NotesRepo(get()) }

    viewModel { SplashViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { NoteViewModel(get()) }
}