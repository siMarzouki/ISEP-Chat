package com.example.isepchat.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.isepchat.R
import com.example.isepchat.activities.ChatActivity
import com.example.isepchat.models.Friend
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Typeface

class FriendsRecyclerAdapter : RecyclerView.Adapter<FriendsRecyclerAdapter.ViewHolder>() {

    var items: MutableList<Friend> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = items[position]
        holder.bind(friend)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivFriend : ShapeableImageView = itemView.findViewById(R.id.ivUserImg)
        val tvName : TextView = itemView.findViewById(R.id.ivName)
        val tvLastMsg : TextView = itemView.findViewById(R.id.ivTweet)
        val tvHour : TextView = itemView.findViewById(R.id.ivHour)
        val seenView : View = itemView.findViewById(R.id.pointnoir)


        fun bind(friend: Friend) {
            if(friend.me){
                if( friend.lastMsg.startsWith("###img:")){
                    tvLastMsg.text="Image Sent"
                }else if ( friend.lastMsg.startsWith("###location:")) {
                    tvLastMsg.text="Location Sent"

                }  else{
                    tvLastMsg.text ="you: "+ friend.lastMsg
                }

            }else{

                if( friend.lastMsg.startsWith("###img:")){
                    tvLastMsg.text="Image Received"
                }else if ( friend.lastMsg.startsWith("###location:")) {
                    tvLastMsg.text="Location Received"

                } else{
                    tvLastMsg.text = friend.lastMsg
                }

            }

            tvName.text = friend.name

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvHour.text = sdf.format(Date(friend.timestamp))

            if(!friend.seen){
                tvLastMsg.typeface= Typeface.DEFAULT_BOLD
                tvLastMsg.textSize = 20f
                seenView.visibility = View.VISIBLE

            }else{
                tvLastMsg.typeface= Typeface.DEFAULT
                tvLastMsg.textSize = 18f
                seenView.visibility=View.GONE;
            }
            if(friend.image.isNotEmpty()) {
                Glide.with(itemView.context).load(friend.image).placeholder(R.drawable.avatar1).into(ivFriend)
            }

            itemView.setOnClickListener {
                Intent(itemView.context, ChatActivity::class.java).also {
                    it.putExtra("friend", friend.uuid)
                    itemView.context.startActivity(it)
                }
            }
        }

    }
}






