package com.example.to_dolist

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.Utils.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Collections

class MainActivity : AppCompatActivity(), DialogCloseListener {

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var tasksAdapter: ToDoAdapter
    private lateinit var inputSearch: EditText
    private lateinit var db: DatabaseHandler
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        db = DatabaseHandler(this)
        db.openDatabase()

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)

        tasksAdapter = ToDoAdapter(db, this)
        tasksRecyclerView.adapter = tasksAdapter

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            AddNewTask.newInstance()
                .show(supportFragmentManager, AddNewTask.TAG)
        }

        loadTasks()

        inputSearch = findViewById(R.id.inputSearch)

        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tasksAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun loadTasks() {
        val taskList = db.getAllTasks().toMutableList()
        Collections.reverse(taskList)
        tasksAdapter.setTasks(taskList)
    }

    override fun handleDialogClose(dialog: DialogInterface) {
        loadTasks()
    }
}