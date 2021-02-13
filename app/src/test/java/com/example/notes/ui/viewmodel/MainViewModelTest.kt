package com.example.notes.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.notes.model.Note
import com.example.notes.model.NoteResult
import com.example.notes.model.NotesRepo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.results.PrintableResult

class MainViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository = mockk<NotesRepo>()
    private val noteLiveData = MutableLiveData<NoteResult>()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        every {
            mockRepository.getNotes()
        } returns noteLiveData
        viewModel = MainViewModel(mockRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `should call getNotes once`() {
        verify(exactly = 1) {
            mockRepository.getNotes()
        }
    }

    @Test
    fun `should return error`() {
        var result: Throwable? = null
        var testData = Throwable("error")

        viewModel.viewState().observeForever { result = it?.error }
        noteLiveData.value = NoteResult.Error(testData)

        assertEquals(result, testData)
    }

    @Test
    fun `should return Notes`() {
        var result: List<Note>? = null
        var testData = listOf(Note(uid = "1"), Note(uid = "2"))

        viewModel.viewState().observeForever {
            result = it?.data
        }
        noteLiveData.value = NoteResult.Success(testData)

        assertEquals(result, testData)
    }


    @Test
    fun `should remove observer`() {
        viewModel.onCleared()
        assertFalse(noteLiveData.hasObservers())
    }
}