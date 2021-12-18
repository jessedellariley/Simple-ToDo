package com.example.simpletodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

const val REQUEST_CODE: Int = 20 // for determining result type in editing activity

class MainActivity : AppCompatActivity() {
    var listOfTasks = mutableListOf<String>()
    lateinit var adapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onLongClickListener = object : TaskItemAdapter.OnLongClickListener {
            override fun onItemLongClicked(position: Int) {
                // 1. Remove the item from the list
                listOfTasks.removeAt(position)
                // 2. Notify the adapter that our data set has changed
                adapter.notifyDataSetChanged()

                saveItems()
            }
        }

        val onClickListener = object : TaskItemAdapter.OnClickListener {
            override fun onItemClicked(position: Int) {
                // Go to edit activity
                onClick(position)
            }
        }

        loadItems()

        // Look up recyclerView in layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Create adapter passing in the sample user data
        adapter = TaskItemAdapter(listOfTasks, onLongClickListener, onClickListener)
        // Attach the adapter to the recyclerview to populate items
        recyclerView.adapter = adapter
        // Set layout manager to position the items
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the button and input field, so that the user can enter a task and add it to the list
        val inputTextField = findViewById<EditText>(R.id.addTaskField)

        // Set a reference to the button
        // And then set an onClickListener
        findViewById<Button>(R.id.button).setOnClickListener {
            // 1. Grab the text the user has inputted into @id/addTaskField
            val userInputtedTask = inputTextField.text.toString()

            // 2. Add the string to our list of tasks: listOfTasks
            listOfTasks.add(userInputtedTask)

            // Notify the adapter that our data has been updated
            adapter.notifyItemInserted(listOfTasks.size - 1)

            // 3. Reset text field
            inputTextField.setText("")

            saveItems()
        }
    }

    // Save the data that the user has inputted
    // Save data by writing and reading from a file

    // Get the file we need
    fun getDataFile() : File {

        // Every line is going to represent a specific task in our list of tasks
        return File(filesDir, "data.txt")
    }

    // Load the items by reading every line in the data file
    fun loadItems() {
        try {
            listOfTasks = FileUtils.readLines(getDataFile(), Charset.defaultCharset())
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    // Save items by writing them into our data file
    fun saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), listOfTasks)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    // Launch edit activity using Intent
    fun onClick(position: Int) {
        // first parameter is the context, second is the class of the activity to launch
        val i = Intent(this@MainActivity, EditActivity::class.java)
        // put "extras" into the bundle for access in the edit activity
        i.putExtra("todo", listOfTasks[position])
        i.putExtra("position", position)
        startActivityForResult(i,REQUEST_CODE) // brings up the edit activity
    }

    // Handle the result of the editing activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract updated task and position values from result extras
            val updatedTask = data?.extras?.getString("todo")
            val position = data?.extras?.getInt("position")
            // Update item in list of tasks
            if (updatedTask != null && updatedTask == "") {
                // Remove the item from the list
                listOfTasks.removeAt(position!!)
            } else if (updatedTask != null) {
                // Edit the task in list of tasks
                listOfTasks[position!!] = updatedTask
            }
            // Notify the adapter that our data set has changed
            adapter.notifyDataSetChanged()

            saveItems()
        }
    }
}