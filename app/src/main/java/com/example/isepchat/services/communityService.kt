package com.example.isepchat.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.isepchat.models.Message
import com.example.isepchat.models.Tweet
import com.example.isepchat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class communityService {
    val db = FirebaseFirestore.getInstance()

    companion object {
        val users = mutableListOf<User>()
        val db = FirebaseFirestore.getInstance()

        lateinit var auth: FirebaseAuth
        var currentUser: FirebaseUser? = null

        fun getUsers(){
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val uuid = document.id
                        val email = document.getString("email")
                        val fullName = document.getString("fullname")
                        val image =  document.getString("image")
                        users.add(User(uuid, email ?: "", fullName ?: "", image))

                    }
                }
        }

        fun addTweet(tweet: Tweet) {
            db.collection("posts")
                .add(tweet)
                .addOnSuccessListener { documentReference ->
                    // Tweet added successfully
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }

        fun updateTweet(tweet: Tweet) {
            db.collection("posts")
                .document(tweet.id)
                .set(tweet)
                .addOnSuccessListener { documentReference ->
                    // Tweet updated successfully
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }

         fun deleteTweet(tweetId: String) {
            db.collection("posts")
                .document(tweetId)
                .delete()
                .addOnSuccessListener {

                }
                .addOnFailureListener { e ->
                }
        }

    }







    // Retrieve tweets
    fun getTweets(callback: (List<Tweet>) -> Unit) {
        auth = Firebase.auth
        currentUser = auth.currentUser
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)

            .addSnapshotListener {  result, exception ->
                if (exception != null) {
                    Log.e("ChatActivity", "error getting messages", exception)
                    return@addSnapshotListener
                }
                val tweets = result!!.documents.map { document ->
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.copy(id = document.id) // Include document ID
                }.filterNotNull()
                callback(tweets)
            }

    }


}