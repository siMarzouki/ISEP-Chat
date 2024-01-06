package com.example.isepchat.models

data class Message(
    val sender: String,
    val receiver: String,
    val text: String,
    val timestamp: Long,
    var id:String="",
    var isReceived: Boolean = true,
    var vued:Boolean=false,
    var liked:Boolean=false

    ) {

    constructor(): this("", "","",0)

}
