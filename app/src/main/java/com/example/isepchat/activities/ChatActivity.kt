package com.example.isepchat.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isepchat.R
import com.example.isepchat.adapters.ChatRecyclerAdapter
import com.example.isepchat.models.Friend
import com.example.isepchat.models.Message
import com.example.isepchat.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ListenerRegistration

class ChatActivity : AppCompatActivity() {
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    lateinit var fabSendMessage: FloatingActionButton
    lateinit var likebtn: FloatingActionButton

    lateinit var editMessage: EditText
    lateinit var rvChatList: RecyclerView
    lateinit var  user2:User

    lateinit var chatRecyclerAdapter: ChatRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser

        fabSendMessage = findViewById(R.id.fabSendMessage)
        likebtn = findViewById(R.id.likeBtn)
        editMessage = findViewById(R.id.editMessage)
        rvChatList = findViewById(R.id.rvChatList)

        val userUuid = intent.getStringExtra("friend")!!

        db.collection("users")
            .document(userUuid)
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    var user = result.toObject(User::class.java)
                    user?.let {
                        user.uuid = userUuid
                        setUserData(user)
                    }
                    user2=user!!
                }
            }.addOnFailureListener {
                Log.e("ChatActivity", "error getting user", it)
            }
    }

    private fun setUserData(user: User) {
        supportActionBar?.title = user.fullname
        chatRecyclerAdapter = ChatRecyclerAdapter()
        val messages = mutableListOf<Message>()
        rvChatList.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatRecyclerAdapter
        }
        fabSendMessage.visibility=View.INVISIBLE


        editMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that characters within `start` and `before` are about to be replaced with new text
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within `start` and `before`, text has been replaced with new text having a length of `count` characters
            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify you that the characters within `Editable` have been changed
                val enteredText = editable.toString()

                // Check if the text is empty
                if (enteredText.isEmpty()) {
                    fabSendMessage.visibility=View.INVISIBLE
                } else {
                    fabSendMessage.visibility=View.VISIBLE
                }
            }
        })



        likebtn.setOnClickListener {
            // envoyer le message
            val message = "\uD83D\uDC4D"
            if (message.isNotEmpty()) {
                val message = Message(
                    sender = currentUser!!.uid,
                    receiver = user.uuid,
                    text = message,
                    timestamp = System.currentTimeMillis(),
                    isReceived = false,
                    vued=false
                )
                editMessage.setText("")
                // hide keyboard
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editMessage.windowToken, 0)

                db.collection("messages").add(message)
                    .addOnSuccessListener { documentReference ->
                        val messageId = documentReference.id
                        rvChatList.scrollToPosition(messages.size - 1)
                        val friend = Friend(
                            "",
                            user.fullname,
                            message.text,
                            timestamp = System.currentTimeMillis(),
                            image = user.image ?: "",
                            seen = true,
                            me=true
                        )

                        db.collection("users")
                            .document(currentUser!!.uid)
                            .collection("friends")
                            .document(user.uuid)
                            .set(friend)
                            .addOnSuccessListener {
                                Log.d("ChatActivity", "friend added")
                            }.addOnFailureListener {
                                Log.e("ChatActivity", "error adding friend", it)
                            }


                        db.collection("users")
                            .document(user.uuid)
                            .collection("friends")
                            .document(currentUser!!.uid)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                // Check if the document exists before accessing its data
                                if (documentSnapshot.exists()) {
                                    val data = documentSnapshot.data
                                    // Assuming 'lastMsg' is a field in your document
                                    data?.set("lastMsg", message.text)
                                    data?.set("timestamp", System.currentTimeMillis())
                                    data?.set("seen",false)
                                    data?.set("me",false)

                                    // Update the document with the modified data
                                    db.collection("users")
                                        .document(user.uuid)
                                        .collection("friends")
                                        .document(currentUser!!.uid)
                                        .set(data!!)
                                        .addOnSuccessListener {
                                            Log.d("ChatActivity", "Friend's lastMsg updated successfully")
                                        }.addOnFailureListener {
                                            Log.e("ChatActivity", "Error updating friend's lastMsg", it)
                                        }
                                } else {
                                    Log.e("ChatActivity", "Document does not exist")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ChatActivity", "Error retrieving friend document", e)
                            }
                    }.addOnFailureListener { exception ->
                        Log.e("ChatActivity", "error adding message", exception)
                    }


            }
        }




        fabSendMessage.setOnClickListener {
            // envoyer le message
            val message = editMessage.text.toString()
            if (message.isNotEmpty()) {
                val message = Message(
                    sender = currentUser!!.uid,
                    receiver = user.uuid,
                    text = message,
                    timestamp = System.currentTimeMillis(),
                    isReceived = false,
                    vued=false
                )
                editMessage.setText("")
                // hide keyboard
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editMessage.windowToken, 0)

                db.collection("messages").add(message)
                    .addOnSuccessListener { documentReference ->
                        val messageId = documentReference.id
                        rvChatList.scrollToPosition(messages.size - 1)
                        val friend = Friend(
                            "",
                            user.fullname,
                            message.text,
                            timestamp = System.currentTimeMillis(),
                            image = user.image ?: "",
                            seen = true,
                            me=true

                        )

                        db.collection("users")
                            .document(currentUser!!.uid)
                            .collection("friends")
                            .document(user.uuid)
                            .set(friend)
                            .addOnSuccessListener {
                                Log.d("ChatActivity", "friend added")
                            }.addOnFailureListener {
                                Log.e("ChatActivity", "error adding friend", it)
                            }


                        db.collection("users")
                            .document(user.uuid)
                            .collection("friends")
                            .document(currentUser!!.uid)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                // Check if the document exists before accessing its data
                                if (documentSnapshot.exists()) {
                                    val data = documentSnapshot.data
                                    // Assuming 'lastMsg' is a field in your document
                                    data?.set("lastMsg", message.text)
                                    data?.set("timestamp", System.currentTimeMillis())
                                    data?.set("seen",false)
                                    data?.set("me",false)
                                    // Update the document with the modified data
                                    db.collection("users")
                                        .document(user.uuid)
                                        .collection("friends")
                                        .document(currentUser!!.uid)
                                        .set(data!!)
                                        .addOnSuccessListener {
                                            Log.d("ChatActivity", "Friend's lastMsg updated successfully")
                                        }.addOnFailureListener {
                                            Log.e("ChatActivity", "Error updating friend's lastMsg", it)
                                        }
                                } else {
                                    Log.e("ChatActivity", "Document does not exist")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ChatActivity", "Error retrieving friend document", e)
                            }
                    }.addOnFailureListener { exception ->
                        Log.e("ChatActivity", "error adding message", exception)
                    }


            }
        }

        val sentQuery = db.collection("messages")
            .whereEqualTo("sender", currentUser!!.uid)
            .whereEqualTo("receiver", user.uuid)
            .orderBy("timestamp", Query.Direction.ASCENDING)



        sentQuery.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("ChatActivity", "error getting messages", exception)
                return@addSnapshotListener
            }

            for (document in snapshot!!.documents) {
                var message = document.toObject(Message::class.java)
                message?.let {
                    Log.d("ChatActivity sent: ", message.toString())
                    message.id = document.id
                    message.isReceived = false

                    val existingMessageIndex = messages.indexOfFirst { it.id == message.id }

                    if (existingMessageIndex != -1) {
                        // Message with the same id already exists, replace it
                        messages[existingMessageIndex] = message
                    } else {
                        // Message with the given id doesn't exist, add it to the list
                        messages.add(message)
                    }
                }
            }
            if (messages.isNotEmpty()) {
                chatRecyclerAdapter.items = messages.sortedBy { it.timestamp } as MutableList<Message>
                rvChatList.scrollToPosition(messages.size - 1)
            }
        }


       val  receivedQuery = db.collection("messages")
            .whereEqualTo("sender", user.uuid)
            .whereEqualTo("receiver", currentUser!!.uid)
            .orderBy("timestamp", Query.Direction.ASCENDING)

        listenerRegistration =   receivedQuery.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("ChatActivity", "error getting messages", exception)
                return@addSnapshotListener
            }
            for (document in snapshot!!.documents) {
                var message = document.toObject(Message::class.java)
                message?.let {
                    Log.d("ChatActivity received: ", message.toString())
                    message.id = document.id
                    message.isReceived = true
                    val existingMessageIndex = messages.indexOfFirst { it.id == message.id }

                    if (existingMessageIndex != -1) {
                        // Message with the same id already exists, replace it
                        messages[existingMessageIndex] = message
                    } else {
                        // Message with the given id doesn't exist, add it to the list
                        messages.add(message)
                    }

                    if(!message.vued){
                            message.vued=true
                            db.collection("messages")
                                .document(message.id)
                                .set(message)
                                .addOnSuccessListener {
                                    Log.d("ChatActivity", "Message marked as vued")
                                }.addOnFailureListener {
                                    Log.e("ChatActivity", "Message cannont bemarked as vued", it)
                                }

                    }
                }
            }
            if (messages.isNotEmpty()) {
                chatRecyclerAdapter.items = messages.sortedBy { it.timestamp } as MutableList<Message>
                rvChatList.scrollToPosition(messages.size - 1)
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove();
        db.collection("users")
            .document(currentUser!!.uid)
            .collection("friends")
            .document(user2.uuid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Check if the document exists before accessing its data
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data
                    data?.set("seen",true)
                    db.collection("users")
                        .document(currentUser!!.uid)
                        .collection("friends")
                        .document(user2.uuid)
                        .set(data!!)
                        .addOnSuccessListener {
                            Log.d("ChatActivity", "See All")
                        }.addOnFailureListener {
                            Log.e("ChatActivity", "Error", it)
                        }
                } else {
                    Log.e("ChatActivity", "Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ChatActivity", "Error retrieving friend document", e)
            }
    }
}