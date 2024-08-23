package com.dramirez.myapplication

data class Message(
    val userId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val deleted: Boolean = false, // Asegúrate de tener este campo
    val messageId: String = "", // Asegúrate de tener este campo
)