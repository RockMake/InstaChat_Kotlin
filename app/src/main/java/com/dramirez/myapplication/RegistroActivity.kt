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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


 class RegistroActivity : AppCompatActivity() {
    private lateinit var R_nombre_usuario : EditText
    private lateinit var R_correo : EditText
    private lateinit var R_contrasena : EditText
    private lateinit var R_confirmar_contrasena : EditText
    private lateinit var Btn_registrarse : Button

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        // supportActionBar!!.title = "Registro"

        Inicializar()

        Btn_registrarse.setOnClickListener {
            ValidarDatos()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


     private fun Inicializar() {
         R_nombre_usuario = findViewById(R.id.R_nombre_usuario)
         R_correo = findViewById(R.id.R_correo)
         R_contrasena = findViewById(R.id.R_contrasena)
         R_confirmar_contrasena = findViewById(R.id.R_confirmar_contrasena)
         Btn_registrarse = findViewById(R.id.Btn_registrarse)

         auth = FirebaseAuth.getInstance()


     }
     private fun ValidarDatos() {
            val nombre_usuario = R_nombre_usuario.text.toString()
            val correo = R_correo.text.toString()
            val contrasena = R_contrasena.text.toString()
            val confirmar_contrasena = R_confirmar_contrasena.text.toString()

            if (nombre_usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmar_contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                if (contrasena == confirmar_contrasena) {
                    RegistrarUsuario(correo, contrasena)
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            }

     }

     private fun RegistrarUsuario(correo: String, contrasena: String) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser!!.uid
                reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)
                Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()
                val hashmap = HashMap<String, Any>()
                val h_nombre_usuario = R_nombre_usuario.text.toString()
                val h_correo = R_correo.text.toString()

                hashmap["nombre_usuario"] = h_nombre_usuario
                hashmap["correo"] = h_correo
                hashmap["uid"] = uid
                hashmap["imagen_perfil"] = ""
                hashmap["buscar"] = h_nombre_usuario.toLowerCase()

                reference.updateChildren(hashmap).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        val intent = Intent(this@RegistroActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
                    .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
            }


        } .addOnFailureListener { e ->
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
        }

    }
}