package com.why_group.tododo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    val db = Firebase.firestore
    val todoLiveData = MutableLiveData<List<Todo>>()
    private val data = arrayListOf<Todo>()

    init {
        loadData()
    }

    fun loadData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.apply {
            db.collection(this.uid).get()
                .addOnSuccessListener { result ->
                    data.clear()
                    for (document in result) {
                        val todo = Todo(
                            document.data["text"] as String,
                            document.data["isDone"] as Boolean
                        )
                        data.add(todo)
                    }

                    todoLiveData.value = data
                }
                .addOnFailureListener {
                    Log.d("Error", "Error: $it")
                }
        }
    }

    fun addTodo(todo: Todo) {
        data.add(todo)
        todoLiveData.value = data
    }

    fun toggleDone(todo: Todo) {
        todo.isDone = !todo.isDone
        todoLiveData.value = data
    }

    fun deleteTodo(todo: Todo) {
        data.remove(todo)
        todoLiveData.value = data
    }
}