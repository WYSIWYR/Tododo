package com.why_group.tododo

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val data = arrayListOf<Todo>()

    fun addTodo(todo: Todo) {
        data.add(todo)
    }

    fun toggleDone(todo: Todo) {
        todo.isDone = !todo.isDone
    }

    fun deleteTodo(todo: Todo) {
        data.remove(todo)
    }
}