package com.example.myonlyapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var preferencesPseudos: SharedPreferences
    private lateinit var preferencesTokens: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferencesPseudos = getSharedPreferences("Pseudos", Context.MODE_PRIVATE)
        preferencesTokens = getSharedPreferences("Tokens", Context.MODE_PRIVATE)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        usernameEditText = findViewById(R.id.InputPseudo)
        passwordEditText = findViewById(R.id.InputPassword)


        sharedPreferences = getSharedPreferences("Myprefs", MODE_PRIVATE)

        val okButton: Button = findViewById(R.id.buttonOK)
        okButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            identification(username,password)
            startChoixListActivity(username)
        }
    }

    private fun identification(pseudo: String, mdp: String): Boolean {
        val requestQueue = Volley.newRequestQueue(this)
        val url = "http://tomnab.fr/todo-api/authenticate?user=$pseudo&password=$mdp"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, null,
            {response ->
                Log.i("Identification","success")
                var token = response["hash"].toString()

                val editorToken = preferencesTokens.edit()
                editorToken.putString("token", token)
                editorToken.apply()

            },
            {error->
                Log.i("Identification", "error+${error.toString()}" )
            }
        )

        requestQueue.add(jsonObjectRequest)

        return true
    }




    private fun startChoixListActivity(username: String) {
        val intent = Intent(this, ChoixListActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }


}
