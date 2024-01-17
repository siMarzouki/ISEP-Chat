package com.example.isepchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.isepchat.R
import com.example.isepchat.models.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.Intent
import android.net.Uri
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

        val tvImage: ImageView = itemView.findViewById(R.id.tvImg)
        fun bind(message: Message) {



            if(message.text=="\uD83D\uDC4D"){
                tvMessage.textSize=35f
            }else{
                tvMessage.textSize=16f
            }


            if(message.text.startsWith("###location:") ){
                tvMessage.text="\uD83D\uDCCD Shared Location"
                tvImage.visibility=View.GONE
                val locationInfo = message.text.substring(12)
                val latLng = locationInfo.split("^")

                tvMessage.setOnClickListener {
                    openGoogleMaps(itemView.context, latLng[0], latLng[1])
                }
            }else if(message.text.startsWith("###img:") ){
                tvMessage.text="IMAGE"
                tvImage.visibility=View.VISIBLE
                val imageUrl = message.text.substring(7)
                Glide.with(itemView)
                    .load(imageUrl)
                    .into(tvImage)
            }else{

                tvMessage.text = message.text
                tvImage.visibility=View.GONE

            }
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
    private fun openGoogleMaps(context: Context, latitude: String, longitude: String) {
        val mapUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Handle the case where Google Maps is not installed
            // You can open the maps website in a browser as an alternative
            val mapsUrl = "https://www.google.com/maps?q=$latitude,$longitude"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))
            context.startActivity(browserIntent)
        }
    }
}