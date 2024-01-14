package com.example.isepchat.activities.ui.dashboard

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.isepchat.R
import com.example.isepchat.activities.UsersSearchActivity
import com.example.isepchat.adapters.FriendsRecyclerAdapter
import com.example.isepchat.adapters.TweetAdapter
import com.example.isepchat.databinding.FragmentDashboardBinding
import com.example.isepchat.models.Friend
import com.example.isepchat.models.Tweet
import com.example.isepchat.services.communityService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashboardFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    private lateinit var rvFriends: RecyclerView
    private lateinit var fabChat: FloatingActionButton

    private lateinit var tweetsRecyclerAdapter: TweetAdapter
    private val community :communityService= communityService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser!!

        rvFriends = view.findViewById(R.id.rvFriends)
        fabChat = view.findViewById(R.id.fabTwitter)

        fabChat.setOnClickListener {
            // Create a custom layout for the dialog
            val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null)

            // Find views within the custom layout
            val textView: EditText = dialogView.findViewById(R.id.dialogEditText)
            val btnPost: Button = dialogView.findViewById(R.id.btnPost)
            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)

            // Set text for the TextView

            // Create an AlertDialog and set the custom layout
            val alertDialogBuilder = AlertDialog.Builder(activity)
                .setView(dialogView)
                .setCancelable(false) // Set to true if you want to make it dismissible by tapping outside

            // Create the dialog
            val alertDialog = alertDialogBuilder.create()

            // Set click listeners for the buttons
            btnPost.setOnClickListener {
                val tweet= Tweet()
                tweet.uid=communityService.currentUser!!.uid
                tweet.text=textView.text.toString()
                tweet.timestamp=System.currentTimeMillis()
                communityService.addTweet(tweet)
                alertDialog.dismiss()
            }

            btnCancel.setOnClickListener {
                // Handle cancel button click
                alertDialog.dismiss()
            }

            // Show the dialog
            alertDialog.show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val tweets = mutableListOf<Tweet>()
        tweetsRecyclerAdapter = TweetAdapter()

        rvFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tweetsRecyclerAdapter
        }

        community.getTweets {tweets->
            val mutableTweets = tweets.toMutableList()
            tweetsRecyclerAdapter.items = mutableTweets

            tweets.forEach {
                val mutableTweets = tweets.toMutableList()
                val currentTimeMillis = System.currentTimeMillis()
                val messageTimeMillis = it.timestamp

                val timeDifferenceMillis = currentTimeMillis - messageTimeMillis
                val isTimeDifferenceLessThanOneMinute =
                    timeDifferenceMillis <  5* 1000

                if (isTimeDifferenceLessThanOneMinute && getContext()!=null && it.uid!=auth.uid) {
                    val notificationId = timeDifferenceMillis.toInt()
                    val notificationBuilder = NotificationCompat.Builder(requireContext(), "isepchat")
                        .setSmallIcon(R.drawable.bavarder)
                        .setContentTitle("New Post")
                        .setContentText(it.text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)



                    val notificationManager =
                        requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(notificationId, notificationBuilder.build())

                }
            }

        }
    }

}