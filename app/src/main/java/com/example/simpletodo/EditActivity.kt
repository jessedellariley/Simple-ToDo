package com.example.simpletodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val inputTextField = findViewById<EditText>(R.id.editTaskField)

        // Set the text in the update field to be the current to do value
        inputTextField.setText(intent.getStringExtra("todo"))

        // Set a reference to the edit button
        // And then set an onClickListener
        findViewById<Button>(R.id.editButton).setOnClickListener {
            // Grab the text the user has inputted into @id/editTaskField
            val updatedTask = inputTextField.text.toString()

            // Return to main screen
            onSubmit(updatedTask,intent.getIntExtra("position",0))
        }
    }

    fun onSubmit(updatedTask: String, position: Int) {
        // Prepare data Intent
        val data = Intent()
        // Pass relevant data back as a result
        data.putExtra("todo", updatedTask)
        data.putExtra("position",position)
        // Activity finished okay, return the data
        setResult(RESULT_OK, data) // set result code and bundle data for response
        // closes the activity and returns to first screen
        finish()
    }
}