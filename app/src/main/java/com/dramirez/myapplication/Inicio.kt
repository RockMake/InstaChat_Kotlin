package com.dramirez.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Inicio : AppCompatActivity() {
    private lateinit var Btn_ir_a_login: Button
    private lateinit var Btn_ir_a_registro: Button
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Btn_ir_a_registro = findViewById(R.id.ir_registro)
        Btn_ir_a_login = findViewById(R.id.ir_login)


        Btn_ir_a_registro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            Toast.makeText(this, "Ir a registro", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        Btn_ir_a_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            Toast.makeText(this, "Ir a login", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    fun validarSesion() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val intent = Intent(this@Inicio, MainActivity::class.java)
            Toast.makeText(applicationContext,"Sesion Activa", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }
    override fun onStart() {
        validarSesion()
        super.onStart()
    }
}