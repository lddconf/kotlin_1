package com.example.notes.ui.activities

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.rule.ActivityTestRule
import com.example.notes.R
import com.example.notes.model.NetworkNoteProvider
import com.example.notes.model.Note
import com.example.notes.model.NoteResult
import com.example.notes.model.NotesRepo
import com.example.notes.ui.adapters.NotesRVAdapter
import com.example.notes.ui.viewmodel.MainViewModel
import com.example.notes.ui.viewmodel.NoteViewModel
import com.example.notes.ui.viewmodel.SplashViewModel
import io.mockk.*
import org.junit.*

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class MainActivityTest {
    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository = mockk<NotesRepo>()
    private lateinit var viewModel: MainViewModel
    private val viewStateLiveData = MutableLiveData<MainViewState>()

    private val testNotes = listOf<Note>(
        Note(uid = "1", title = "TestNoteTitle1", text = "TestNoteText1"),
        Note(uid = "2", title = "TestNoteTitle2", text = "TestNoteText2"),
        Note(uid = "3", title = "TestNoteTitle3", text = "TestNoteText3")
    )

    @Before
    fun setUp() {
        clearAllMocks()
        startKoin {
            modules()
        }

        every { mockRepository.getNotes() } returns MutableLiveData<NoteResult>()
        every { mockRepository.getNoteById(any()) } returns MutableLiveData<NoteResult>()

        viewModel = spyk(MainViewModel(mockRepository))

        loadKoinModules(
            module {
                viewModel { viewModel }
                viewModel { NoteViewModel(mockRepository) }
                viewModel { SplashViewModel(mockRepository) }
            }
        )

        every { viewModel.viewState() } returns viewStateLiveData
        every { viewModel.deleteNote(any<String>()) } just runs
        every { viewModel.undoLastDeletedNote() } just runs

        activityTestRule.launchActivity(Intent())
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun on_open_note_view() {
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.title_editor_text)).check(matches(isDisplayed()))
    }

    @Test
    fun on_logout_dialog_show() {
        onView(withId(R.id.action_logout)).perform(click())
        onView(withText(R.string.logout_dialog_message)).check(matches(isDisplayed()))
    }

    @Test
    fun on_logout() {
        onView(withId(R.id.action_logout)).perform(click())
        onView(withText(R.string.logout_dlg_ok_btn_title)).perform(click())
        onView(withId(R.id.splash_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun on_delete_note_by_swipe() {
        UiThreadStatement.runOnUiThread(
            Runnable {
                viewStateLiveData.postValue(MainViewState(testNotes, null))
            }
        )
        onView(withId(R.id.notes_list)).perform(
            actionOnItemAtPosition<NotesRVAdapter.NoteRVHolder>(
                0, swipeLeft()
            )
        )
        verify(timeout = 1000, exactly = 1) { viewModel.deleteNote(any()) }
    }

    @Test
    fun on_restore_deleted_note() {
        UiThreadStatement.runOnUiThread(
            Runnable {
                viewStateLiveData.postValue(MainViewState(testNotes, null))
            }
        )
        onView(withId(R.id.notes_list)).perform(
            actionOnItemAtPosition<NotesRVAdapter.NoteRVHolder>(
                0, swipeLeft()
            )
        )
        onView(withText(R.string.snckbar_recover_btn)).perform(click())
        verify(timeout = 1000, exactly = 1) { viewModel.undoLastDeletedNote() }
    }

    @Test
    fun on_confirm_note_deletion() {
        UiThreadStatement.runOnUiThread(
            Runnable {
                viewStateLiveData.postValue(MainViewState(testNotes, null))
            }
        )
        onView(withId(R.id.notes_list)).perform(
            actionOnItemAtPosition<NotesRVAdapter.NoteRVHolder>(
                0, swipeLeft()
            )
        )
        verify(timeout = 5000, exactly = 0) { viewModel.undoLastDeletedNote() }
    }

    @Test
    fun on_note_click() {
        UiThreadStatement.runOnUiThread(
            Runnable {
                viewStateLiveData.postValue(MainViewState(testNotes, null))
            }
        )
        onView(withId(R.id.notes_list)).perform(
            actionOnItemAtPosition<NotesRVAdapter.NoteRVHolder>(
                0,
                click()
            )
        )
        onView(withId(R.id.title_editor_text)).check(matches(isDisplayed()))
    }
}