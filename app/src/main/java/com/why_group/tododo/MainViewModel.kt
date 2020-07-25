package com.why_group.tododo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    val db = Firebase.firestore
    val todoLiveData = MutableLiveData<List<DocumentSnapshot>>()

    init {
        loadData()
    }

    fun loadData() {
        FirebaseAuth.getInstance().currentUser?.let {
            db.collection(it.uid).addSnapshotListener { value, e ->
                if (e != null) {
                    Log.d("Error", "Error $e")
                    return@addSnapshotListener
                }

                value?.let {
                    todoLiveData.value = it.documents
                }
            }
        }
    }

    fun addTodo(todo: Todo) {
        FirebaseAuth.getInstance().currentUser?.let {
            db.collection(it.uid).add(todo)
        }
    }

    fun toggleDone(todo: DocumentSnapshot) {
        FirebaseAuth.getInstance().currentUser?.let {
            val isDone = todo.getBoolean("isDone") ?: false
            db.collection(it.uid).document(todo.id).update("isDone", !isDone)
        }
    }

    fun deleteTodo(todo: DocumentSnapshot) {
        FirebaseAuth.getInstance().currentUser?.let {
            db.collection(it.uid).document(todo.id).delete()
        }
    }
}