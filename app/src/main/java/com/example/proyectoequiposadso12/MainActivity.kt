package com.example.proyectoequiposadso12


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            progressBar.visibility = View.VISIBLE
            tvResultado.text = ""

            lifecycleScope.launch {
                try {
                    val loginResponse = RetrofitClient.api.login(
                        LoginRequest(username = username, password = password)
                    )

                    if (loginResponse.isSuccessful) {
                        val token = loginResponse.body()?.accessToken

                        if (token != null) {
                            val userResponse = RetrofitClient.api.getCurrentUser("Bearer $token")

                            if (userResponse.isSuccessful) {
                                val user = userResponse.body()
                                tvResultado.text =
                                    "Bienvenido, ${user?.firstName} (${user?.email})"
                            } else {
                                tvResultado.text = "Token rechazado: ${userResponse.code()}"
                            }
                        } else {
                            tvResultado.text = "No se recibió token del servidor"
                        }
                    } else {
                        tvResultado.text = "Credenciales incorrectas: ${loginResponse.code()}"
                    }

                } catch (e: Exception) {
                    tvResultado.text = "Error de conexión: ${e.message}"
                } finally {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}