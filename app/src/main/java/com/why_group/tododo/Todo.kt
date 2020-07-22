package com.why_group.tododo

// data class는 getter, setter가 자동으로 생성되고 toString 재정의를 안해도 된다.
data class Todo(val text: String, var isDone: Boolean = false)