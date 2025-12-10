package com.example.to_dolist;

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.R
import android.widget.CheckBox
import android.widget.CompoundButton
import com.example.to_dolist.Model.ToDoModel
import com.example.to_dolist.MainActivity
import com.example.to_dolist.Utils.DatabaseHandler

class ToDoAdapter(private val db: DatabaseHandler, private val activity: MainActivity) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    private var todoList: MutableList<ToDoModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db.openDatabase()

        val item = todoList[position]
        holder.task.text = item.task
        holder.task.isChecked = toBoolean(item.status)

        holder.task.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                db.updateStatus(item.id, 1)
            } else {
                db.updateStatus(item.id, 0)
            }
        }
    }

    override fun getItemCount(): Int = todoList.size

    private fun toBoolean(n: Int): Boolean = n != 0

    fun setTasks(tasks: List<ToDoModel>) {
        this.todoList = tasks.toMutableList()
        notifyDataSetChanged()
    }



    fun deleteItem(position: Int) {
        val item = todoList[position]
        db.deleteTask(item.id)
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val item = todoList[position]
        val bundle = Bundle().apply {
            putInt("id", item.id)
            putString("task", item.task)
        }

        val fragment = AddNewTask()
        fragment.arguments = bundle
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

    fun getContext(): Context = activity

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val task: CheckBox = view.findViewById(R.id.todoCheckBox)
    }
}
