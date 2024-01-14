package com.example.isepchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.isepchat.R
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
class Gif : AppCompatActivity() {



        private val gifUrls = arrayOf(
            "https://user-images.githubusercontent.com/14011726/94132137-7d4fc100-fe7c-11ea-8512-69f90cb65e48.gif",
            "https://compote.slate.com/images/697b023b-64a5-49a0-8059-27b963453fb1.gif",
                "https://media2.giphy.com/media/3oEjI4sFlp73fvEYgw/giphy.gif",
    "https://user-images.githubusercontent.com/14011726/94132137-7d4fc100-fe7c-11ea-8512-69f90cb65e48.gif",
    "https://compote.slate.com/images/697b023b-64a5-49a0-8059-27b963453fb1.gif",
    "https://media2.giphy.com/media/3oEjI4sFlp73fvEYgw/giphy.gif",
    "https://i0.wp.com/www.printmag.com/wp-content/uploads/2021/02/4cbe8d_f1ed2800a49649848102c68fc5a66e53mv2.gif?fit=476%2C280&ssl=1",
    "https://i.stack.imgur.com/1dpmw.gif",
    "https://media.itsnicethat.com/original_images/giphy-2021-gifs-and-clips-animation-itsnicethat-02.gif",
    "https://ask.libreoffice.org/uploads/asklibo/original/3X/3/5/35664d063435f940bda4cb3bb31ea0a6c5fed2f4.gif",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQptAnD9jT2aBsXWe0OK-ar7n8bHcyI5z3PKA&usqp=CAU",
    "https://media3.giphy.com/media/q49YSnLzrvghiyKBAR/200w.gif?cid=6c09b952uxhklzo6m5uv4yj8hbz41xocbdf9wbovzto77fqv&ep=v1_gifs_search&rid=200w.gif&ct=g",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZ92ICMGdcbonqtK-p7gjG6dJWniWPg1traA&usqp=CAU",
    "https://media0.giphy.com/media/WoWm8YzFQJg5i/giphy.gif",
    "https://www.icegif.com/wp-content/uploads/2022/11/icegif-816.gif",
    "https://media2.giphy.com/media/5pMGZHSqfvGT5mnTwx/200w.gif?cid=6c09b952674qc4y2k1e6whvc6i3fokh572zl356sr5xxf51k&ep=v1_gifs_search&rid=200w.gif&ct=g",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZvfglwonZSd-9hpu4qB3cNaZnKL_1BfV7JXCyADqjtV0jH_Gxy8IHfe9G4c-uSn_SpGg&usqp=CAU",
    "https://media.tenor.com/6mHANk4RN68AAAAM/steve-harvey.gif",
    "https://techcrunch.com/wp-content/uploads/2015/05/fb-animal-gif.gif?w=430&h=230&crop=1",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTsea_U8vAAqwkhTJFkAWKkkkxZgsMcple1HS6qI1sj_7cj1DDpqxY7UJveWDmXnHbbPcg&usqp=CAU",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR7NJb1JDIUAb9QkZdSrNURkkF1MR7JIb8Rxx3ekgz91B4DybiFgFyite5ZqEROd9fdP_8&usqp=CAU",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSji5DCGQaWL1DHUjsL5Pbo_mhZNOwc6H6V96gdqaiXX6tTnvRPLZoCBdqmu_NtzmmVcAM&usqp=CAU",
    "https://media1.giphy.com/media/3oKIPpqF1Jt9E3SR7W/200w.gif",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTv3QYlmwlQ7b2Z0nw-5_SREM4M68rXxq3SkL4eSUFi6LPIQD4xH2-GFfCmci5uPbdzli8&usqp=CAU"
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_gif)

            val gridView: GridView = findViewById(R.id.gridview)
            val adapter = GifAdapter(this, gifUrls)
            gridView.adapter = adapter

            val userUuid = intent.getStringExtra("friend")!!


            gridView.setOnItemClickListener { _, _, position, _ ->
                val selectedUrl = gifUrls[position]
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("GIF", selectedUrl)
                intent.putExtra("friend", userUuid)

                startActivity(intent)

                finish()

            }
        }

        private class GifAdapter(
            private val context: AppCompatActivity,
            private val gifUrls: Array<String>
        ) : ArrayAdapter<String>(context, 0, gifUrls) {

            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val gifUrl = getItem(position)

                val imageView: ImageView = if (convertView == null) {
                    ImageView(context)
                } else {
                    convertView as ImageView
                }

                // Load GIF using Glide
                Glide.with(context)
                    .asGif()
                    .load(gifUrl)
                    .into(imageView)

                return imageView
            }
        }


}