package com.dramirez.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.dramirez.myapplication.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar View Binding
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // Configurar ajustes de UI con WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("chats")

        // Configurar RecyclerView
        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList)
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.adapter = messageAdapter

        // Cargar mensajes desde Firebase
        loadMessages()

        // Enviar mensaje al presionar el botón
        binding.buttonSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                binding.editTextMessage.text.clear()
            }
        }
    }

    private fun loadMessages() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    // Verificar si el mensaje está eliminado
                    if (!it.deleted) {
                        messageList.add(it)
                        messageAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores si es necesario
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    // Actualizar el mensaje en la lista si no está eliminado
                    if (!it.deleted) {
                        val index = messageList.indexOfFirst { m -> m.messageId == it.messageId }
                        if (index >= 0) {
                            messageList[index] = it
                            messageAdapter.notifyItemChanged(index)
                        }
                    } else {
                        // Eliminar el mensaje de la lista si está marcado como eliminado
                        val index = messageList.indexOfFirst { m -> m.messageId == it.messageId }
                        if (index >= 0) {
                            messageList.removeAt(index)
                            messageAdapter.notifyItemRemoved(index)
                        }
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    private fun sendMessage(messageText: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val messageId = database.push().key ?: return
        val message =
            userId?.let { Message(it, messageText, System.currentTimeMillis(), false, messageId) }

        database.child(messageId).setValue(message)
            .addOnSuccessListener {
                // Opcional: Mostrar mensaje de éxito
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

}
