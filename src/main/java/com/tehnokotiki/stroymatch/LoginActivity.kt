package com.tehnokotiki.stroymatch

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tehnokotiki.stroymatch.network.ApiService
import com.tehnokotiki.stroymatch.network.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etPhone: TextInputEditText = findViewById(R.id.et_phone)
        val etPassword: TextInputEditText = findViewById(R.id.et_password)
        val btnLogin: MaterialButton = findViewById(R.id.btn_login)
        val tvRegister: TextView = findViewById(R.id.tv_register)

        apiService = Retrofit.Builder()
            .baseUrl("http://192.168.1.78:8000") // Убедитесь, что URL правильный
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val registerText = SpannableString("Ещё нет аккаунта? Зарегистрируйтесь!")
        registerText.setSpan(
            ForegroundColorSpan(resources.getColor(com.google.android.material.R.color.design_default_color_primary, null)),
            18, 34,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvRegister.text = registerText

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()

            if (phone.isNotEmpty() && password.isNotEmpty()) {
                login(phone, password)
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(phone: String, password: String) {
        val sharedPreferences = getSharedPreferences("job_matching_prefs", MODE_PRIVATE)
        val role = sharedPreferences.getString("selected_role", null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (role == "worker") {
                    apiService.loginWorker(User(phone, password, "worker"))
                } else {
                    apiService.loginEmployer(User(phone, password, "employer"))
                }

                if (response.isSuccessful) {
                    val user = response.body()
                    with(sharedPreferences.edit()) {
                        putString("phone", phone)
                        putString("password", password)
                        putString("user_data", user.toString()) // Сохраняем данные пользователя
                        apply()
                    }
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Ошибка входа", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
