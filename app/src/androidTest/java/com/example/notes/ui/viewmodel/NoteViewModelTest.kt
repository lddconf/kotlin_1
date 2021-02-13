package com.example.notes.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.example.notes.R
import com.example.notes.model.Note
import com.example.notes.model.NotesRepo
import com.example.notes.ui.activities.NoteViewActivity
import com.example.notes.ui.activities.NoteViewState
import io.mockk.*
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module


class NoteViewModelTest {
    @get:Rule
    val activityTestRule = ActivityTestRule(NoteViewActivity::class.java, true, false)

    private val viewModel: NoteViewModel = spyk(NoteViewModel(mockk<NotesRepo>()))
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private val testNote = Note(uid = "123", title = "TestNoteTitle", text = "TestNoteText")

    @Before
    fun setUp() {
        startKoin {
            modules()
        }

        loadKoinModules(
            module {
                viewModel { viewModel }
            }
        )

        every { viewModel.viewState() } returns viewStateLiveData
        every { viewModel.loadNote(any<String>()) } just runs
        every { viewModel.saveChanges(any<Note>()) } just runs

        activityTestRule.launchActivity(Intent().apply {
            putExtra("NoteViewActivity.extra.NOTE", testNote.uid)
        })

    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun should_shown_color_picker() {
        onView(withId(R.id.action_color_pick)).perform(click())
        onView(withId(R.id.color_picker_palette)).check(matches(isDisplayed()))
        onView(withId(R.id.color_selection_dialog_close_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun should_close_color_picker() {
        onView(withId(R.id.action_color_pick)).perform(click())
        onView(withId(R.id.color_selection_dialog_close_btn)).perform(click())

        onView(withId(R.id.color_picker_palette)).check(doesNotExist())
        onView(withId(R.id.color_selection_dialog_close_btn)).check(doesNotExist())
    }
}