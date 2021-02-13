package com.example.notes.ui.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.example.notes.R
import com.example.notes.model.Note
import com.example.notes.model.NotesRepo
import com.example.notes.toColor
import com.example.notes.ui.customviews.CircleColorPicker
import com.example.notes.ui.viewmodel.NoteViewModel
import io.mockk.*
import org.junit.*
import org.junit.Assert.assertTrue

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module


class NoteViewTest {
    @get:Rule
    val activityTestRule = ActivityTestRule(NoteViewActivity::class.java, true, false)

    private val notesRepo = mockk<NotesRepo>()
    private val viewModel: NoteViewModel = spyk(NoteViewModel(notesRepo))
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private val testNote = Note(uid = "123", title = "TestNoteTitle", text = "TestNoteText")

    @Before
    fun setUp() {
        startKoin {
            modules()
        }

        clearMocks( viewModel )

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

    @Test
    fun should_color_has_been_setup() {
        val coloredNote = testNote.copy(color = Note.PredefinedColor.YELLOW)
        viewStateLiveData.postValue(NoteViewState(note = coloredNote))


        onView(withId(R.id.action_color_pick)).perform(click())

        //Check color picker
        onView(withId(R.id.color_picker_palette)).check { view, _ ->
            assertTrue(
                "Should be like colored note",
                (view as CircleColorPicker).selectedColor == coloredNote.color.toColor(activityTestRule.activity)
            )
        }
        onView(withId(R.id.color_selection_dialog_close_btn)).perform(click())

        //Check toolbar color
        onView(withId(R.id.note_toolbar)).check { view, _ ->
            assertTrue(
                "Should be like colored note",
                (view.background as? ColorDrawable)?.color == coloredNote.color.toColor(activityTestRule.activity)
            )
        }
    }

    @Test
    fun should_call_viewModel_loadNote() {
        verify(exactly = 1) {
            viewModel.loadNote(testNote.uid)
        }
    }

    @Test
    fun should_show_note() {
        activityTestRule.launchActivity(null)
        viewStateLiveData.postValue(NoteViewState(note = testNote))

        onView(withId(R.id.title_editor_text)).check(matches(withText(testNote.title)))
        onView(withId(R.id.body_editor_text)).check(matches(withText(testNote.text)))
    }

    @Test
    fun should_call_saveNote() {
        onView(withId(R.id.title_editor_text)).perform(typeText(testNote.title))
        verify(timeout = 1000) { viewModel.saveChanges(any<Note>()) }
    }
}