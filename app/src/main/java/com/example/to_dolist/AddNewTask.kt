package com.example.to_dolist;

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.to_dolist.Model.ToDoModel
import com.example.to_dolist.Utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddNewTask : BottomSheetDialogFragment() {

    private lateinit var newTaskText: EditText
    private lateinit var newTaskSaveButton: Button
    private lateinit var db: DatabaseHandler

    companion object {
        const val TAG = "ActionBottomDialog"

        fun newInstance(): AddNewTask {
            return AddNewTask()
        }

        fun newInstance(taskId: Int, taskText: String): AddNewTask {
            val fragment = AddNewTask()
            val bundle = Bundle().apply {
                putInt("id", taskId)
                putString("task", taskText)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newTaskText = view.findViewById(R.id.newTaskText)
        newTaskSaveButton = view.findViewById(R.id.newTaskButton)

        db = DatabaseHandler(requireContext())
        db.openDatabase()

        val bundle = arguments
        val isUpdate = bundle != null

        if (isUpdate) {
            val task = bundle?.getString("task")
            newTaskText.setText(task)
            if (!task.isNullOrEmpty()) {
                newTaskSaveButton.isEnabled = true
                newTaskSaveButton.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.blue)
                )
                newTaskText.setSelection(task.length)
            }
        } else {
            newTaskSaveButton.isEnabled = false
            newTaskSaveButton.setTextColor(Color.GRAY)
        }

        // TextWatcher untuk enable/disable tombol sesuai input
        newTaskText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    newTaskSaveButton.isEnabled = false
                    newTaskSaveButton.setTextColor(Color.GRAY)
                } else {
                    newTaskSaveButton.isEnabled = true
                    newTaskSaveButton.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.blue)
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Tombol simpan
        newTaskSaveButton.setOnClickListener {
            val text = newTaskText.text.toString()
            if (isUpdate) {
                val id = bundle?.getInt("id") ?: 0
                db.updateTask(id, text)
            } else {
                val task = ToDoModel().apply {
                    this.task = text
                    this.status = 0
                }
                db.insertTask(task)
            }
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity: Activity? = activity
        if (activity is DialogCloseListener) {
            activity.handleDialogClose(dialog)
        }
    }
}
