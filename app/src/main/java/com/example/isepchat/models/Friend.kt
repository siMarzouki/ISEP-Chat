package com.example.isepchat.models

data class Friend(
    var uuid: String,
    val name: String,
    var lastMsg: String,
    val image: String,
    val timestamp: Long,
    var seen:Boolean=false,
    var me:Boolean=false
    ) {
    constructor(): this("", "", "", "", 0,)
}
