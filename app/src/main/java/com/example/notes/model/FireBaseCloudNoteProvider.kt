package com.example.notes.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*


private const val NOTES_FIRESTORE_COLLECTION = "geo_notes"

class FireBaseCloudNoteProvider : NetworkNoteProvider {

    companion object {
        private val TAG = FireBaseCloudNoteProvider::class.java.simpleName
    }

    private val db = FirebaseFirestore.getInstance()
    private val notesRef = db.collection(NOTES_FIRESTORE_COLLECTION)


    override fun subscribeToNotes(): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesRef.addSnapshotListener { value, error ->
            if (error != null) {
                result.value = NoteResult.Error(error)
                Log.d(TAG, "Notes load failure")
            } else if (value != null) {
                val notes = mutableListOf<Note>()

                for (doc: QueryDocumentSnapshot in value) {
                    notes.add(doc.toObject(Note::class.java))
                }
                Log.d(TAG, "Notes has been successfully loaded")
                result.value = NoteResult.Success(notes)
            }
        }
        return result
    }

    override fun getNoteById(uid: String): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesRef
            .document(uid)
            .get().addOnSuccessListener { docSnapshot ->
                result.value = NoteResult.Success(docSnapshot.toObject(Note::class.java))
                Log.d(TAG, "Note with uid=$uid has been loaded")
            }.addOnFailureListener { exception ->
                result.value = NoteResult.Error(exception)
                Log.d(TAG, "Note with uid=$uid load failure")
            }
        return result
    }

    override fun saveNote(note: Note): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesRef.document(note.uid)
            .set(note)
            .addOnSuccessListener {
                Log.d(TAG, "Note ${note.uid} has been saved")
                result.value = NoteResult.Success(note)
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Note ${note.uid} has been failed with error: ${exception.message}")
                result.value = NoteResult.Error(exception)
            }
        return result
    }

    override fun deleteNoteWithId(uid: String): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        notesRef.document(uid).delete().addOnSuccessListener {
            Log.d(TAG, "Note $uid has been deleted")
            result.value = NoteResult.Success(uid)
        }.addOnFailureListener { exception ->
            Log.d(TAG, "Note $uid delete error")
            result.value = NoteResult.Error(exception)
        }
        return result
    }
}