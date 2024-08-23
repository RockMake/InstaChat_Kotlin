package com.dramirez.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dramirez.myapplication.databinding.ItemMessageReceivedBinding
import com.dramirez.myapplication.databinding.ItemMessageSentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MessageAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MESSAGE_TYPE_SENT = 1
    private val MESSAGE_TYPE_RECEIVED = 2

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("chats")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MESSAGE_TYPE_SENT) {
            val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.userId == FirebaseAuth.getInstance().currentUser?.uid) {
            MESSAGE_TYPE_SENT
        } else {
            MESSAGE_TYPE_RECEIVED
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SentMessageViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textViewMessageSent.text = if (message.deleted) "Mensaje eliminado" else message.text

            binding.buttonDeleteSent.setOnClickListener {
                deleteMessage(message.messageId)
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textViewMessageReceived.text = if (message.deleted) "Mensaje eliminado" else message.text

            binding.buttonDeleteReceived.setOnClickListener {
                deleteMessage(message.messageId)
            }
        }
    }

    private fun deleteMessage(messageId: String) {
        if (messageId.isNotEmpty()) {
            database.child(messageId).child("deleted").setValue(true)
                .addOnSuccessListener {
                    // Opcional: Mostrar mensaje de éxito
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        }
    }
}
