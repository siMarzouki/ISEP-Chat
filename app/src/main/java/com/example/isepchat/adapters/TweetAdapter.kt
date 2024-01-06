package com.example.isepchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isepchat.models.Tweet
import com.example.isepchat.R
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import com.bumptech.glide.Glide
import com.example.isepchat.services.communityService

class TweetAdapter: RecyclerView.Adapter<TweetAdapter.ViewHolder>() {


    var items: MutableList<Tweet> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_tweet, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tweet = items[position]
        holder.bind(tweet)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserImg : ShapeableImageView = itemView.findViewById(R.id.ivUserImg)
        val ivName : TextView = itemView.findViewById(R.id.ivName)
        val ivTweet : TextView = itemView.findViewById(R.id.ivTweet)
        val ivHour : TextView = itemView.findViewById(R.id.ivHour)
        val ivLikeCount : TextView = itemView.findViewById(R.id.ivLikeCount)
        val ivLikeBtn : Button = itemView.findViewById(R.id.ivLikeBtn)
        val ivDeleteBtn : AppCompatImageButton = itemView.findViewById(R.id.ivDeleteBtn)



        fun bind(tweet: Tweet) {
            val user = communityService.users.find{it.uuid==tweet.uid}
           ivName.text =  user!!.fullname

            if(user.image!!.isNotEmpty()) {
                Glide.with(itemView.context).load(user.image).placeholder(R.drawable.avatar1).into(ivUserImg)
            }
            if(communityService.currentUser!!.uid!=tweet.uid){
                ivDeleteBtn.visibility=View.INVISIBLE
            }else{
                ivDeleteBtn.visibility=View.VISIBLE

            }

            ivDeleteBtn.setOnClickListener{
                communityService.deleteTweet(tweet.id);
            }


            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            ivHour.text = sdf.format(Date(tweet.timestamp))
            ivTweet.text= tweet.text
            ivLikeCount.text=tweet.likes.size.toString()+" ❤"

            if (tweet.likes.contains(communityService!!.currentUser!!.uid)) {
                ivLikeBtn.text = "liked"
                ivLikeBtn.setBackgroundColor(itemView.context.resources.getColor(R.color.likedButtonColor))

                ivLikeBtn.setOnClickListener {
                    tweet.likes.remove(communityService!!.currentUser!!.uid)
                    communityService.updateTweet(tweet)
                }
            } else {
                ivLikeBtn.setBackgroundColor(itemView.context.resources.getColor(R.color.notLikedButtonColor))

                ivLikeBtn.text = "like"
                ivLikeBtn.setOnClickListener {
                    tweet.likes.add(communityService!!.currentUser!!.uid)

                    communityService.updateTweet(tweet)
                }
            }

        }

    }
}
