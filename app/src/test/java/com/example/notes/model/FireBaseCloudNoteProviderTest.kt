package com.example.notes.model

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.notes.model.auth.NoAuthException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.*
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class FireBaseCloudNoteProviderTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockDb = mockk<FirebaseFirestore>()
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockCollection = mockk<CollectionReference>()
    private val mockUser = mockk<FirebaseUser>()
    private val mockQueryDocument1 = mockk<QueryDocumentSnapshot>()
    private val mockQueryDocument2 = mockk<QueryDocumentSnapshot>()
    private val mockQueryDocument3 = mockk<QueryDocumentSnapshot>()


    private val testNotes = listOf(Note(uid = "1"), Note(uid = "2"), Note(uid = "3"))

    private val provider = FireBaseCloudNoteProvider(mockDb, mockAuth)

    @Before
    fun setUp() {
        clearMocks(mockCollection, mockQueryDocument1, mockQueryDocument2, mockQueryDocument3)

        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0

        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns ""
        every {
            mockDb.collection(any()).document(any()).collection(any())
        } returns mockCollection
        every { mockQueryDocument1.toObject(Note::class.java) } returns testNotes[0]
        every { mockQueryDocument2.toObject(Note::class.java) } returns testNotes[1]
        every { mockQueryDocument3.toObject(Note::class.java) } returns testNotes[2]

    }

    @Test
    fun `should throw if no auth`() {
        var result: Any? = null

        every { mockAuth.currentUser } returns null
        provider.subscribeToNotes().observeForever {
            result = (it as? NoteResult.Error)?.error
        }
        assertTrue(result is NoAuthException)
    }

    @Test
    fun `subscribeToNotes should return notes`() {
        var result: List<Note>? = null
        val slot = slot<EventListener<QuerySnapshot>>()
        val mockSnapshot = mockk<QuerySnapshot>()
        val mockSnapshotIterator = mockk<MutableListIterator<QueryDocumentSnapshot>>()

        every { mockSnapshot.documents } returns listOf(
            mockQueryDocument1, mockQueryDocument2, mockQueryDocument3
        )

        every { mockSnapshot.iterator() } returns mutableListOf<QueryDocumentSnapshot>(
            mockQueryDocument1, mockQueryDocument2, mockQueryDocument3
        ).iterator()

        every { mockCollection.addSnapshotListener(capture(slot)) } returns mockk()

        provider.subscribeToNotes().observeForever {
            result = (it as? NoteResult.Success<List<Note>>)?.data
        }

        slot.captured.onEvent(mockSnapshot, null)
        assertEquals(testNotes, result)
    }

    @Test
    fun `subscribeToNotes should return error`() {
        var result: Throwable? = null
        val slot = slot<EventListener<QuerySnapshot>>()
        val testError = mockk<FirebaseFirestoreException>()

        every { mockCollection.addSnapshotListener(capture(slot)) } returns mockk()

        provider.subscribeToNotes().observeForever {
            result = (it as? NoteResult.Error)?.error
        }

        slot.captured.onEvent(null, testError)
        assertNotNull(result)
        assertEquals(testError, result)

    }

    @Test
    fun `saveNote calls document set`() {
        val mockDocumentReference: DocumentReference = mockk()

        every { mockCollection.document(testNotes[0].uid) } returns mockDocumentReference
        provider.saveNote(testNotes[0])

        verify(exactly = 1) { mockDocumentReference.set(testNotes[0]) }
    }

    @Test
    fun `saveNote return Note`() {
        val mockDocumentReference: DocumentReference = mockk()
        val slot = slot<OnSuccessListener<in Void>>()
        var result: Note? = null

        every { mockCollection.document(testNotes[0].uid) } returns mockDocumentReference
        every {
            mockDocumentReference.set(testNotes[0]).addOnSuccessListener(capture(slot))
        } returns mockk()

        provider.saveNote(testNotes[0]).observeForever {
            result = (it as? NoteResult.Success<Note>)?.data
        }

        slot.captured.onSuccess(null)

        assertNotNull(result)
        assertEquals(testNotes[0], result)
    }

    @After
    fun tearDown() {
    }
}
