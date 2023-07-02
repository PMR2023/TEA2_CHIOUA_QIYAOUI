package com.example.myonlyapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ShowListActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listData: MutableList<String>
    private var selectedListIndex: Int = -1
    private var username: String = ""
    private var listIndex: Int = 0
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        listView = findViewById(R.id.list_view_items)

        token = intent.getStringExtra("token") ?: ""
        listIndex = intent.getIntExtra("listIndex", 1)

        listData = mutableListOf()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_checked, listData)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val addNewItemButton: Button = findViewById(R.id.button_add_item)
        addNewItemButton.setOnClickListener {
            val newItemEditText: EditText = findViewById(R.id.edit_text_item)
            val newItem = newItemEditText.text.toString().trim()
            if (newItem.isNotBlank()) {

                listData.add(newItem)

                adapter.notifyDataSetChanged()
                newItemEditText.text.clear()
            }
        }

        val intent = intent
        if (intent != null) {
            selectedListIndex = intent.getIntExtra("selectedListIndex", -1)
            username = intent.getStringExtra("username") ?: ""

            if (selectedListIndex != -1 && username.isNotBlank()) {

                adapter.notifyDataSetChanged()
            }
        }
    }

    fun onAddItemClicked(view: View) {
        val newItemEditText: EditText = findViewById(R.id.edit_text_item)
        val newItem = newItemEditText.text.toString().trim()
        if (newItem.isNotBlank()) {

            listData.add(newItem)

            adapter.notifyDataSetChanged()
            newItemEditText.text.clear()
        }
    }



}
