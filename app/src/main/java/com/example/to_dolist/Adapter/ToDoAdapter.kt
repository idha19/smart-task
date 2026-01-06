package com.example.to_dolist

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.Model.ToDoModel
import com.example.to_dolist.Utils.DatabaseHandler

class ToDoAdapter(
    private val db: DatabaseHandler,
    private val activity: MainActivity
) : RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    private var todoList: MutableList<ToDoModel> = mutableListOf()
    private var todoListFull: MutableList<ToDoModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = todoList[holder.position]

        // set text
        holder.task.text = item.task

        // reset listener (WAJIB biar ga bug)
        holder.task.setOnCheckedChangeListener(null)

        // status -> checkbox
        holder.task.isChecked = item.status == 1
        setStrikeThrough(holder.task, item.status == 1)

        // checkbox click
        holder.task.setOnCheckedChangeListener { _, isChecked ->
            item.status = if (isChecked) 1 else 0
            db.updateStatus(item.id, item.status)
            setStrikeThrough(holder.task, isChecked)
        }

        // EDIT
        holder.btnEdit.setOnClickListener {
            editItem(holder.adapterPosition)
        }

        // HAPUS
        holder.btnHapus.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = todoList.size

    fun setTasks(tasks: List<ToDoModel>) {
        todoList = tasks.toMutableList()
        todoListFull = tasks.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val lowerQuery = query.lowercase()

        todoList = if (query.isEmpty()) {
            todoListFull.toMutableList()
        } else {
            todoListFull.filter {
                it.task.lowercase().contains(lowerQuery)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }


    private fun deleteItem(position: Int) {
        val item = todoList[position]
        db.deleteTask(item.id)
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun editItem(position: Int) {
        val item = todoList[position]
        val bundle = Bundle().apply {
            putInt("id", item.id)
            putString("task", item.task)
        }

        val fragment = AddNewTask()
        fragment.arguments = bundle
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

    private fun setStrikeThrough(checkBox: CheckBox, isDone: Boolean) {
        checkBox.paintFlags = if (isDone) {
            checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val task: CheckBox = view.findViewById(R.id.todoCheckBox)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
        val btnHapus: ImageView = view.findViewById(R.id.btnHapus)
    }
}