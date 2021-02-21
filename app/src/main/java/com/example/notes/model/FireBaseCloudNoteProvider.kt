package com.example.notes.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notes.model.auth.NoAuthException
import com.example.notes.model.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


private const val NOTES_FIRESTORE_COLLECTION = "geo_notes"
private const val USERS_FIRESTORE_COLLECTION = "user_notes"

class FireBaseCloudNoteProvider(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : NetworkNoteProvider {

    companion object {
        private val TAG = FireBaseCloudNoteProvider::class.java.simpleName
    }

    private val currentUser
        get() = firebaseAuth.currentUser

    override suspend fun subscribeToNotes(): ReceiveChannel<NoteResult> =
        Channel<NoteResult>(Channel.CONFLATED).apply {
            var registration: ListenerRegistration? = null
            try {
                registration = getUserNotesCollection().addSnapshotListener { snapshot, error ->
                    val value = error?.let {
                        Log.d(TAG, "Notes load failure")
                        NoteResult.Error(error)
                    } ?: snapshot?.let { querySnapshots ->
                        val notes = querySnapshots.map { doc ->
                            doc.toObject(Note::class.java)
                        }
                        Log.d(TAG, "Notes has been successfully loaded")
                        NoteResult.Success(notes)
                    }
                    value?.let { offer(it) }
                }
            } catch (e: Throwable) {
                offer(NoteResult.Error(e))
            }

            invokeOnClose { registration?.remove() }
        }

    override suspend fun getNoteById(uid: String): Note = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(uid)
                .get().addOnSuccessListener { docSnapshot ->
                    Log.d(TAG, "Note with uid=$uid has been loaded")
                    continuation.resume(docSnapshot.toObject(Note::class.java)!!)
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "Note with uid=$uid load failure")
                    continuation.resumeWithException(exception)
                }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    override suspend fun saveNote(note: Note): Note = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(note.uid)
                .set(note)
                .addOnSuccessListener {
                    Log.d(TAG, "Note ${note.uid} has been saved")
                    continuation.resume(note)
                }.addOnFailureListener { exception ->
                    Log.d(
                        TAG,
                        "Note ${note.uid} has been failed with error: ${exception.message}"
                    )
                    continuation.resumeWithException(exception)
                }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    override suspend fun removeNote(uid: String): Note? = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(uid).delete().addOnSuccessListener {
                Log.d(TAG, "Note $uid has been deleted")
                continuation.resume(null)
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Note $uid delete error")
                continuation.resumeWithException(exception)
            }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    private fun getUserNotesCollection() = currentUser?.let { firebaseUser ->
        db.collection(USERS_FIRESTORE_COLLECTION)
            .document(firebaseUser.uid)
            .collection(NOTES_FIRESTORE_COLLECTION)
    } ?: throw NoAuthException()


    override suspend fun getCurrentUser(): User? = suspendCoroutine { continuation ->
        currentUser?.let { firebaseUser ->
            continuation.resume(User(firebaseUser.displayName ?: "", firebaseUser.email ?: ""))
        } ?: continuation.resumeWithException(NoAuthException())
    }
}