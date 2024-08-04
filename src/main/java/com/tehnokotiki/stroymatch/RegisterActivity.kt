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

class RegisterActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etPhone: TextInputEditText = findViewById(R.id.et_phone)
        val etPassword: TextInputEditText = findViewById(R.id.et_password)
        val btnRegister: MaterialButton = findViewById(R.id.btn_register)
        val tvLogin: TextView = findViewById(R.id.tv_login)

        apiService = Retrofit.Builder()
            .baseUrl("http://10.2.0.125:8000") // Убедитесь, что URL правильный
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val loginText = SpannableString("Уже есть аккаунт? Войти!")
        loginText.setSpan(
            ForegroundColorSpan(resources.getColor(com.google.android.material.R.color.design_default_color_primary, null)),
            16, 21,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvLogin.text = loginText

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()

            if (phone.isNotEmpty() && password.isNotEmpty()) {
                register(phone, password)
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun register(phone: String, password: String) {
        val sharedPreferences = getSharedPreferences("job_matching_prefs", MODE_PRIVATE)
        val role = sharedPreferences.getString("selected_role", null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (role == "worker") {
                    apiService.registerWorker(User(phone, password, "worker"))
                } else {
                    apiService.registerEmployer(User(phone, password, "employer"))
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
                        startActivity(Intent(this@RegisterActivity, InfoActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
