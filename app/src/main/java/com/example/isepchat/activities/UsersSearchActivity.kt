package com.example.isepchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isepchat.R
import com.example.isepchat.adapters.UsersRecyclerAdapter
import com.example.isepchat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UsersSearchActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    lateinit var rvUsers: RecyclerView
    lateinit var editSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_search)

        auth = Firebase.auth
        currentUser = auth.currentUser

        rvUsers = findViewById(R.id.rvUsers)
        editSearch = findViewById(R.id.editSearch)

        val usersRecyclerAdapter = UsersRecyclerAdapter()

        rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UsersSearchActivity)
            adapter = usersRecyclerAdapter
        }

        val users = mutableListOf<User>()
        db = Firebase.firestore
        db.collection("users")
            .whereNotEqualTo("email", currentUser?.email)
            .get()
            .addOnSuccessListener { result ->
            for(document in result) {
                val uuid = document.id
                val email = document.getString("email")
                val fullName = document.getString("fullname")
                users.add(User(uuid, email ?: "", fullName ?: "", null))
            }
            usersRecyclerAdapter.items = users
        }.addOnFailureListener { exception ->
            Log.e("UsersSearchActivity", "error getting users", exception)
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                usersRecyclerAdapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }
}