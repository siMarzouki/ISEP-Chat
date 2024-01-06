package com.example.isepchat.activities.ui.home
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isepchat.R
import com.example.isepchat.activities.AuthentificationActivity
import com.example.isepchat.activities.SettingsActivity
import com.example.isepchat.activities.UsersSearchActivity
import com.example.isepchat.adapters.FriendsRecyclerAdapter
import com.example.isepchat.models.Friend
import com.example.isepchat.models.Message
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    private lateinit var rvFriends: RecyclerView
    private lateinit var fabChat: FloatingActionButton

    private lateinit var friendsRecyclerAdapter: FriendsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser!!

        rvFriends = view.findViewById(R.id.rvFriends)
        fabChat = view.findViewById(R.id.fabChat)

        fabChat.setOnClickListener {
            Intent(activity, UsersSearchActivity::class.java).also {
                startActivity(it)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val friends = mutableListOf<Friend>()
        friendsRecyclerAdapter = FriendsRecyclerAdapter()

        rvFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendsRecyclerAdapter
        }

        val bolles =      db.collection("users")
            .document(currentUser.uid)
            .collection("friends")

        bolles.addSnapshotListener{ snapshot, exception ->
            if (exception != null) {
                Log.e("ChatActivity", "error getting messages", exception)
                return@addSnapshotListener
            }
            friends.clear()
            for (document in snapshot!!.documents) {
                val friend = document.toObject(Friend::class.java)
                friend!!.uuid = document.id
                friends.add(friend)
            }
            friendsRecyclerAdapter.items = friends
        }

    }



}