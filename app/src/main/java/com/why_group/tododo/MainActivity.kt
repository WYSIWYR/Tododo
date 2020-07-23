package com.why_group.tododo

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.why_group.tododo.databinding.ActivityMainBinding
import com.why_group.tododo.databinding.TodoItemBinding

class MainActivity : AppCompatActivity() {
    /*
    view binding은 findViewById를 대체하기 위해 사용한다.
    binding.root는 binding의 view를 반환한다.
     */
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val RC_SIGN_IN = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (FirebaseAuth.getInstance().currentUser == null) {
            signIn()
        }

        binding.todoList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TodoAdapter(
                emptyList(),
                onClickDelete = {
                    viewModel.deleteTodo(it)
                },
                onClickToggle = {
                    viewModel.toggleDone(it)
                }
            )
        }

        binding.addButton.setOnClickListener {
            val todo = Todo(binding.enterTodo.text.toString())
            viewModel.addTodo(todo)
        }

        viewModel.todoLiveData.observe(this@MainActivity, Observer {
            (binding.todoList.adapter as TodoAdapter).setLiveData(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                viewModel.loadData()
            } else {
                finish()
            }
        }
    }

    fun signIn() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                signIn()
            }
    }
}

class TodoAdapter(
    private var myDataset: List<Todo>,
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

    fun setLiveData(newData: List<Todo>) {
        myDataset = newData
        notifyDataSetChanged()
    }
}
