package com.dramirez.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var L_email: EditText
    private lateinit var L_contrasena: EditText
    private lateinit var L_btn_login: Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        Inicializar()
        // supportActionBar!!.title = "Login"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        L_btn_login.setOnClickListener {
            ValidarDatos()
        }
    }

    private fun Inicializar() {
        L_email = findViewById(R.id.L_email)
        L_contrasena = findViewById(R.id.L_contrasena)
        L_btn_login = findViewById(R.id.Btn_iniciar_sesion)
        auth = FirebaseAuth.getInstance()
    }
    private fun ValidarDatos() {
        val mail : String = L_email.text.toString()
        val contrasena : String = L_contrasena.text.toString()
        if (mail.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(mail, contrasena).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    Toast.makeText(applicationContext, "Sesion Iniciada", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}