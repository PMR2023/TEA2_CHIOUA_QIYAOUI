package com.example.myonlyapplication

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume

class ChoixListActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var username: String
    private lateinit var pseudo: String
    private lateinit var token: String
    private val idLists: MutableList<Int> = mutableListOf()


    private val SHOW_LIST_REQUEST_CODE = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        pseudo = intent.getStringExtra("pseudo") ?: ""

        sharedPreferences = getSharedPreferences("Tokens", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", "").toString()


        username = intent.getStringExtra("username") ?: ""


        listView = findViewById(R.id.list_view)
        sharedPreferences = getSharedPreferences("Myprefs", MODE_PRIVATE)


        val listData = getListData(username)


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter


        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                launchShowListActivity(idLists[position])
            }
    }

    private fun getListData(username: String): List<String> {
        val listData = mutableListOf<String>()
        val gson = Gson()

        val json = sharedPreferences.getString(username, null)
        json?.let {
            val savedLists = gson.fromJson<List<String>>(
                it,
                object : TypeToken<List<String>>() {}.type
            )
            listData.addAll(savedLists)
        }

        return listData
    }

    fun onAddListClicked(view: View) {
        val editTextListName = findViewById<EditText>(R.id.edit_text_list_name)
        val listName = editTextListName.text.toString()

        if (listName.isNotBlank()) {
            val listData = getListData(username).toMutableList()
            listData.add(listName)

            val gson = Gson()

            val json = gson.toJson(listData)

            val editor = sharedPreferences.edit()
            editor.putString(username, json)
            editor.apply()

            val adapter = listView.adapter as ArrayAdapter<String>
            adapter.add(listName)
            adapter.notifyDataSetChanged()

            editTextListName.text.clear()
        }
    }

    // on utilise cette fonction pour lancer l'activité ShowListActivity avec l'indice de la liste sélectionnée
    private fun launchShowListActivity(selectedListIndex: Int) {
        val intent = Intent(this, ShowListActivity::class.java)
        intent.putExtra("selectedListIndex", selectedListIndex)
        intent.putExtra("username", username)
        startActivityForResult(intent, SHOW_LIST_REQUEST_CODE)
    }

    // une méthode appelée lorsque l'activité ShowListActivity se termine
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Vérifier le code de requête et du code de résultat
        if (requestCode == SHOW_LIST_REQUEST_CODE && resultCode == RESULT_OK) {
            val updatedListData = data?.getStringArrayExtra("listData")
            updatedListData?.let {
                // Mise à jour de l'adapter de la ListView avec les nouvelles données de la liste
                val adapter = listView.adapter as ArrayAdapter<String>
                adapter.clear()
                adapter.addAll(updatedListData.toList())
                adapter.notifyDataSetChanged()
            }
        }
    }


    private suspend fun getLists(): List<String> {
        val requestQueue = Volley.newRequestQueue(this)
        val url = "http://tomnab.fr/todo-api/lists?hash=$token"

        return suspendCoroutine { continuation ->
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    val jsonObject = JSONObject(response.toString())
                    val listsArray = jsonObject.getJSONArray("lists")

                    val namesList = mutableListOf<String>()
                    for (i in 0 until listsArray.length()) {
                        val listObject = listsArray.getJSONObject(i)
                        val name = listObject.getString("name")
                        val id = listObject.getString("id").toInt()
                        namesList.add(name)
                        idLists.add(id)
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, namesList)
                    listView.adapter = adapter
                    continuation.resume(namesList)
                },
                { error ->
                    continuation.resume(emptyList())
                }
            )

            requestQueue.add(jsonObjectRequest)
        }


    }

}
