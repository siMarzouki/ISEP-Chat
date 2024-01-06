package com.example.isepchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isepchat.R
import com.example.isepchat.models.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ChatRecyclerAdapter: RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>() {
    private lateinit var db: FirebaseFirestore


    var items: MutableList<Message> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        db = Firebase.firestore
        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position].isReceived) {
            true -> R.layout.item_chat_left
            false -> R.layout.item_chat_right
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = items[position]
        holder.bind(message)
    }


    inner class ViewHolder(itemView: View):  RecyclerView.ViewHolder(itemView){

        val tvMessage: TextView = itemView.findViewById(R.id.tvMsg)
        val tvHour: TextView = itemView.findViewById(R.id.ivHour)


        fun bind(message: Message) {

            if(message.text=="\uD83D\uDC4D"){
                tvMessage.textSize=35f
            }else{
                tvMessage.textSize=16f
            }

            tvMessage.text = message.text
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())


            var hour = sdf.format(Date(message.timestamp))
            if(message.vued){
                hour+=" ✔✔"
            }

            if(message.liked){
                hour="❤️ "+hour
            }



            tvHour.text=hour

            if(message.isReceived){
                tvMessage.setOnLongClickListener {
                    message.liked=! message.liked
                    db.collection("messages")
                        .document(message.id)
                        .set(message).addOnSuccessListener { true }
                        .addOnFailureListener{false}
                        false
                }
            }

        }

    }
}