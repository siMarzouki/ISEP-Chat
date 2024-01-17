package com.example.isepchat.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

import android.content.pm.PackageManager
import android.location.Location

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class ChatActivity : AppCompatActivity() {
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    lateinit var fabSendMessage: FloatingActionButton
    lateinit var likebtn: FloatingActionButton
    lateinit var imageButton: ImageButton

    lateinit var editMessage: EditText
    lateinit var rvChatList: RecyclerView
    lateinit var  user2:User

    lateinit var chatRecyclerAdapter: ChatRecyclerAdapter
    private val IMAGE_PICK_REQUEST_CODE = 123

    var latitude=0.0
    var longitude=0.0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 1

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Use the location object
                     latitude = location.latitude
                     longitude = location.longitude
//                    Toast.makeText(
//                        this,
//                        "Latitude: $latitude, Longitude: $longitude",
//                        Toast.LENGTH_SHORT
//                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Unable to retrieve location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkLocationPermission()) {
            getLastLocation()
        } else {
            requestLocationPermission()
        }


        setContentView(R.layout.activity_chat)
        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser

        fabSendMessage = findViewById(R.id.fabSendMessage)
        likebtn = findViewById(R.id.likeBtn)
        editMessage = findViewById(R.id.editMessage)
        rvChatList = findViewById(R.id.rvChatList)
        imageButton = findViewById(R.id.imageButton)


        val gifLink = intent.getStringExtra("GIF")



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


                    if(gifLink!=null){

                        val message = "###img:"+gifLink
                        if (message.isNotEmpty()) {
                            val message = Message(
                                sender = currentUser!!.uid,
                                receiver = user2.uuid,
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
                                    val friend = Friend(
                                        "",
                                        user2.fullname,
                                        message.text,
                                        timestamp = System.currentTimeMillis(),
                                        image = user2.image ?: "",
                                        seen = true,
                                        me=true

                                    )

                                    db.collection("users")
                                        .document(currentUser!!.uid)
                                        .collection("friends")
                                        .document(user2.uuid)
                                        .set(friend)
                                        .addOnSuccessListener {
                                            Log.d("ChatActivity", "friend added")
                                        }.addOnFailureListener {
                                            Log.e("ChatActivity", "error adding friend", it)
                                        }


                                    db.collection("users")
                                        .document(user2.uuid)
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
                                                    .document(user2.uuid)
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
        fun openImagePicker(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }
        fun openGifPicker(userUuid:String){
            val intent = Intent(this, Gif::class.java)
            intent.putExtra("friend", userUuid)
            startActivity(intent)
            finish();
        }

        fun sendLocation(){
getLastLocation();

                val message = "###location:"+latitude+"^"+longitude
                if (message.isNotEmpty()) {
                    val message = Message(
                        sender = currentUser!!.uid,
                        receiver = user2.uuid,
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
                            val friend = Friend(
                                "",
                                user2.fullname,
                                message.text,
                                timestamp = System.currentTimeMillis(),
                                image = user2.image ?: "",
                                seen = true,
                                me=true

                            )

                            db.collection("users")
                                .document(currentUser!!.uid)
                                .collection("friends")
                                .document(user2.uuid)
                                .set(friend)
                                .addOnSuccessListener {
                                    Log.d("ChatActivity", "friend added")
                                }.addOnFailureListener {
                                    Log.e("ChatActivity", "error adding friend", it)
                                }


                            db.collection("users")
                                .document(user2.uuid)
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
                                            .document(user2.uuid)
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


                imageButton.setOnClickListener{
            val options = arrayOf("Send Image", "Send Gif", "Send Location")
            AlertDialog.Builder(this)
                .setTitle("Choose Attachment Type")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> openImagePicker()
                        1 -> openGifPicker(user.uuid)
                        2 -> sendLocation()
                    }
                }
                .show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                val storageRef = Firebase.storage.reference
                val imageRef = storageRef.child("chat_pics/${auth.uid}-${System.currentTimeMillis()}")

                // Load the image from URI into Bitmap
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageData = baos.toByteArray()

                // Upload the byte array to Firebase storage
                val uploadTask = imageRef.putBytes(imageData)
                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val message = "###img:$uri"
                        if (message.isNotEmpty()) {
                            val message = Message(
                                sender = currentUser!!.uid,
                                receiver = user2.uuid,
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
                                    val friend = Friend(
                                        "",
                                        user2.fullname,
                                        message.text,
                                        timestamp = System.currentTimeMillis(),
                                        image = user2.image ?: "",
                                        seen = true,
                                        me=true

                                    )

                                    db.collection("users")
                                        .document(currentUser!!.uid)
                                        .collection("friends")
                                        .document(user2.uuid)
                                        .set(friend)
                                        .addOnSuccessListener {
                                            Log.d("ChatActivity", "friend added")
                                        }.addOnFailureListener {
                                            Log.e("ChatActivity", "error adding friend", it)
                                        }


                                    db.collection("users")
                                        .document(user2.uuid)
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
                                                    .document(user2.uuid)
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
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
//                Toast.makeText(
//                    this,
//                    "Permission denied. Cannot get location.",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }
    }



}