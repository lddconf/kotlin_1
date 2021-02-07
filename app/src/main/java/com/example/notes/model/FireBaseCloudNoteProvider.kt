package com.example.notes.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notes.model.auth.NoAuthException
import com.example.notes.model.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


private const val NOTES_FIRESTORE_COLLECTION = "geo_notes"
private const val USERS_FIRESTORE_COLLECTION = "user_notes"

class FireBaseCloudNoteProvider : NetworkNoteProvider {

    companion object {
        private val TAG = FireBaseCloudNoteProvider::class.java.simpleName
    }

    private val db = FirebaseFirestore.getInstance()
    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser

    override fun subscribeToNotes(): LiveData<NoteResult> = MutableLiveData<NoteResult>().apply {
        try {
            getUserNotesCollection().addSnapshotListener { snapshot, error ->
                this.value = error?.let {
                    Log.d(TAG, "Notes load failure")
                    NoteResult.Error(error)
                } ?: snapshot?.let { querySnapshots ->
                    val notes = querySnapshots.map { doc ->
                        doc.toObject(Note::class.java)
                    }
                    Log.d(TAG, "Notes has been successfully loaded")
                    NoteResult.Success(notes)
                }
            }
        } catch (e: Throwable) {
            value = NoteResult.Error(e)
        }
    }

    override fun getNoteById(uid: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(uid)
                    .get().addOnSuccessListener { docSnapshot ->
                        this.value = NoteResult.Success(docSnapshot.toObject(Note::class.java))
                        Log.d(TAG, "Note with uid=$uid has been loaded")
                    }.addOnFailureListener { exception ->
                        this.value = NoteResult.Error(exception)
                        Log.d(TAG, "Note with uid=$uid load failure")
                    }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }


    override fun saveNote(note: Note): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(note.uid)
                    .set(note)
                    .addOnSuccessListener {
                        Log.d(TAG, "Note ${note.uid} has been saved")
                        this.value = NoteResult.Success(note)
                    }.addOnFailureListener { exception ->
                        Log.d(
                            TAG,
                            "Note ${note.uid} has been failed with error: ${exception.message}"
                        )
                        this.value = NoteResult.Error(exception)
                    }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }

    override fun removeNote(uid: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(uid).delete().addOnSuccessListener {
                    Log.d(TAG, "Note $uid has been deleted")
                    this.value = NoteResult.Success(uid)
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "Note $uid delete error")
                    this.value = NoteResult.Error(exception)
                }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }

    private fun getUserNotesCollection() = currentUser?.let { firebaseUser ->
        db.collection(USERS_FIRESTORE_COLLECTION)
            .document(firebaseUser.uid)
            .collection(NOTES_FIRESTORE_COLLECTION)
    } ?: throw NoAuthException()


    override fun getCurrentUser(): LiveData<User?> =
        MutableLiveData<User?>().apply {
            value = currentUser?.let { firebaseUser ->
                User(firebaseUser.displayName ?: "", firebaseUser.email ?: "")
            }
        }
}