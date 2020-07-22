package com.why_group.tododo

import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.why_group.tododo.databinding.ActivityMainBinding
import com.why_group.tododo.databinding.TodoItemBinding

class MainActivity : AppCompatActivity() {
    /*
    view binding은 findViewById를 대체하기 위해 사용한다.
    binding.root는 binding의 view를 반환한다.
     */
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.todoList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TodoAdapter(
                viewModel.data,
                onClickDelete = {
                    viewModel.deleteTodo(it)
                    binding.todoList.adapter?.notifyDataSetChanged()
                },
                onClickToggle = {
                    viewModel.toggleDone(it)
                    binding.todoList.adapter?.notifyDataSetChanged()
                }
            )
        }

        binding.addButton.setOnClickListener {
            val todo = Todo(binding.enterTodo.text.toString())
            viewModel.addTodo(todo)
            binding.todoList.adapter?.notifyDataSetChanged()
        }
    }
}

class TodoAdapter(
    private val myDataset: List<Todo>,
    val onClickDelete: (todo: Todo) -> Unit,
    val onClickToggle: (todo: Todo) -> Unit
) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(val binding: TodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoAdapter.TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)

        return TodoViewHolder(TodoItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = myDataset[position]
        holder.binding.todoText.text = todo.text

        if (todo.isDone) {
            holder.binding.todoText.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC)
            }
        } else {
            holder.binding.todoText.apply {
                paintFlags = 0
                setTypeface(null, Typeface.NORMAL)
            }
        }

        holder.binding.root.setOnClickListener {
            onClickToggle.invoke(todo)
        }

        holder.binding.delelteButton.setOnClickListener {
            onClickDelete.invoke(todo)
        }
    }

    override fun getItemCount() = myDataset.size
}
