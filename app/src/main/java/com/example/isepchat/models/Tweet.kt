package com.example.isepchat.models

data class Tweet(
    var uid: String,
    var text: String,
    var timestamp: Long,
    var id:String="",
    var likes:  MutableList<String> =  mutableListOf<String>()
)
    { constructor(): this("", "",0)
    }

